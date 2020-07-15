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

package org.tinylog.runtime;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * VM runtime specific functionality.
 */
interface RuntimeDialect {

	/**
	 * Checks if running on Android.
	 *
	 * @return {@code true} if running on Android, otherwise {@code false}
	 */
	boolean isAndroid();

	/**
	 * Gets the name of the default writer.
	 *
	 * @return Name of default writer
	 */
	String getDefaultWriter();

	/**
	 * Gets the ID of the current process (pid).
	 *
	 * @return ID of the current process
	 */
	long getProcessId();

	/**
	 * Gets the start time of the Java virtual machine or tinylog.
	 *
	 * @return Start time
	 */
	Timestamp getStartTime();

	/**
	 * Gets the class name of a caller from stack trace.
	 *
	 * @param depth
	 *            Position of caller in stack trace
	 * @return Fully-qualified class name of caller
	 */
	String getCallerClassName(int depth);

	/**
	 * Gets the class name of a caller from stack trace.
	 * 
	 * @param loggerClassName
	 *            Logger class name that should appear before the real caller
	 * @return Fully-qualified class name of caller
	 */
	String getCallerClassName(String loggerClassName);

	/**
	 * Gets the complete stack trace element of a caller from stack trace.
	 *
	 * @param depth
	 *            Position of caller in stack trace
	 * @return Stack trace element of a caller
	 */
	StackTraceElement getCallerStackTraceElement(int depth);

	/**
	 * Gets the complete stack trace element of a caller from stack trace.
	 *
	 * @param loggerClassName
	 *            Logger class name that should appear before the real caller
	 * @return Stack trace element of a caller
	 */
	StackTraceElement getCallerStackTraceElement(String loggerClassName);

	/**
	 * Creates a timestamp with the current date and time.
	 *
	 * @return Timestamp with current date and time
	 */
	Timestamp createTimestamp();

	/**
	 * Creates a formatter for {@link Timestamp Timestamps}.
	 *
	 * @param pattern
	 *            Format pattern that is compatible with {@link DateTimeFormatter}
	 * @param locale
	 *            Locale for formatting
	 * @return Formatter for formatting timestamps
	 */
	TimestampFormatter createTimestampFormatter(String pattern, Locale locale);

}
