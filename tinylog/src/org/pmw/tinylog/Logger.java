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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Static class to create log entries.
 * 
 * The default logging level is {@link org.pmw.tinylog.ELoggingLevel#INFO} which ignores trace and debug log entries.
 * 
 * An {@link org.pmw.tinylog.ILoggingWriter} must be set to create any output.
 */
public final class Logger {

	private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final int MAX_STACK_TRACE_ELEMENTS = 4;
	private static final String NEW_LINE = System.getProperty("line.separator");

	private static volatile ILoggingWriter loggingWriter = null;
	private static volatile ELoggingLevel loggingLevel = ELoggingLevel.INFO;
	private static volatile String loggingFormat = "{date} [{thread}] {method}\n{level}: {message}";
	private static volatile List<String> loggingEntryTokens = parse(loggingFormat);

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
	 * Sets the format pattern for log entries.
	 * <code>"{date:yyyy-MM-dd HH:mm:ss} [{thread}] {method}\n{level}: {message}"</code> is the default format pattern.
	 * The date format pattern is compatible with {@link SimpleDateFormat}.
	 * 
	 * @param format
	 *            Format pattern for log entries
	 * 
	 * @see SimpleDateFormat
	 */
	public static void setLoggingFormat(final String format) {
		loggingFormat = format;
		loggingEntryTokens = parse(loggingFormat);
	}

	/**
	 * Returns the format pattern for log entries.
	 * 
	 * @return Format pattern for log entries.
	 */
	public static String getLoggingFormat() {
		return loggingFormat;
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

		for (String token : loggingEntryTokens) {
			if ("{thread}".equals(token)) {
				builder.append(threadName);
			} else if ("{method}".equals(token)) {
				builder.append(methodName);
			} else if ("{level}".equals(token)) {
				builder.append(level);
			} else if ("{message}".equals(token)) {
				if (text != null) {
					builder.append(text);
				}
				if (exception != null) {
					if (text != null) {
						builder.append(": ");
					}
					builder.append(getPrintedException(exception, 0));
				}
			} else if (token.startsWith("{date") && token.endsWith("}")) {
				String dateFormatPattern;
				if (token.length() > 6) {
					dateFormatPattern = token.substring(6, token.length() - 1);
				} else {
					dateFormatPattern = DEFAULT_DATE_FORMAT_PATTERN;
				}
				builder.append(new SimpleDateFormat(dateFormatPattern).format(time));
			} else {
				builder.append(token);
			}
		}
		builder.append("\n");

		return builder.toString().replaceAll("\n", NEW_LINE);
	}

	private static List<String> parse(final String format) {
		List<String> tokens = new ArrayList<String>();
		char[] chars = format.toCharArray();

		int start = 0;
		int openMarkers = 0;
		for (int i = 0; i < chars.length; ++i) {
			char c = chars[i];
			if (c == '{') {
				if (openMarkers == 0 && start < i) {
					tokens.add(format.substring(start, i));
					start = i;
				}
				++openMarkers;
			} else if (openMarkers > 0 && c == '}') {
				--openMarkers;
				if (openMarkers == 0) {
					tokens.add(format.substring(start, i + 1));
					start = i + 1;
				}
			}
		}

		if (start < chars.length - 1) {
			tokens.add(format.substring(start, chars.length));
		}

		return tokens;
	}

	private static String getPrintedException(final Throwable exception, final int countStackTraceElements) {
		StringBuilder builder = new StringBuilder();
		builder.append(exception.getClass().getName());

		String message = exception.getMessage();
		if (message != null) {
			builder.append(": ");
			builder.append(message);
		}

		StackTraceElement[] stackTrace = exception.getStackTrace();
		int length = Math.max(1, Math.min(stackTrace.length, MAX_STACK_TRACE_ELEMENTS - countStackTraceElements));
		for (int i = 0; i < length; ++i) {
			builder.append("\n\tat ");
			builder.append(stackTrace[i]);
		}

		if (stackTrace.length > length) {
			builder.append("\n\t...");
			return builder.toString();
		}

		Throwable cause = exception.getCause();
		if (cause != null) {
			builder.append("\nCaused by: ");
			builder.append(getPrintedException(cause, countStackTraceElements + length));
		}

		return builder.toString();
	}

}
