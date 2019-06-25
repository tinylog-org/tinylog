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

package org.tinylog.core;

import java.util.Map;

import org.tinylog.Level;
import org.tinylog.runtime.Timestamp;

/**
 * Immutable holder of all required data for writing a log entry.
 *
 * <p>
 * A writer can only depend on values that have been requested. All other values are may {@code null}.
 * </p>
 */
public final class LogEntry {

	private final Timestamp timestamp;
	private final Thread thread;
	private final Map<String, String> context;
	private final String className;
	private final String methodName;
	private final String fileName;
	private final int lineNumber;
	private final String tag;
	private final Level level;
	private final String message;
	private final Throwable exception;

	/**
	 * @param timestamp
	 *            Date and time of issuing this log entry
	 * @param thread
	 *            Thread that has issued this log entry
	 * @param context
	 *            Actual thread context mapping
	 * @param className
	 *            Name of class in which this log entry has been issued
	 * @param methodName
	 *            Name of method in which this log entry has been issued
	 * @param fileName
	 *            Name of source file in which this log entry has been issued
	 * @param lineNumber
	 *            Line number in source file where this log entry has been issued
	 * @param tag
	 *            Tag from logger if this log entry has been issued by a tagged logger
	 * @param level
	 *            Severity level of this log entry
	 * @param message
	 *            Text message of this log entry
	 * @param exception
	 *            Caught exception or throwable associated with this log entry
	 */
	public LogEntry(final Timestamp timestamp, final Thread thread, final Map<String, String> context, final String className,
		final String methodName, final String fileName, final int lineNumber, final String tag, final Level level,
		final String message, final Throwable exception) {
		this.timestamp = timestamp;
		this.thread = thread;
		this.context = context;
		this.className = className;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.tag = tag;
		this.level = level;
		this.message = message;
		this.exception = exception;
	}

	/**
	 * Gets the data and time when this log entry was issued.
	 *
	 * @return Date and time of issue
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * Gets the thread that has issued this log entry.
	 *
	 * @return Issuing thread
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * Gets the thread context mapping as it was active while issuing this log entry.
	 *
	 * @return Thread context mapping
	 */
	public Map<String, String> getContext() {
		return context;
	}

	/**
	 * Gets the name of the class in which this log entry has been issued.
	 *
	 * @return Name of class
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Gets the name of the method in which this log entry has been issued.
	 *
	 * @return Name of method
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Gets the name of the source file in which this log entry has been issued.
	 *
	 * @return Name of source file
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Gets the line number in source file where this log entry has been issued.
	 *
	 * @return Line number in source file
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Gets the tag from logger if this log entry has been issued by a tagged logger.
	 *
	 * @return Tag from logger
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Gets the severity level of this log entry.
	 *
	 * @return Severity level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Gets the text message of this log entry.
	 *
	 * @return Text message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the caught exception or throwable associated with this log entry.
	 *
	 * @return Associated caught exception or throwable
	 */
	public Throwable getException() {
		return exception;
	}

}
