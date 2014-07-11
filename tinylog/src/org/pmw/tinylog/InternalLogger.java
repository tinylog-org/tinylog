/*
 * Copyright 2013 Martin Winandy
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

/**
 * Static logger for logging internal problems of tinylog.
 *
 * Log entries will be always output into the console.
 */
public final class InternalLogger {

	private static final String PREFIX_WARNING = "LOGGER WARNING: ";
	private static final String PREFIX_ERROR = "LOGGER ERROR: ";

	private static volatile String lastLogEntry = null;

	private InternalLogger() {
	}

	/**
	 * Log an internal warning.
	 *
	 * @param message
	 *            Text to log
	 */
	public static void warn(final String message) {
		String logEntry = PREFIX_WARNING + message;
		if (!logEntry.equals(lastLogEntry)) {
			System.err.println(logEntry);
			lastLogEntry = logEntry;
		}
	}

	/**
	 * Log an internal warning. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param message
	 *            Formated text to log
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void warn(final String message, final Object... arguments) {
		warn(MessageFormatter.format(message, arguments));
	}

	/**
	 * Log an internal warning.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void warn(final Throwable exception) {
		String message = exception.getMessage();
		if (message == null || message.length() == 0) {
			warn(exception.getClass().getName());
		} else {
			warn(message + " (" + exception.getClass().getName() + ")");
		}
	}

	/**
	 * Log an internal warning.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Text to log
	 */
	public static void warn(final Throwable exception, final String message) {
		String messageOfThrowable = exception.getMessage();
		if (messageOfThrowable == null || messageOfThrowable.length() == 0) {
			warn(message + " (" + exception.getClass().getName() + ")");
		} else {
			warn(message + " (" + exception.getClass().getName() + ": " + messageOfThrowable + ")");
		}
	}

	/**
	 * Log an internal warning. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text to log
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void warn(final Throwable exception, final String message, final Object... arguments) {
		warn(exception, MessageFormatter.format(message, arguments));
	}

	/**
	 * Log an internal error.
	 *
	 * @param message
	 *            Text to log
	 */
	public static void error(final String message) {
		String logEntry = PREFIX_ERROR + message;
		if (!logEntry.equals(lastLogEntry)) {
			System.err.println(logEntry);
			lastLogEntry = logEntry;
		}
	}

	/**
	 * Log an internal error. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param message
	 *            Formated text to log
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void error(final String message, final Object... arguments) {
		error(MessageFormatter.format(message, arguments));
	}

	/**
	 * Log an internal error.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void error(final Throwable exception) {
		String message = exception.getMessage();
		if (message == null || message.length() == 0) {
			error(exception.getClass().getName());
		} else {
			error(message + " (" + exception.getClass().getName() + ")");
		}
	}

	/**
	 * Log an internal error.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Text to log
	 */
	public static void error(final Throwable exception, final String message) {
		String messageOfThrowable = exception.getMessage();
		if (messageOfThrowable == null || messageOfThrowable.length() == 0) {
			error(message + " (" + exception.getClass().getName() + ")");
		} else {
			error(message + " (" + exception.getClass().getName() + ": " + messageOfThrowable + ")");
		}
	}

	/**
	 * Log an internal error. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text to log
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void error(final Throwable exception, final String message, final Object... arguments) {
		error(exception, MessageFormatter.format(message, arguments));
	}

}
