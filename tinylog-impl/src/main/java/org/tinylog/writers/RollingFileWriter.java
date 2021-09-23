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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.tinylog.Level;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.converters.FileConverter;
import org.tinylog.converters.NopFileConverter;
import org.tinylog.core.LogEntry;
import org.tinylog.path.DynamicPath;
import org.tinylog.path.FileTuple;
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
	 * @throws IOException
	 *             Log file cannot be opened for write access
	 * @throws IllegalArgumentException
	 *             A property has an invalid value or is missing in configuration
	 */
	public RollingFileWriter() throws IOException {
		this(Collections.<String, String>emptyMap());
	}

	/**
	 * @param properties
	 *            Configuration for writer
	 *
	 * @throws IOException
	 *             Log file cannot be opened for write access
	 * @throws IllegalArgumentException
	 *             A property has an invalid value or is missing in configuration
	 */
	public RollingFileWriter(final Map<String, String> properties) throws IOException {
		super(properties);

		path = new DynamicPath(getFileName());
		policies = createPolicies(getStringValue("policies"));
		converter = createConverter(getStringValue("convert"));
		backups = properties.containsKey("backups") ? Integer.parseInt(getStringValue("backups")) : -1;
		linkToLatest = properties.containsKey("latest") ? new DynamicPath(getStringValue("latest")) : null;

		List<FileTuple> files = getAllFileTuplesWithoutLinks(converter.getBackupSuffix());
		File latestFile = findLatestLogFile(files);

		if (backups >= 0) {
			deleteBackups(files, backups);
		}

		String fileName;
		boolean append;

		if (latestFile != null && path.isValid(latestFile)) {
			fileName = latestFile.getAbsolutePath();
			if (canBeContinued(fileName, policies)) {
				append = true;
			} else {
				fileName = path.resolve();
				append = false;
			}
		} else {
			fileName = path.resolve();
			append = false;
		}

		charset = getCharset();
		buffered = getBooleanValue("buffered");
		writingThread = getBooleanValue("writingthread");
		writer = createByteArrayWriterAndLinkLatest(fileName, append, buffered, charset);
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
			converter.close();

			String fileName = path.resolve();
			writer = createByteArrayWriterAndLinkLatest(fileName, false, buffered, charset);

			for (Policy policy : policies) {
				policy.reset();
			}

			if (backups >= 0) {
				deleteBackups(getAllFileTuplesWithoutLinks(converter.getBackupSuffix()), backups);
			}
		}

		byte[] convertedData = converter.write(data);
		writer.write(convertedData, 0, convertedData.length);
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
		converter.close();
		converter.shutdown();
	}

	/**
	 * Gets all log files including backups from {@link DynamicPath} but without links.
	 *
	 * @param backupSuffix
	 *            File extension for backup files
	 * @return Found file tuples without links
	 */
	@IgnoreJRERequirement
	private List<FileTuple> getAllFileTuplesWithoutLinks(final String backupSuffix) {
		List<FileTuple> files = path.getAllFiles(backupSuffix);
		if (linkToLatest != null && !RuntimeProvider.isAndroid()) {
			File fileLink = new File(linkToLatest.resolve()).getAbsoluteFile();
			Iterator<FileTuple> iterator = files.iterator();
			while (iterator.hasNext()) {
				if (fileLink.equals(iterator.next().getOriginal())) {
					iterator.remove();
					break;
				}
			}
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
	 * @param charset
	 *            Charset used by the writer
	 * @return Writer for writing to passed file
	 * @throws IOException
	 *             Log file cannot be opened for write access
	 */
	@IgnoreJRERequirement
	private ByteArrayWriter createByteArrayWriterAndLinkLatest(final String fileName, final boolean append,
			final boolean buffered, final Charset charset) throws IOException {
		converter.open(fileName);
		ByteArrayWriter writer = createByteArrayWriter(fileName, append, buffered, false, false, charset);
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
	 * Finds the latest existing original log file.
	 *
	 * @param files
	 *            All original and backup files
	 * @return Found original log file or {@code null} if there are no original log files
	 */
	private static File findLatestLogFile(final List<FileTuple> files) {
		for (FileTuple file : files) {
			if (file.getOriginal().isFile() && (file.getOriginal().equals(file.getBackup()) || !file.getBackup().isFile())) {
				return file.getOriginal();
			}
		}

		return null;
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
	 *            All original and backup files
	 * @param count
	 *            Number of log files to keep
	 */
	private static void deleteBackups(final List<FileTuple> files, final int count) {
		for (int i = count; i < files.size(); ++i) {
			files.get(i).delete();
		}
	}

}
