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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.labellers.CountLabeller;
import org.pmw.tinylog.labellers.Labeller;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.StartupPolicy;

/**
 * Writes log entries to a file like {@link org.pmw.tinylog.writers.FileWriter} but keeps backups of old logging files.
 */
@PropertiesSupport(name = "rollingfile", properties = { @Property(name = "filename", type = String.class), @Property(name = "backups", type = int.class),
		@Property(name = "label", type = Labeller.class, optional = true), @Property(name = "policies", type = Policy[].class, optional = true) })
public final class RollingFileWriter implements LoggingWriter {

	private final String filename;
	private final int backups;
	private final Labeller labeller;
	private final List<? extends Policy> policies;

	private File file;
	private java.io.FileWriter writer;

	/**
	 * Rolling log files once at startup.
	 * 
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * 
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int backups) {
		this(filename, backups, null, (Policy[]) null);
	}

	/**
	 * Rolling log files once at startup.
	 * 
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param labeller
	 *            Labeller for naming backups
	 * 
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int backups, final Labeller labeller) {
		this(filename, backups, labeller, (Policy[]) null);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param policies
	 *            Rollover strategies
	 */
	public RollingFileWriter(final String filename, final int backups, final Policy... policies) {
		this(filename, backups, null, policies);
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
	 */
	public RollingFileWriter(final String filename, final int backups, final Labeller labeller, final Policy... policies) {
		this.filename = filename;
		this.backups = Math.max(0, backups);
		this.labeller = labeller == null ? new CountLabeller() : labeller;
		this.policies = policies == null || policies.length == 0 ? Arrays.asList(new StartupPolicy()) : Arrays.asList(policies);
	}

	/**
	 * Get the filename of the current log file.
	 * 
	 * @return Filename of the current log file
	 */
	public String getFilename() {
		synchronized (this) {
			return file == null ? filename : file.getAbsolutePath();
		}
	}

	/**
	 * Get the maximum number of backups.
	 * 
	 * @return Maximum number of backups
	 */
	public int getNumberOfBackups() {
		return backups;
	}

	/**
	 * Get the labeller for naming backups.
	 * 
	 * @return Labeller for naming backups
	 */
	public Labeller getLabeller() {
		return labeller;
	}

	/**
	 * Get the rollover strategies.
	 * 
	 * @return Rollover strategies
	 */
	public List<? extends Policy> getPolicies() {
		return Collections.unmodifiableList(policies);
	}

	@Override
	public void init() throws IOException {
		file = labeller.getLogFile(new File(filename));
		initCheckPolicies();
		writer = new java.io.FileWriter(file, true);
	}

	@Override
	public void write(final LoggingLevel level, final String logEntry) throws IOException {
		synchronized (this) {
			if (!checkPolicies(level, logEntry)) {
				try {
					writer.close();
				} catch (IOException ex) {
					ex.printStackTrace(System.err);
				}
				file = labeller.roll(file, backups);
				writer = new java.io.FileWriter(file);
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
	public void close() throws IOException {
		synchronized (this) {
			writer.close();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

	private void initCheckPolicies() {
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
