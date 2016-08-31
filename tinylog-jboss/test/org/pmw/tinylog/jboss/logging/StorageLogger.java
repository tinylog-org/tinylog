/*
 * Copyright 2016 Martin Winandy
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

package org.pmw.tinylog.jboss.logging;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

/**
 * Logger stores all log entries in a list.
 */
public final class StorageLogger extends Logger {

	private static final long serialVersionUID = 1L;

	private StorageLogger parent;
	private org.pmw.tinylog.Level minimumLevel;
	private List<LogEntry> entries;

	/**
	 * @param parent
	 *            Parent logger
	 * @param name
	 *            Name of logger
	 */
	public StorageLogger(final StorageLogger parent, final String name) {
		super(name);

		this.parent = parent;
		this.minimumLevel = null;
		this.entries = new ArrayList<>();
	}

	/**
	 * Get and remove all stored log entries.
	 * 
	 * @return Stored log entries
	 */
	public List<LogEntry> consumeLogEntries() {
		try {
			return entries;
		} finally {
			entries = new ArrayList<>();
		}
	}

	@Override
	public boolean isEnabled(final Level level) {
		if (minimumLevel == null) {
			return parent == null ? true : parent.isEnabled(level);
		} else {
			switch (level) {
				case FATAL:
				case ERROR:
					if (minimumLevel == org.pmw.tinylog.Level.ERROR) {
						return true;
					}

				case WARN:
					if (minimumLevel == org.pmw.tinylog.Level.WARNING) {
						return true;
					}

				case INFO:
					if (minimumLevel == org.pmw.tinylog.Level.INFO) {
						return true;
					}

				case DEBUG:
					if (minimumLevel == org.pmw.tinylog.Level.DEBUG) {
						return true;
					}

				case TRACE:
					return minimumLevel == org.pmw.tinylog.Level.TRACE;

				default:
					throw new IllegalArgumentException("Unknown logging level: " + level);
			}
		}
	}

	/**
	 * Set a severity level for this logger. Only log entries with the same or a higher level will be stored.
	 * 
	 * @param minimumLevel
	 *            New severity level or <code>null</code> for using severity level from parent logger
	 */
	public void setLevel(final org.pmw.tinylog.Level minimumLevel) {
		this.minimumLevel = minimumLevel;
	}

	@Override
	protected void doLog(final Level level, final String loggerClassName, final Object message, final Object[] parameters, final Throwable exception) {
		if (isEnabled(level)) {
			if (parameters == null || parameters.length == 0) {
				entries.add(new LogEntry(level, message, exception));
			} else {
				entries.add(new LogEntry(level, MessageFormat.format(message.toString(), parameters), exception));
			}
		}
	}

	@Override
	protected void doLogf(final Level level, final String loggerClassName, final String message, final Object[] parameters, final Throwable exception) {
		if (isEnabled(level)) {
			if (parameters == null || parameters.length == 0) {
				entries.add(new LogEntry(level, message, exception));
			} else {
				entries.add(new LogEntry(level, String.format(message, parameters), exception));
			}
		}
	}

}
