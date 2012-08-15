/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.pmw.tinylog.ELoggingLevel;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.StartupPolicy;

/**
 * Writes log entries to a file like {@link FileWriter} but keeps backups of old logging files.
 */
public class RollingFileWriter implements LoggingWriter {

	private final File file;
	private final int maxBackups;
	private final List<? extends Policy> policies;
	private BufferedWriter writer;

	/**
	 * Rolling log files once at startup (= {@link #RollingFileWriter(String, int, Policy...)
	 * RollingFileWriter(filename, maxBackups, new StartupPolicy())}).
	 * 
	 * @param filename
	 *            Filename of the log file
	 * @param maxBackups
	 *            Number of backups
	 * @throws IOException
	 *             Failed to open or create the log file
	 * 
	 * @see StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int maxBackups) throws IOException {
		this(filename, maxBackups, new StartupPolicy());
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param maxBackups
	 *            Number of backups
	 * @param policies
	 *            Rollover strategies
	 * @throws IOException
	 *             Failed to open or create the log file
	 */
	public RollingFileWriter(final String filename, final int maxBackups, final Policy... policies) throws IOException {
		this.file = new File(filename);
		this.maxBackups = Math.max(0, maxBackups);
		this.policies = Arrays.asList(policies);
		initCkeckPolicies();
		this.writer = new BufferedWriter(new java.io.FileWriter(file, true));
	}

	/**
	 * Returns the supported properties for this writer.
	 * 
	 * The rolling file logging writer needs a "filename" and the number of backups ("maxBackups") plus optionally
	 * rollover strategies ("policies").
	 * 
	 * @return Two string arrays with and without the property "policies"
	 */
	public static String[][] getSupportedProperties() {
		return new String[][] { new String[] { "filename", "maxBackups" }, new String[] { "filename", "maxBackups", "policies" } };
	}

	@Override
	public final void write(final ELoggingLevel level, final String logEntry) {
		synchronized (this) {
			if (!checkPolicies(level, logEntry)) {
				try {
					writer.close();
				} catch (IOException ex) {
					ex.printStackTrace(System.err);
				}
				roll();
				try {
					writer = new BufferedWriter(new java.io.FileWriter(file));
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
			write(logEntry);
		}
	}

	/**
	 * Close the log file.
	 * 
	 * @throws IOException
	 *             Failed to close the log file
	 */
	public final void close() throws IOException {
		synchronized (this) {
			writer.close();
		}
	}

	@Override
	protected final void finalize() throws Throwable {
		close();
	}

	private void initCkeckPolicies() {
		for (Policy policy : policies) {
			if (!policy.initCheck(file)) {
				resetPolicies();
				roll();
				return;
			}
		}
	}

	private boolean checkPolicies(final ELoggingLevel level, final String logEntry) {
		for (Policy policy : policies) {
			if (!policy.check(level, logEntry)) {
				resetPolicies();
				return false;
			}
		}
		return true;
	}

	private void resetPolicies() {
		for (Policy policy : policies) {
			policy.reset();
		}
	}

	private void roll() {
		if (file.exists()) {
			String filenameWithoutExtension;
			String filenameExtension;

			String path = file.getPath();
			String name = file.getName();
			int index = name.indexOf('.', 1);
			if (index > 0) {
				filenameWithoutExtension = path.substring(0, (path.length() - name.length()) + index);
				filenameExtension = name.substring(index);
			} else {
				filenameWithoutExtension = path;
				filenameExtension = "";
			}

			roll(file, 0, filenameWithoutExtension, filenameExtension);
		}
	}

	private void roll(final File baseFile, final int number, final String filenameWithoutExtension, final String filenameExtension) {
		File targetFile = new File(filenameWithoutExtension + "." + number + filenameExtension);
		if (targetFile.exists()) {
			roll(targetFile, number + 1, filenameWithoutExtension, filenameExtension);
		}
		if (number < maxBackups) {
			baseFile.renameTo(targetFile);
		} else {
			baseFile.delete();
		}
	}

	private void write(final String logEntry) {
		try {
			writer.write(logEntry);
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

}
