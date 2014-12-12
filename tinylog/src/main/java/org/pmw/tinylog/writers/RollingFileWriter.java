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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.InternalLogger;
import org.pmw.tinylog.LogEntry;
import org.pmw.tinylog.labelers.CountLabeler;
import org.pmw.tinylog.labelers.FilePair;
import org.pmw.tinylog.labelers.Labeler;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.StartupPolicy;

/**
 * Writes log entries to a file like {@link org.pmw.tinylog.writers.FileWriter FileWriter} but keeps backups of old
 * logging files.
 */
@PropertiesSupport(name = "rollingfile", properties = { @Property(name = "filename", type = String.class), @Property(name = "backups", type = int.class),
		@Property(name = "buffered", type = boolean.class, optional = true), @Property(name = "label", type = Labeler.class, optional = true),
		@Property(name = "policies", type = Policy[].class, optional = true), @Property(name = "listeners", type = RollingListener[].class, optional = true) })
public final class RollingFileWriter implements Writer {

	private static final int BUFFER_SIZE = 64 * 1024;

	private final String filename;
	private final int backups;
	private final boolean buffered;
	private final Labeler labeler;
	private final List<? extends Policy> policies;
	private final List<RollingListener> listeners;

	private final Object mutex;
	private File file;
	private OutputStream stream;

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
		this(filename, backups, false, (Labeler) null, (Policy[]) null, (RollingListener[]) null);
	}

	/**
	 * Rolling log files once at startup.
	 *
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 *
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int backups, final boolean buffered) {
		this(filename, backups, buffered, (Labeler) null, (Policy[]) null, (RollingListener[]) null);
	}

	/**
	 * Rolling log files once at startup.
	 *
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param labeler
	 *            Labeler for naming backups
	 *
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int backups, final Labeler labeler) {
		this(filename, backups, false, labeler, (Policy[]) null, (RollingListener[]) null);
	}

	/**
	 * Rolling log files once at startup.
	 *
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * @param labeler
	 *            Labeler for naming backups
	 *
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public RollingFileWriter(final String filename, final int backups, final boolean buffered, final Labeler labeler) {
		this(filename, backups, buffered, labeler, (Policy[]) null, (RollingListener[]) null);
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
		this(filename, backups, false, (Labeler) null, policies, (RollingListener[]) null);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * @param policies
	 *            Rollover strategies
	 */
	public RollingFileWriter(final String filename, final int backups, final boolean buffered, final Policy... policies) {
		this(filename, backups, buffered, (Labeler) null, policies, (RollingListener[]) null);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param labeler
	 *            Labeler for naming backups
	 * @param policies
	 *            Rollover strategies
	 */
	public RollingFileWriter(final String filename, final int backups, final Labeler labeler, final Policy... policies) {
		this(filename, backups, false, labeler, policies, (RollingListener[]) null);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * @param labeler
	 *            Labeler for naming backups
	 * @param policies
	 *            Rollover strategies
	 */
	public RollingFileWriter(final String filename, final int backups, final boolean buffered, final Labeler labeler, final Policy... policies) {
		this(filename, backups, buffered, labeler, policies, (RollingListener[]) null);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param labeler
	 *            Labeler for naming backups
	 * @param policies
	 *            Rollover strategies
	 * @param listeners
	 *            Rollover listeners
	 */
	RollingFileWriter(final String filename, final int backups, final Labeler labeler, final Policy[] policies, final RollingListener[] listeners) {
		this(filename, backups, false, labeler, policies, listeners);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * @param labeler
	 *            Labeler for naming backups
	 * @param policies
	 *            Rollover strategies
	 * @param listeners
	 *            Rollover listeners
	 */
	RollingFileWriter(final String filename, final int backups, final boolean buffered, final Labeler labeler, final Policy[] policies,
			final RollingListener[] listeners) {
		this.mutex = new Object();
		this.filename = filename;
		this.backups = Math.max(0, backups);
		this.buffered = buffered;
		this.labeler = labeler == null ? new CountLabeler() : labeler;
		this.policies = policies == null || policies.length == 0 ? Arrays.asList(new StartupPolicy()) : Arrays.asList(policies);
		this.listeners = listeners == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(listeners));
	}

	/**
	 * Add a rolling listener.
	 *
	 * @param listener
	 *            Rolling listener
	 */
	public void addListener(final RollingListener listener) {
		synchronized (mutex) {
			listeners.add(listener);
		}
	}

	/**
	 * Remove a rolling listener.
	 *
	 * @param listener
	 *            Rolling listener
	 */
	public void removeListener(final RollingListener listener) {
		synchronized (mutex) {
			listeners.remove(listener);
		}
	}
	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.RENDERED_LOG_ENTRY);
	}

	/**
	 * Get the filename of the current log file.
	 *
	 * @return Filename of the current log file
	 */
	public String getFilename() {
		synchronized (mutex) {
			return file == null ? filename : file.getAbsolutePath();
		}
	}

	/**
	 * Determine whether buffered writing is enabled.
	 *
	 * @return <code>true</code> if buffered writing is enabled, otherwise <code>false</code>
	 */
	public boolean isBuffered() {
		return buffered;
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
	 * Get the labeler for naming backups.
	 *
	 * @return Labeler for naming backups
	 */
	public Labeler getLabeler() {
		return labeler;
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
	public void init(final Configuration configuration) throws IOException {
		synchronized (mutex) {
			labeler.init(configuration);
			file = labeler.getLogFile(new File(filename));

			for (RollingListener listener : listeners) {
				try {
					listener.startup(file);
				} catch (Exception ex) {
					InternalLogger.error(ex);
				}
			}

			for (Policy policy : policies) {
				policy.init(configuration);
			}
			for (Policy policy : policies) {
				if (!policy.check(file)) {
					resetPolicies();
					roll();
					break;
				}
			}

			if (buffered) {
				stream = new BufferedOutputStream(new FileOutputStream(file, true), BUFFER_SIZE);
			} else {
				stream = new FileOutputStream(file, true);
			}

			VMShutdownHook.register(this);
		}
	}

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		synchronized (mutex) {
			if (!checkPolicies(logEntry.getRenderedLogEntry())) {
				stream.close();
				roll();
				if (buffered) {
					stream = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
				} else {
					stream = new FileOutputStream(file);
				}
			}
			stream.write(logEntry.getRenderedLogEntry().getBytes());
		}
	}

	@Override
	public void flush() throws IOException {
		if (buffered) {
			synchronized (mutex) {
				stream.flush();
			}
		}
	}

	/**
	 * Close the log file.
	 *
	 * @throws IOException
	 *             Failed to close the log file
	 */
	@Override
	public void close() throws IOException {
		synchronized (mutex) {
			VMShutdownHook.unregister(this);
			stream.close();

			for (RollingListener listener : listeners) {
				try {
					listener.shutdown(file);
				} catch (Exception ex) {
					InternalLogger.error(ex);
				}
			}
		}
	}

	private boolean checkPolicies(final String logEntry) {
		for (Policy policy : policies) {
			if (!policy.check(logEntry)) {
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

	private void roll() throws IOException {
		FilePair files = labeler.roll(file, backups);
		file = files.getFile();

		for (RollingListener listener : listeners) {
			try {
				listener.rolled(files.getBackup(), file);
			} catch (Exception ex) {
				InternalLogger.error(ex);
			}
		}
	}

}
