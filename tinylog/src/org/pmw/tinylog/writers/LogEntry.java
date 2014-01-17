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
	private final String methodName;
	private final String filename;
	private final int lineNumber;
	private final LoggingLevel loggingLevel;
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
	 *            The fully qualified class name
	 * @param methodName
	 *            The method name
	 * @param filename
	 *            The source filename
	 * @param lineNumber
	 *            The line number
	 * @param loggingLevel
	 *            The logging level
	 * @param message
	 *            The message of the logging event
	 * @param exception
	 *            The exception of the log entry
	 * @param renderedLogEntry
	 *            The rendered log entry
	 */
	public LogEntry(final Date date, final String processId, final Thread thread, final String className, final String methodName, final String filename,
			final int lineNumber, final LoggingLevel loggingLevel, final String message, final Throwable exception, final String renderedLogEntry) {
		this.date = date;
		this.processId = processId;
		this.thread = thread;
		this.className = className;
		this.methodName = methodName;
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.loggingLevel = loggingLevel;
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
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Get the source filename of the caller.
	 * 
	 * @return Source filename of the caller
	 */
	public String getFilename() {
		return filename;
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
	public LoggingLevel getLoggingLevel() {
		return loggingLevel;
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
