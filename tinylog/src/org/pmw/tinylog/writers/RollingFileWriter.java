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

import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.labellers.CountLabeller;
import org.pmw.tinylog.labellers.Labeller;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.StartupPolicy;

/**
 * Writes log entries to a file like {@link org.pmw.tinylog.writers.FileWriter} but keeps backups of old logging files.
 */
public class RollingFileWriter implements LoggingWriter {

	private final int backups;
	private final Labeller labeller;
	private final List<? extends Policy> policies;

	private File file;
	private BufferedWriter writer;

	/**
	 * Rolling log files once at startup.
	 * 
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @throws IOException
	 *             Failed to open or create the log file
	 * 
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int backups) throws IOException {
		this(filename, backups, new StartupPolicy());
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param policies
	 *            Rollover strategies
	 * @throws IOException
	 *             Failed to open or create the log file
	 */
	public RollingFileWriter(final String filename, final int backups, final Policy... policies) throws IOException {
		this(filename, backups, new CountLabeller(), policies);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param labeller
	 *            Labeller for naming backups
	 * @param policies
	 *            Rollover strategies
	 * @throws IOException
	 *             Failed to open or create the log file
	 */
	public RollingFileWriter(final String filename, final int backups, final Labeller labeller, final Policy... policies) throws IOException {
		this.backups = Math.max(0, backups);
		this.labeller = labeller;
		this.policies = Arrays.asList(policies);
		this.file = labeller.getLogFile(new File(filename));
		initCkeckPolicies();
		this.writer = new BufferedWriter(new java.io.FileWriter(file, true));
	}

	/**
	 * Returns the name of the writer.
	 * 
	 * @return "rollingfile"
	 */
	public static String getName() {
		return "rollingfile";
	}

	/**
	 * Returns the supported properties for this writer.
	 * 
	 * The rolling file logging writer needs a "filename" and the number of backups ("backups") plus optionally a
	 * labeller ("labeling") and rollover strategies ("policies").
	 * 
	 * @return Three string arrays with and without the properties "naming" and "policies"
	 */
	public static String[][] getSupportedProperties() {
		return new String[][] { new String[] { "filename", "backups" }, new String[] { "filename", "backups", "policies" },
				new String[] { "filename", "backups", "labeling", "policies" } };
	}

	@Override
	public final void write(final LoggingLevel level, final String logEntry) {
		synchronized (this) {
			if (!checkPolicies(level, logEntry)) {
				try {
					writer.close();
				} catch (IOException ex) {
					ex.printStackTrace(System.err);
				}
				file = labeller.roll(file, backups);
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
				file = labeller.roll(file, backups);
				return;
			}
		}
	}

	private boolean checkPolicies(final LoggingLevel level, final String logEntry) {
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

	private void write(final String logEntry) {
		try {
			writer.write(logEntry);
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

}
