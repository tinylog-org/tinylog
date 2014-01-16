/*
 * Copyright 2014 Martin Winandy
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

package org.pmw.tinylog.util;

import java.util.Date;

import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.writers.LogEntry;

/**
 * Fluent API to create and fill {@link LogEntry}.
 */
public final class LogEntryBuilder {

	private Date date = null;
	private String processId = null;
	private Thread thread = null;
	private String className = null;
	private String method = null;
	private String file = null;
	private int lineNumber = -1;
	private LoggingLevel level = null;
	private String message = null;
	private Throwable exception = null;
	private String renderedLogEntry = null;

	/**
	 * Set the current date.
	 * 
	 * @param date
	 *            Current date
	 * @return The current log entry builder
	 */
	public LogEntryBuilder date(final Date date) {
		this.date = date;
		return this;
	}

	/**
	 * Set the ID of the process (pid).
	 * 
	 * @param processId
	 *            ID of the process
	 * @return The current log entry builder
	 */
	public LogEntryBuilder processId(final String processId) {
		this.processId = processId;
		return this;
	}

	/**
	 * Set the current thread.
	 * 
	 * @param thread
	 *            Current thread
	 * @return The current log entry builder
	 */
	public LogEntryBuilder thread(final Thread thread) {
		this.thread = thread;
		return this;
	}

	/**
	 * Set the fully qualified class name of the caller.
	 * 
	 * @param className
	 *            Fully qualified class name of the caller
	 * @return The current log entry builder
	 */
	public LogEntryBuilder className(final String className) {
		this.className = className;
		return this;
	}

	/**
	 * Set the method name of the caller.
	 * 
	 * @param method
	 *            Method name of the caller
	 * @return The current log entry builder
	 */
	public LogEntryBuilder method(final String method) {
		this.method = method;
		return this;
	}

	/**
	 * Set the source filename of the caller.
	 * 
	 * @param file
	 *            Source filename of the caller
	 * @return The current log entry builder
	 */
	public LogEntryBuilder file(final String file) {
		this.file = file;
		return this;
	}

	/**
	 * Set the line number of calling.
	 * 
	 * @param lineNumber
	 *            Line number of calling
	 * @return The current log entry builder
	 */
	public LogEntryBuilder lineNumber(final int lineNumber) {
		this.lineNumber = lineNumber;
		return this;
	}

	/**
	 * Set the logging level.
	 * 
	 * @param level
	 *            Logging level
	 * @return The current log entry builder
	 */
	public LogEntryBuilder level(final LoggingLevel level) {
		this.level = level;
		return this;
	}

	/**
	 * Set the message of the logging event.
	 * 
	 * @param message
	 *            Message of the logging event
	 * @return The current log entry builder
	 */
	public LogEntryBuilder message(final String message) {
		this.message = message;
		return this;
	}

	/**
	 * Set the exception of the log entry.
	 * 
	 * @param exception
	 *            Exception of the log entry
	 * @return The current log entry builder
	 */
	public LogEntryBuilder exception(final Throwable exception) {
		this.exception = exception;
		return this;
	}

	/**
	 * Set the rendered log entry.
	 * 
	 * @param renderedLogEntry
	 *            Rendered log entry
	 * @return The current log entry builder
	 */
	public LogEntryBuilder renderedLogEntry(final String renderedLogEntry) {
		this.renderedLogEntry = renderedLogEntry;
		return this;
	}

	/**
	 * Get the created log entry.
	 * 
	 * @return Created log entry
	 */
	public LogEntry create() {
		return new LogEntry(date, processId, thread, className, method, file, lineNumber, level, message, exception, renderedLogEntry);
	}
}
