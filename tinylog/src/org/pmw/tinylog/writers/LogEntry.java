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

package org.pmw.tinylog.writers;

import java.util.Date;

import org.pmw.tinylog.LoggingLevel;

/**
 * Log entry object for {@link org.pmw.tinylog.writers.LoggingWriter#write(LogEntry) LoggingWriter.write(LogEntry)}.
 */
public final class LogEntry {

	private final Date date;
	private final String processId;
	private final Thread thread;
	private final String className;
	private final String method;
	private final String file;
	private final int lineNumber;
	private final LoggingLevel level;
	private final String message;
	private final Throwable exception;
	private final String renderedLogEntry;

	/**
	 * @param date
	 *            The current date
	 * @param processId
	 *            The ID of the process (pid)
	 * @param thread
	 *            The current thread
	 * @param className
	 *            The fully qualified class name of the caller
	 * @param method
	 *            The method name of the caller
	 * @param file
	 *            The source filename of the caller
	 * @param lineNumber
	 *            The line number of calling
	 * @param level
	 *            The logging level
	 * @param message
	 *            The message of the logging event
	 * @param exception
	 *            The exception of the log entry
	 * @param renderedLogEntry
	 *            The rendered log entry
	 */
	public LogEntry(final Date date, final String processId, final Thread thread, final String className, final String method, final String file,
			final int lineNumber, final LoggingLevel level, final String message, final Throwable exception, final String renderedLogEntry) {
		this.date = date;
		this.processId = processId;
		this.thread = thread;
		this.className = className;
		this.method = method;
		this.file = file;
		this.lineNumber = lineNumber;
		this.level = level;
		this.message = message;
		this.exception = exception;
		this.renderedLogEntry = renderedLogEntry;
	}

	/**
	 * Get the current date.
	 * 
	 * @return Current date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Get the ID of the process (pid).
	 * 
	 * @return ID of the process
	 */
	public String getProcessId() {
		return processId;
	}

	/**
	 * Get the current thread.
	 * 
	 * @return Current thread
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * Get the fully qualified class name of the caller.
	 * 
	 * @return Fully qualified class name of the caller
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Get the method name of the caller.
	 * 
	 * @return Method name of the caller
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Get the source filename of the caller.
	 * 
	 * @return Source filename of the caller
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Get the line number of calling.
	 * 
	 * @return Line number of calling
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Get the logging level.
	 * 
	 * @return Logging level
	 */
	public LoggingLevel getLevel() {
		return level;
	}

	/**
	 * Get the message of the logging event.
	 * 
	 * @return Message of the logging event
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Get the exception of the log entry.
	 * 
	 * @return Exception of the log entry
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Get the rendered log entry.
	 * 
	 * @return Rendered log entry
	 */
	public String getRenderedLogEntry() {
		return renderedLogEntry;
	}

}
