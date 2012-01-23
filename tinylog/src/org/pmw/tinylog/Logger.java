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

package org.pmw.tinylog;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Static class to create log entries.
 * 
 * The default logging level is {@link ELoggingLevel#INFO} which ignores trace and debug log entries.
 * 
 * An {@link ILoggingWriter} must be registered to create any output.
 */
public final class Logger {

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final int MAX_STACK_TRACE_ELEMENTS = 40;
	private static final String NEW_LINE = System.getProperty("line.separator");

	private static volatile ILoggingWriter loggingWriter = null;
	private static volatile ELoggingLevel loggingLevel = ELoggingLevel.INFO;

	private Logger() {
	}

	/**
	 * Register a logging writer to output the created log entries.
	 * 
	 * @param writer
	 *            Logging writer to add
	 */
	public static void setWriter(final ILoggingWriter writer) {
		loggingWriter = writer;
	}

	/**
	 * Returns the current logging level.
	 * 
	 * @return The current logging level
	 */
	public static ELoggingLevel getLoggingLevel() {
		return loggingLevel;
	}

	/**
	 * Change the logging level. The logger creates only log entries for the current logging level or higher.
	 * 
	 * @param level
	 *            New logging level
	 */
	public static void setLoggingLevel(final ELoggingLevel level) {
		loggingLevel = level;
	}

	/**
	 * Create a trace log entry.
	 * 
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void trace(final String message, final Object... arguments) {
		output(ELoggingLevel.TRACE, null, message, arguments);
	}

	/**
	 * Create a trace log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void trace(final Throwable exception, final String message, final Object... arguments) {
		output(ELoggingLevel.TRACE, exception, message, arguments);
	}

	/**
	 * Create a trace log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void trace(final Throwable exception) {
		output(ELoggingLevel.TRACE, exception, null);
	}

	/**
	 * Create a debug log entry.
	 * 
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void debug(final String message, final Object... arguments) {
		output(ELoggingLevel.DEBUG, null, message, arguments);
	}

	/**
	 * Create a debug log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void debug(final Throwable exception, final String message, final Object... arguments) {
		output(ELoggingLevel.DEBUG, exception, message, arguments);
	}

	/**
	 * Create a debug log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void debug(final Throwable exception) {
		output(ELoggingLevel.DEBUG, exception, null);
	}

	/**
	 * Create an info log entry.
	 * 
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void info(final String message, final Object... arguments) {
		output(ELoggingLevel.INFO, null, message, arguments);
	}

	/**
	 * Create an info log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void info(final Throwable exception, final String message, final Object... arguments) {
		output(ELoggingLevel.INFO, exception, message, arguments);
	}

	/**
	 * Create an info log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void info(final Throwable exception) {
		output(ELoggingLevel.INFO, exception, null);
	}

	/**
	 * Create a warning log entry.
	 * 
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void warn(final String message, final Object... arguments) {
		output(ELoggingLevel.WARNING, null, message, arguments);
	}

	/**
	 * Create a warning log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void warn(final Throwable exception, final String message, final Object... arguments) {
		output(ELoggingLevel.WARNING, exception, message, arguments);
	}

	/**
	 * Create a warning log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void warn(final Throwable exception) {
		output(ELoggingLevel.WARNING, exception, null);
	}

	/**
	 * Create an error log entry.
	 * 
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void error(final String message, final Object... arguments) {
		output(ELoggingLevel.ERROR, null, message, arguments);
	}

	/**
	 * Create an error log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void error(final Throwable exception, final String message, final Object... arguments) {
		output(ELoggingLevel.ERROR, exception, message, arguments);
	}

	/**
	 * Create an error log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void error(final Throwable exception) {
		output(ELoggingLevel.ERROR, exception, null);
	}

	private static void output(final ELoggingLevel level, final Throwable exception, final String message, final Object... arguments) {
		ILoggingWriter currentWriter = loggingWriter;
		if (currentWriter != null && loggingLevel.ordinal() <= level.ordinal()) {
			try {
				String threadName = Thread.currentThread().getName();
				String methodName = getMethodName();
				Date now = new Date();

				String text;
				if (message != null) {
					text = MessageFormat.format(message, arguments);
				} else {
					text = null;
				}

				String logEntry = createEntry(threadName, methodName, now, level, exception, text);
				currentWriter.write(level, logEntry);
			} catch (Exception ex) {
				error(ex, "Could not create log entry");
			}
		}
	}

	private static String getMethodName() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		if (stackTraceElements.length > 4) {
			StackTraceElement stackTraceElement = stackTraceElements[4];
			return stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "()";
		} else {
			return null;
		}
	}

	private static String createEntry(final String threadName, final String methodName, final Date time, final ELoggingLevel level, final Throwable exception,
			final String text) {
		StringBuilder builder = new StringBuilder();

		builder.append(new SimpleDateFormat(DATE_TIME_FORMAT).format(time));
		builder.append(" [");
		builder.append(threadName);
		builder.append("] ");
		builder.append(methodName);
		builder.append(NEW_LINE);

		builder.append(level);
		if (text != null) {
			builder.append(": ");
			builder.append(text);
		}
		if (exception != null) {
			builder.append(": ");
			appendStackTrace(builder, exception, 0);
		} else {
			builder.append(NEW_LINE);
		}

		return builder.toString();
	}

	private static void appendStackTrace(final StringBuilder builder, final Throwable exception, final int countStackTraceElements) {
		builder.append(exception.getClass().getName());

		String message = exception.getMessage();
		if (message != null) {
			builder.append(": ");
			builder.append(message);
		}
		builder.append(NEW_LINE);

		StackTraceElement[] stackTrace = exception.getStackTrace();
		int length = Math.max(1, Math.min(stackTrace.length, MAX_STACK_TRACE_ELEMENTS - countStackTraceElements));
		for (int i = 0; i < length; ++i) {
			builder.append("\tat ");
			builder.append(stackTrace[i]);
			builder.append(NEW_LINE);
		}

		if (stackTrace.length > length) {
			builder.append("\t...");
			builder.append(NEW_LINE);
			return;
		}

		Throwable cause = exception.getCause();
		if (cause != null) {
			builder.append("Caused by: ");
			appendStackTrace(builder, cause, countStackTraceElements + length);
		}
	}

}
