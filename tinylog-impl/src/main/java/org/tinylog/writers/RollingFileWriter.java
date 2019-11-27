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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.tinylog.Level;
import org.tinylog.configuration.ServiceLoader;
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
	private final int backups;
	private final boolean buffered;
	private final boolean writingThread;
	private final Charset charset;

	private ByteArrayWriter writer;

	/**
	 * @throws FileNotFoundException
	 *             Log file does not exist or cannot be opened for any other reason
	 * @throws IllegalArgumentException
	 *             A property has an invalid value or is missing in configuration
	 */
	public RollingFileWriter() throws FileNotFoundException {
		this(Collections.emptyMap());
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
		backups = properties.containsKey("backups") ? Integer.parseInt(properties.get("backups")) : -1;

		List<File> files = path.getAllFiles();

		String fileName;
		boolean append;

		if (files.size() > 0 && path.isValid(files.get(0))) {
			fileName = files.get(0).getPath();
			if (canBeContinued(fileName, policies)) {
				append = true;
				deleteBackups(files.subList(1, files.size()), backups);
			} else {
				fileName = path.resolve();
				append = false;
				deleteBackups(files, backups);
			}
		} else {
			fileName = path.resolve();
			append = false;
		}

		charset = getCharset(properties);
		buffered = Boolean.parseBoolean(properties.get("buffered"));
		writingThread = Boolean.parseBoolean(properties.get("writingthread"));
		writer = createByteArrayWriter(fileName, append, buffered, false, false);
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
	public void close() throws IOException {
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

			String fileName = path.resolve();
			deleteBackups(path.getAllFiles(), backups);
			writer = createByteArrayWriter(fileName, false, buffered, false, false);

			for (Policy policy : policies) {
				policy.reset();
			}
		}

		writer.write(data, data.length);
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
	 */
	private void internalClose() throws IOException {
		writer.close();
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

			ServiceLoader<Policy> loader = new ServiceLoader<Policy>(Policy.class, String.class);
			List<Policy> policies = new ArrayList<Policy>();
			for (String entry : property.split(",")) {
				int separator = entry.indexOf(':');
				if (separator == -1) {
					policies.add(loader.create(entry, (String) null));
				} else {
					String name = entry.substring(0, separator).trim();
					String argument = entry.substring(separator + 1).trim();
					policies.add(loader.create(name, argument));
				}
			}
			return policies;
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
