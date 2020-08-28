/*
 * Copyright 2018 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.writers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.tinylog.Level;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.converters.FileConverter;
import org.tinylog.converters.NopFileConverter;
import org.tinylog.core.LogEntry;
import org.tinylog.path.DynamicPath;
import org.tinylog.policies.Policy;
import org.tinylog.policies.StartupPolicy;
import org.tinylog.provider.InternalLogger;
import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.writers.raw.ByteArrayWriter;

/**
 * Writer for outputting log entries to rolling log files. Rollover strategies can be defined via {@link Policy
 * policies} and the output can be buffered for improving performance. The path to the log file can contain one or more
 * patterns that will be resolved at runtime.
 */
public final class RollingFileWriter extends AbstractFormatPatternWriter {

	private final DynamicPath path;
	private final List<Policy> policies;
	private final FileConverter converter;
	private final int backups;
	private final boolean buffered;
	private final boolean writingThread;
	private final DynamicPath linkToLatest;
	private final Charset charset;

	private ByteArrayWriter writer;

	/**
	 * @throws FileNotFoundException
	 *             Log file does not exist or cannot be opened for any other reason
	 * @throws IllegalArgumentException
	 *             A property has an invalid value or is missing in configuration
	 */
	public RollingFileWriter() throws FileNotFoundException {
		this(Collections.<String, String>emptyMap());
	}

