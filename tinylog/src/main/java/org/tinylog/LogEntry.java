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

package org.tinylog;

import java.util.Date;
import java.util.Map;

/**
 * Log entry object for {@link org.tinylog.writers.Writer#write(LogEntry) Writer.write(LogEntry)}.
 */
public final class LogEntry {

	private final Date date;
	private final String processId;
	private final Thread thread;
	private final Map<String, String> context;
	private final String className;
	private final String methodName;
	private final String filename;
	private final int lineNumber;
	private final Level level;
	private final String message;
	private final Throwable exception;
	private String renderedLogEntry;

	/**
	 * @param date
	 *            The current date
	 * @param processId
	 *            The ID of the process (pid)
	 * @param thread
	 *            The current thread
	 * @param context
	 *            Thread-based mapped diagnostic context
	 * @param className
	 *            The fully qualified class name
	 * @param methodName
	 *            The method name
	 * @param filename
	 *            The source filename
	 * @param lineNumber
	 *            The line number
	 * @param level
	 *            The severity level
	 * @param message
	 *            The message of the logging event
	 * @param exception
	 *            The exception of the log entry
	 */
	public LogEntry(final Date date, final String processId, final Thread thread, final Map<String, String> context, final String className,
			final String methodName, final String filename, final int lineNumber, final Level level, final String message, final Throwable exception) {
		this.date = date;
		this.processId = processId;
		this.thread = thread;
		this.context = context;
		this.className = className;
		this.methodName = methodName;
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.level = level;
		this.message = message;
		this.exception = exception;
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
	 * Get the thread-based mapped diagnostic context.
	 *
	 * @return Mapped diagnostic context
	 */
	public Map<String, String> getContext() {
		return context;
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
	 * Get the severity level.
	 *
	 * @return Severity level
	 */
	public Level getLevel() {
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

	/**
	 * Set the rendered log entry.
	 *
	 * @param renderedLogEntry
	 *            Rendered log entry
	 */
	void setRenderedLogEntry(final String renderedLogEntry) {
		this.renderedLogEntry = renderedLogEntry;
	}

}
