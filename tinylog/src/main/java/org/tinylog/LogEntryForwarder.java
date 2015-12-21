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

package org.tinylog;

/**
 * External logging API.
 */
public final class LogEntryForwarder {

	private LogEntryForwarder() {
	}

	/**
	 * Test if log entries with the defined severity level will be output.
	 *
	 * @param deepOfStackTrace
	 *            Deep of (additional) stack trace (is needed to find the right caller class)
	 * @param level
	 *            Severity level to test
	 * @return <code>true</code> if defined severity level is enabled, otherwise <code>false</code>
	 */
	public static boolean isEnabled(final int deepOfStackTrace, final Level level) {
		return Logger.isEnabled(deepOfStackTrace + Logger.DEEP_OF_STACK_TRACE + 1, level);
	}

	/**
	 * Test if log entries with the defined severity level will be output.
	 *
	 * @param stackTraceElement
	 *            Stack trace element that contains at least the class name
	 * @param level
	 *            Severity level to test
	 * @return <code>true</code> if defined severity level is enabled, otherwise <code>false</code>
	 */
	public static boolean isEnabled(final StackTraceElement stackTraceElement, final Level level) {
		return Logger.isEnabled(stackTraceElement, level);
	}

	/**
	 * Get the severity level for the caller class.
	 *
	 * @param deepOfStackTrace
	 *            Deep of (additional) stack trace (is needed to find the right caller class)
	 * @return Severity level
	 */
	public static Level getLevel(final int deepOfStackTrace) {
		return Logger.getLevel(deepOfStackTrace + Logger.DEEP_OF_STACK_TRACE + 1);
	}

	/**
	 * Get the severity level for the caller class.
	 *
	 * @param stackTraceElement
	 *            Stack trace element that contains at least the class name
	 * @return Severity level
	 */
	public static Level getLevel(final StackTraceElement stackTraceElement) {
		return Logger.getLevel(stackTraceElement);
	}

	/**
	 * Forward a logging message.
	 *
	 * @param deepOfStackTrace
	 *            Deep of (additional) stack trace (is needed to find the right stack trace element in a stack trace)
	 * @param level
	 *            Severity level
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void forward(final int deepOfStackTrace, final Level level, final Object obj) {
		Logger.output(deepOfStackTrace + Logger.DEEP_OF_STACK_TRACE + 1, level, null, obj, null);
	}

	/**
	 * Forward a logging message.
	 *
	 * @param deepOfStackTrace
	 *            Deep of (additional) stack trace (is needed to find the right stack trace element in a stack trace)
	 * @param level
	 *            Severity level
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void forward(final int deepOfStackTrace, final Level level, final String message, final Object... arguments) {
		Logger.output(deepOfStackTrace + Logger.DEEP_OF_STACK_TRACE + 1, level, null, message, arguments);
	}

	/**
	 * Forward a logging message.
	 *
	 * @param deepOfStackTrace
	 *            Deep of (additional) stack trace (is needed to find the right stack trace element in a stack trace)
	 * @param level
	 *            Severity level
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void forward(final int deepOfStackTrace, final Level level, final Throwable exception, final String message, final Object... arguments) {
		Logger.output(deepOfStackTrace + Logger.DEEP_OF_STACK_TRACE + 1, level, exception, message, arguments);
	}

	/**
	 * Forward a logging message.
	 *
	 * @param stackTraceElement
	 *            Stack trace element for class, method and source information
	 * @param level
	 *            Severity level
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void forward(final StackTraceElement stackTraceElement, final Level level, final Object obj) {
		Logger.output(stackTraceElement, level, null, obj, null);
	}

	/**
	 * Forward a logging message.
	 *
	 * @param stackTraceElement
	 *            Stack trace element for class, method and source information
	 * @param level
	 *            Severity level
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void forward(final StackTraceElement stackTraceElement, final Level level, final String message, final Object... arguments) {
		Logger.output(stackTraceElement, level, null, message, arguments);
	}

	/**
	 * Forward a logging message.
	 *
	 * @param stackTraceElement
	 *            Stack trace element for class, method and source information
	 * @param level
	 *            Severity level
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void forward(final StackTraceElement stackTraceElement, final Level level, final Throwable exception, final String message,
			final Object... arguments) {
		Logger.output(stackTraceElement, level, exception, message, arguments);
	}

}
