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

package org.tinylog.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.Level;
import org.tinylog.core.LogEntry;

/**
 * Fluent API for creating a {@link LogEntry}.
 */
public final class LogEntryBuilder {

	/**
	 * Default date for prefilled log entry builders.
	 */
	public static final Date DEFAULT_DATE = new GregorianCalendar(1985, Calendar.JUNE, 03).getTime();

	/**
	 * Default method name for prefilled log entry builders.
	 */
	public static final String DEFAULT_METHOD = "foo";

	/**
	 * Default severity level for prefilled log entry builders.
	 */
	public static final Level DEFAULT_LEVEL = Level.TRACE;

	/**
	 * Default text message for prefilled log entry builders.
	 */
	public static final String DEFAULT_MESSAGE = "Hello World!";

	private Date date;
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
	private LogEntryBuilder() {
	}

	/**
	 * Creates a new empty instance.
	 *
	 * <p>
	 * Line number is -1 and thread context is empty by default. All other value are {@code null}.
	 * </p>
	 *
	 * @return Empty log entry builder
	 */
	public static LogEntryBuilder empty() {
		return new LogEntryBuilder();
	}

	/**
	 * Creates a new prefilled instance.
	 *
	 * <p>
	 * <table>
	 * <tr>
	 * <td>Date</td>
	 * <td>1985-06-03 ({@link #DEFAULT_DATE})</td>
	 * </tr>
	 * <tr>
	 * <td>Thread</td>
	 * <td>Current thread ({@link Thread#currentThread()})</td>
	 * </tr>
	 * <tr>
	 * <td>Class name</td>
	 * <td>Fully-qualified class name of caller parameter</td>
	 * </tr>
	 * <tr>
	 * <td>Method name</td>
	 * <td>foo ({@link #DEFAULT_METHOD})</td>
	 * </tr>
	 * <tr>
	 * <td>File name</td>
	 * <td>Simple class name of caller plus ".java" file extension</td>
	 * </tr>
	 * <tr>
	 * <td>Level</td>
	 * <td>TRACE ({@link #DEFAULT_LEVEL})</td>
	 * </tr>
	 * <tr>
	 * <td>Message</td>
	 * <td>Hello World! ({@link #DEFAULT_MESSAGE})</td>
	 * </tr>
	 * </table>
	 * </p>
	 *
	 * <p>
	 * Tag and exception are {@code null}, line number is -1 and thread context is empty by default.
	 * </p>
	 *
	 * @param caller
	 *            Caller class
	 * @return Prefilled log entry builder
	 */
	public static LogEntryBuilder prefilled(final Class<?> caller) {
		LogEntryBuilder builder = new LogEntryBuilder();
		builder.date(DEFAULT_DATE);
		builder.thread(Thread.currentThread());
		builder.className(caller.getName());
		builder.methodName(DEFAULT_METHOD);
		builder.fileName(caller.getSimpleName() + ".java");
		builder.level(Level.TRACE);
		builder.message(DEFAULT_MESSAGE);
		return builder;
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
	 * Sets the name of the class in which this log entry has been issued.
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
	 * Sets the name of the method in which this log entry has been issued.
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
	 * Sets the name of the source file in which this log entry has been issued.
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
		return new LogEntry(date, thread, context, className, methodName, fileName, lineNumber, tag, level, message, exception);
	}

}
