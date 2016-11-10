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

package org.tinylog.backend.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.Level;
import org.tinylog.backend.LogEntry;

/**
 * Fluent API for creating a {@link LogEntry}.
 */
public final class LogEntryBuilder {

	private Date date;
	private String processId;
	private Thread thread;
	private Map<String, String> context = new HashMap<>();
	private String className;
	private String methodName;
	private String fileName;
	private int lineNumber = -1;
	private String tag;
	private Level level;
	private String message;
	private Throwable exception;

	/** */
	public LogEntryBuilder() {
	}

	/**
	 * Sets the data and time when this log entry was issued.
	 *
	 * @param date
	 *            Date and time of issue
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder date(final Date date) {
		this.date = date;
		return this;
	}

	/**
	 * Sets the process ID.
	 *
	 * @param id
	 *            Process ID
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder processId(final String id) {
		this.processId = id;
		return this;
	}

	/**
	 * Sets the thread that has issued this log entry.
	 *
	 * @param thread
	 *            Issuing thread
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder thread(final Thread thread) {
		this.thread = thread;
		return this;
	}

	/**
	 * Adds an entry to the thread context mapping.
	 *
	 * @param key
	 *            Key of entry
	 * @param value
	 *            Value of entry
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder context(final String key, final String value) {
		context.put(key, value);
		return this;
	}

	/**
	 * Sets the name of class in which this log entry has been issued.
	 *
	 * @param name
	 *            Name of class
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder className(final String name) {
		this.className = name;
		return this;
	}

	/**
	 * Sets the name of method in which this log entry has been issued.
	 *
	 * @param name
	 *            Name of method
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder methodName(final String name) {
		this.methodName = name;
		return this;
	}

	/**
	 * Sets the name of source file in which this log entry has been issued.
	 *
	 * @param name
	 *            Name of source file
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder fileName(final String name) {
		this.fileName = name;
		return this;
	}

	/**
	 * Sets the line number in source file where this log entry has been issued.
	 *
	 * @param number
	 *            Line number in source file
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder lineNumber(final int number) {
		this.lineNumber = number;
		return this;
	}

	/**
	 * Sets the logger tag of this log entry.
	 *
	 * @param tag
	 *            Logger tag
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder tag(final String tag) {
		this.tag = tag;
		return this;
	}

	/**
	 * Sets the severity level of this log entry.
	 *
	 * @param level
	 *            Severity level
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder level(final Level level) {
		this.level = level;
		return this;
	}

	/**
	 * Sets the text message of this log entry.
	 *
	 * @param message
	 *            Text message
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder message(final String message) {
		this.message = message;
		return this;
	}

	/**
	 * Sets the catched exception or throwable associated with this log entry.
	 *
	 * @param exception
	 *            Associated catched exception or throwable
	 * @return Actual log entry builder
	 */
	public LogEntryBuilder exception(final Throwable exception) {
		this.exception = exception;
		return this;
	}

	/**
	 * Creates a new log entry.
	 *
	 * @return Created log entry
	 */
	public LogEntry create() {
		return new LogEntry(date, processId, thread, context, className, methodName, fileName, lineNumber, tag, level, message, exception);
	}

}
