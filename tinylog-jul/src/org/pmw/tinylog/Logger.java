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

package org.pmw.tinylog;

import org.pmw.tinylog.runtime.RuntimeDialect;

/**
 * Static class to create log entries.
 */
public final class Logger {

	private static final int DEPTH_OF_STACK_TRACE = 3;

	private static final RuntimeDialect runtime = EnvironmentHelper.getRuntimeDialect();

	private Logger() {
	}

	/**
	 * Get the current global severity level.
	 *
	 * @return Global severity level
	 */
	@SuppressWarnings("deprecation")
	public static Level getLevel() {
		return getLevel(java.util.logging.Logger.global);
	}

	/**
	 * Get the current severity level for a specific package.
	 *
	 * @param packageObject
	 *            Package
	 *
	 * @return Severity level for the package
	 */
	public static Level getLevel(final Package packageObject) {
		return getLevel(java.util.logging.Logger.getLogger(packageObject.getName()));
	}

	/**
	 * Get the current severity level for a specific class.
	 *
	 * @param classObject
	 *            Name of the class
	 *
	 * @return Severity level for the class
	 */
	public static Level getLevel(final Class<?> classObject) {
		return getLevel(java.util.logging.Logger.getLogger(classObject.getName()));
	}

	/**
	 * Get the current severity level for a specific package or class.
	 *
	 * @param packageOrClass
	 *            Name of the package respectively class
	 *
	 * @return severity level for the package respectively class
	 */
	public static Level getLevel(final String packageOrClass) {
		return getLevel(java.util.logging.Logger.getLogger(packageOrClass));
	}

	/**
	 * Create a trace log entry.
	 *
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void trace(final Object obj) {
		log(java.util.logging.Level.FINER, null, obj, null);
	}

	/**
	 * Create a trace log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void trace(final String message) {
		log(java.util.logging.Level.FINER, null, message, null);
	}

	/**
	 * Create a trace log entry. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void trace(final String message, final Object... arguments) {
		log(java.util.logging.Level.FINER, null, message, arguments);
	}

	/**
	 * Create a trace log entry. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void trace(final Throwable exception, final String message, final Object... arguments) {
		log(java.util.logging.Level.FINER, exception, message, arguments);
	}

	/**
	 * Create a trace log entry.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void trace(final Throwable exception) {
		log(java.util.logging.Level.FINER, exception, null, null);
	}

	/**
	 * Create a debug log entry.
	 *
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void debug(final Object obj) {
		log(java.util.logging.Level.FINE, null, obj, null);
	}

	/**
	 * Create a debug log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void debug(final String message) {
		log(java.util.logging.Level.FINE, null, message, null);
	}

	/**
	 * Create a debug log entry. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void debug(final String message, final Object... arguments) {
		log(java.util.logging.Level.FINE, null, message, arguments);
	}

	/**
	 * Create a debug log entry. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void debug(final Throwable exception, final String message, final Object... arguments) {
		log(java.util.logging.Level.FINE, exception, message, arguments);
	}

	/**
	 * Create a debug log entry.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void debug(final Throwable exception) {
		log(java.util.logging.Level.FINE, exception, null, null);
	}

	/**
	 * Create an info log entry.
	 *
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void info(final Object obj) {
		log(java.util.logging.Level.INFO, null, obj, null);
	}

	/**
	 * Create an info log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void info(final String message) {
		log(java.util.logging.Level.INFO, null, message, null);
	}

	/**
	 * Create an info log entry. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void info(final String message, final Object... arguments) {
		log(java.util.logging.Level.INFO, null, message, arguments);
	}

	/**
	 * Create an info log entry. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void info(final Throwable exception, final String message, final Object... arguments) {
		log(java.util.logging.Level.INFO, exception, message, arguments);
	}

	/**
	 * Create an info log entry.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void info(final Throwable exception) {
		log(java.util.logging.Level.INFO, exception, null, null);
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void warn(final Object obj) {
		log(java.util.logging.Level.WARNING, null, obj, null);
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void warn(final String message) {
		log(java.util.logging.Level.WARNING, null, message, null);
	}

	/**
	 * Create a warning log entry. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void warn(final String message, final Object... arguments) {
		log(java.util.logging.Level.WARNING, null, message, arguments);
	}

	/**
	 * Create a warning log entry. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void warn(final Throwable exception, final String message, final Object... arguments) {
		log(java.util.logging.Level.WARNING, exception, message, arguments);
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void warn(final Throwable exception) {
		log(java.util.logging.Level.WARNING, exception, null, null);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void error(final Object obj) {
		log(java.util.logging.Level.SEVERE, null, obj, null);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void error(final String message) {
		log(java.util.logging.Level.SEVERE, null, message, null);
	}

	/**
	 * Create an error log entry. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void error(final String message, final Object... arguments) {
		log(java.util.logging.Level.SEVERE, null, message, arguments);
	}

	/**
	 * Create an error log entry. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void error(final Throwable exception, final String message, final Object... arguments) {
		log(java.util.logging.Level.SEVERE, exception, message, arguments);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void error(final Throwable exception) {
		log(java.util.logging.Level.SEVERE, exception, null, null);
	}

	private static void log(final java.util.logging.Level level, final Throwable exception, final Object message, final Object[] arguments) {
		StackTraceElement stackTraceElement = runtime.getStackTraceElement(DEPTH_OF_STACK_TRACE);
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(stackTraceElement.getClassName());

		if (logger.isLoggable(level)) {
			String formattedMessage;
			if (message == null || arguments == null) {
				formattedMessage =  message == null ? null : message.toString();
			} else {
				formattedMessage = MessageFormatter.format(message.toString(), arguments);
			}

			if (exception == null) {
				logger.logp(level, stackTraceElement.getClassName(), stackTraceElement.getMethodName(), formattedMessage);
			} else {
				logger.logp(level, stackTraceElement.getClassName(), stackTraceElement.getMethodName(), formattedMessage, exception);
			}

		}
	}

	private static Level getLevel(final java.util.logging.Logger logger) {
		if (logger.isLoggable(java.util.logging.Level.FINER)) {
			return Level.TRACE;
		} else if (logger.isLoggable(java.util.logging.Level.FINE)) {
			return Level.DEBUG;
		} else if (logger.isLoggable(java.util.logging.Level.INFO)) {
			return Level.INFO;
		} else if (logger.isLoggable(java.util.logging.Level.WARNING)) {
			return Level.WARNING;
		} else if (logger.isLoggable(java.util.logging.Level.SEVERE)) {
			return Level.ERROR;
		} else {
			return Level.OFF;
		}
	}

}