	/**
	 * @param properties
	 *            Configuration for writer
	 *
	 * @throws FileNotFoundException
	 *             Log file does not exist or cannot be opened for any other reason
	 * @throws IllegalArgumentException
	 *             A property has an invalid value or is missing in configuration
	 */
	public RollingFileWriter(final Map<String, String> properties) throws FileNotFoundException {
		super(properties);

		path = new DynamicPath(getFileName(properties));
		policies = createPolicies(properties.get("policies"));
		converter = createConverter(properties.get("convert"));
		backups = properties.containsKey("backups") ? Integer.parseInt(properties.get("backups")) : -1;
		linkToLatest = properties.containsKey("latest") ? new DynamicPath(properties.get("latest")) : null;

		String suffix = converter.getBackupSuffix();
		List<File> files = getAllFilesWithoutLinks(null);

		String fileName;
		boolean append;

		if (files.size() > 0 && path.isValid(files.get(0))) {
			fileName = files.get(0).getPath();
			if (canBeContinued(fileName, policies)) {
				deleteBackups(suffix == null ? files.subList(1, files.size()) : getAllFilesWithoutLinks(suffix), backups);
				append = true;
			} else {
				converter.open(fileName);
				deleteBackups(suffix == null ? files : getAllFilesWithoutLinks(suffix), backups);
				converter.close();

				fileName = path.resolve();
				append = false;
			}
		} else {
			fileName = path.resolve();
			append = false;
		}

		charset = getCharset(properties);
		buffered = Boolean.parseBoolean(properties.get("buffered"));
		writingThread = Boolean.parseBoolean(properties.get("writingthread"));
		writer = createByteArrayWriterAndLinkLatest(fileName, append, buffered, false, false);
	}

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		byte[] data = render(logEntry).getBytes(charset);
		if (writingThread) {
			internalWrite(data);
		} else {
			synchronized (writer) {
				internalWrite(data);
			}
		}
	}

	@Override
	public void flush() throws IOException {
		if (writingThread) {
			internalFlush();
		} else {
			synchronized (writer) {
				internalFlush();
			}
		}
	}

	@Override
	public void close() throws IOException, InterruptedException {
		if (writingThread) {
			internalClose();
		} else {
			synchronized (writer) {
				internalClose();
			}
		}
	}

	/**
	 * Outputs a passed byte array unsynchronized.
	 *
	 * @param data
	 *            Byte array to output
	 * @throws IOException
	 *             Writing failed
	 */
	private void internalWrite(final byte[] data) throws IOException {
		if (!canBeContinued(data, policies)) {
			writer.close();

			String suffix = converter.getBackupSuffix();
			deleteBackups(getAllFilesWithoutLinks(suffix), suffix == null ? backups : backups - 1);

			converter.close();

			String fileName = path.resolve();
			writer = createByteArrayWriterAndLinkLatest(fileName, false, buffered, false, false);

			for (Policy policy : policies) {
				policy.reset();
			}
		}

		byte[] convertedData = converter.write(data);
		writer.write(convertedData, convertedData.length);
	}

	/**
	 * Outputs buffered log entries immediately unsynchronized.
	 *
	 * @throws IOException
	 *             Flushing failed
	 */
	private void internalFlush() throws IOException {
		writer.flush();
	}

	/**
	 * Closes the writer unsynchronized.
	 *
	 * @throws IOException
	 *             Closing failed
	 * @throws InterruptedException
	 *             Interrupted while waiting for the converter
	 */
	private void internalClose() throws IOException, InterruptedException {
		writer.close();

		String suffix = converter.getBackupSuffix();
		if (suffix != null) {
			deleteBackups(getAllFilesWithoutLinks(suffix), backups - 1);
		}

		converter.close();
		converter.shutdown();
	}

	/**
	 * Gets all files from {@link DynamicPath} but without links.
	 *
	 * @param backupSuffix
	 *            Optional file extension for backup files (can be {@code null})
	 * @return Found files without links
	 */
	@IgnoreJRERequirement
	private List<File> getAllFilesWithoutLinks(final String backupSuffix) {
		List<File> files = path.getAllFiles(backupSuffix);
		if (linkToLatest != null && !RuntimeProvider.isAndroid()) {
			files.remove(new File(linkToLatest.resolve()).getAbsoluteFile());
		}
		return files;
	}

	/**
	 * Creates a {@link ByteArrayWriter} for a file and creates a link to it if linking is enabled.
	 *
	 * @param fileName
	 *            Name of file to open for writing
	 * @param append
	 *            An already existing file should be continued
	 * @param buffered
	 *            Output should be buffered
	 * @param threadSafe
	 *            Created writer must be thread-safe
	 * @param shared
	 *            Output file is shared with other processes
	 * @return Writer for writing to passed file
	 * @throws FileNotFoundException
	 *             File does not exist or cannot be opened for any other reason
	 */
	@IgnoreJRERequirement
	private ByteArrayWriter createByteArrayWriterAndLinkLatest(final String fileName, final boolean append, final boolean buffered,
		final boolean threadSafe, final boolean shared) throws FileNotFoundException {
		converter.open(fileName);
		ByteArrayWriter writer = createByteArrayWriter(fileName, append, buffered, threadSafe, shared);
		if (linkToLatest != null) {
			File logFile = new File(fileName);
			File linkFile = new File(linkToLatest.resolve());
			if (!RuntimeProvider.isAndroid()) {
				try {
					Path logPath = logFile.toPath();
					Path linkPath = linkFile.toPath();
					Files.deleteIfExists(linkPath);
					Files.createLink(linkPath, logPath);
				} catch (IOException ex) {
					InternalLogger.log(Level.ERROR, ex, "Failed to create link '" + linkFile + "'");
				}
			} else {
				InternalLogger.log(Level.WARN, "Cannot create link to latest log file on Android");
			}
		}
		return writer;
	}

	/**
	 * Creates policies from a nullable string.
	 *
	 * @param property
	 *            Nullable string with policies to create
	 * @return Created policies
	 */
	private static List<Policy> createPolicies(final String property) {
		if (property == null || property.isEmpty()) {
			return Collections.<Policy>singletonList(new StartupPolicy(null));
		} else {
			if (RuntimeProvider.getProcessId() == Long.MIN_VALUE) {
				java.util.ServiceLoader.load(Policy.class); // Workaround for ProGuard (see issue #126)
			}

			return new ServiceLoader<Policy>(Policy.class, String.class).createList(property);
		}
	}

	/**
	 * Creates the file converter from a nullable string.
	 *
	 * @param property
	 *            Nullable string with converter to create
	 * @return Created file converter
	 */
	private static FileConverter createConverter(final String property) {
		if (property == null || property.isEmpty()) {
			return new NopFileConverter();
		} else {
			if (RuntimeProvider.getProcessId() == Long.MIN_VALUE) {
				java.util.ServiceLoader.load(FileConverter.class); // Workaround for ProGuard (see issue #126)
			}

			FileConverter converter = new ServiceLoader<FileConverter>(FileConverter.class).create(property);
			return converter == null ? new NopFileConverter() : converter;
		}
	}

	/**
	 * Checks if an already existing log file can be continued.
	 *
	 * @param fileName
	 *            Log file
	 * @param policies
	 *            Policies that should be applied
	 * @return {@code true} if the passed log file can be continued, {@code false} if a new log file should be started
	 */
	private static boolean canBeContinued(final String fileName, final List<Policy> policies) {
		boolean result = true;
		for (Policy policy : policies) {
			result &= policy.continueExistingFile(fileName);
		}
		return result;
	}

	/**
	 * Checks if a new log entry can be still written to the current log file.
	 *
	 * @param data
	 *            Log entry
	 * @param policies
	 *            Policies that should be applied
	 * @return {@code true} if the current log file can be continued, {@code false} if a new log file should be started
	 */
	private static boolean canBeContinued(final byte[] data, final List<Policy> policies) {
		boolean result = true;
		for (Policy policy : policies) {
			result &= policy.continueCurrentFile(data);
		}
		return result;
	}

	/**
	 * Deletes old log files.
	 *
	 * @param files
	 *            All existing log files
	 * @param count
	 *            Number of log files to keep
	 */
	private static void deleteBackups(final List<File> files, final int count) {
		if (count >= 0) {
			for (int i = files.size() - Math.max(0, files.size() - count); i < files.size(); ++i) {
				if (!files.get(i).delete()) {
					InternalLogger.log(Level.WARN, "Failed to delete log file '" + files.get(i).getAbsolutePath() + "'");
				}
			}
		}
	}

}
