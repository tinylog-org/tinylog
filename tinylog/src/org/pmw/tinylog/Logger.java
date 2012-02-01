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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Static class to create log entries.
 * 
 * The default logging level is {@link org.pmw.tinylog.ELoggingLevel#INFO} which ignores trace and debug log entries.
 * 
 * An {@link org.pmw.tinylog.ILoggingWriter} must be set to create any output.
 */
public final class Logger {

	private static final String DEFAULT_LOGGING_FORMAT = "{date} [{thread}] {method}\n{level}: {message}";
	private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final Pattern NEW_LINE_REPLACER = Pattern.compile("\r\n|\\\\r\\\\n|\n|\\\\n|\r|\\\\r");

	private static volatile int maxLoggingStackTraceElements = 40;
	private static volatile ILoggingWriter loggingWriter = new ConsoleLoggingWriter();
	private static volatile ELoggingLevel loggingLevel = ELoggingLevel.INFO;
	private static volatile String loggingFormat = DEFAULT_LOGGING_FORMAT;
	private static volatile Locale locale = Locale.getDefault();
	private static volatile List<Token> loggingEntryTokens = parse(loggingFormat);

	static {
		readProperties();
	}

	private Logger() {
	}

	/**
	 * Register a logging writer to output the created log entries.
	 * 
	 * @param writer
	 *            Logging writer to add (can be <code>null</code> to disable any output)
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
	 * Returns the format pattern for log entries.
	 * 
	 * @return Format pattern for log entries.
	 */
	public static String getLoggingFormat() {
		return loggingFormat;
	}

	/**
	 * Sets the format pattern for log entries.
	 * <code>"{date:yyyy-MM-dd HH:mm:ss} [{thread}] {method}\n{level}: {message}"</code> is the default format pattern.
	 * The date format pattern is compatible with {@link SimpleDateFormat}.
	 * 
	 * @param format
	 *            Format pattern for log entries (or <code>null</code> to reset to default)
	 * 
	 * @see SimpleDateFormat
	 */
	public static void setLoggingFormat(final String format) {
		if (format == null) {
			loggingFormat = DEFAULT_LOGGING_FORMAT;
		} else {
			loggingFormat = format;
		}
		loggingEntryTokens = parse(loggingFormat);
	}

	/**
	 * Gets the locale for message format.
	 * 
	 * @return Locale for message format
	 * 
	 * @see MessageFormat#getLocale()
	 */
	public static Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the locale for message format.
	 * 
	 * @param locale
	 *            Locale for message format
	 * 
	 * @see MessageFormat#setLocale(Locale)
	 */
	public static void setLocale(final Locale locale) {
		Logger.locale = locale;
	}

	/**
	 * Returns the limit of stack traces for exceptions.
	 * 
	 * @return The limit of stack traces
	 */
	public static int getMaxStackTraceElements() {
		return maxLoggingStackTraceElements;
	}

	/**
	 * Sets the limit of stack traces for exceptions (default is 40). Set "-1" for no limitation and "0" to disable any
	 * stack traces.
	 * 
	 * @param maxStackTraceElements
	 *            Limit of stack traces
	 */
	public static void setMaxStackTraceElements(final int maxStackTraceElements) {
		if (maxStackTraceElements < 0) {
			Logger.maxLoggingStackTraceElements = Integer.MAX_VALUE;
		} else {
			Logger.maxLoggingStackTraceElements = maxStackTraceElements;
		}
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

	private static void readProperties() {
		String level = System.getProperty("tinylog.level");
		if (level != null && !level.isEmpty()) {
			try {
				setLoggingLevel(ELoggingLevel.valueOf(level.toUpperCase()));
			} catch (IllegalArgumentException ex) {
				// Ignore
			}
		}

		String format = System.getProperty("tinylog.format");
		if (format != null && !format.isEmpty()) {
			setLoggingFormat(format);
		}

		String localeString = System.getProperty("tinylog.locale");
		if (localeString != null && !localeString.isEmpty()) {
			String[] localeArray = localeString.split("_", 3);
			if (localeArray.length == 1) {
				setLocale(new Locale(localeArray[0]));
			} else if (localeArray.length == 2) {
				setLocale(new Locale(localeArray[0], localeArray[1]));
			} else if (localeArray.length >= 3) {
				setLocale(new Locale(localeArray[0], localeArray[1], localeArray[2]));
			}
		}

		String stacktace = System.getProperty("tinylog.stacktrace");
		if (stacktace != null && !stacktace.isEmpty()) {
			try {
				int limit = Integer.parseInt(stacktace);
				setMaxStackTraceElements(limit);
			} catch (NumberFormatException ex) {
				// Ignore
			}
		}

		String writer = System.getProperty("tinylog.writer");
		if (writer != null && !writer.isEmpty()) {
			if (writer.equalsIgnoreCase("null")) {
				setWriter(null);
			} else if (writer.equalsIgnoreCase("console")) {
				setWriter(new ConsoleLoggingWriter());
			} else if (writer.equalsIgnoreCase("file")) {
				String filename = System.getProperty("tinylog.writer.file");
				if (filename != null && !filename.isEmpty()) {
					try {
						File file = new File(filename);
						setWriter(new FileLoggingWriter(file));
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}
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
					text = new MessageFormat(message, locale).format(arguments);
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
			return "<unknown>()";
		}
	}

	private static String createEntry(final String threadName, final String methodName, final Date time, final ELoggingLevel level, final Throwable exception,
			final String text) {
		StringBuilder builder = new StringBuilder();

		for (Token token : loggingEntryTokens) {
			switch (token.getType()) {
				case THREAD:
					builder.append(threadName);
					break;
				case METHOD:
					builder.append(methodName);
					break;
				case LOGGING_LEVEL:
					builder.append(level);
					break;
				case DATE:
					DateFormat formatter = (DateFormat) token.getData();
					String format;
					synchronized (formatter) {
						format = formatter.format(time);
					}
					builder.append(format);
					break;
				case MESSAGE:
					if (text != null) {
						builder.append(text);
					}
					if (exception != null) {
						if (text != null) {
							builder.append(": ");
						}
						int countLoggingStackTraceElements = maxLoggingStackTraceElements;
						if (countLoggingStackTraceElements == 0) {
							builder.append(":");
							builder.append(exception.getMessage());
						} else {
							builder.append(getPrintedException(exception, countLoggingStackTraceElements));
						}
					}
					break;
				default:
					builder.append(token.getData());
					break;
			}
		}
		builder.append(NEW_LINE);

		return builder.toString();
	}

	private static List<Token> parse(final String format) {
		List<Token> tokens = new ArrayList<Token>();
		char[] chars = format.toCharArray();

		int start = 0;
		int openMarkers = 0;
		for (int i = 0; i < chars.length; ++i) {
			char c = chars[i];
			if (c == '{') {
				if (openMarkers == 0 && start < i) {
					tokens.add(getToken(format.substring(start, i)));
					start = i;
				}
				++openMarkers;
			} else if (openMarkers > 0 && c == '}') {
				--openMarkers;
				if (openMarkers == 0) {
					tokens.add(getToken(format.substring(start, i + 1)));
					start = i + 1;
				}
			}
		}

		if (start < chars.length - 1) {
			tokens.add(getToken(format.substring(start, chars.length)));
		}

		return tokens;
	}

	private static Token getToken(final String text) {
		if ("{thread}".equals(text)) {
			return new Token(EToken.THREAD);
		} else if ("{method}".equals(text)) {
			return new Token(EToken.METHOD);
		} else if ("{level}".equals(text)) {
			return new Token(EToken.LOGGING_LEVEL);
		} else if ("{message}".equals(text)) {
			return new Token(EToken.MESSAGE);
		} else if (text.startsWith("{date") && text.endsWith("}")) {
			String dateFormatPattern;
			if (text.length() > 6) {
				dateFormatPattern = text.substring(6, text.length() - 1);
			} else {
				dateFormatPattern = DEFAULT_DATE_FORMAT_PATTERN;
			}
			return new Token(EToken.DATE, new SimpleDateFormat(dateFormatPattern));
		} else {
			return new Token(EToken.PLAIN_TEXT, NEW_LINE_REPLACER.matcher(text).replaceAll(NEW_LINE));
		}
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
		int length = Math.max(1, Math.min(stackTrace.length, countStackTraceElements));
		for (int i = 0; i < length; ++i) {
			builder.append(NEW_LINE);
			builder.append('\t');
			builder.append("at ");
			builder.append(stackTrace[i]);
		}

		if (stackTrace.length > length) {
			builder.append(NEW_LINE);
			builder.append('\t');
			builder.append("...");
			return builder.toString();
		}

		Throwable cause = exception.getCause();
		if (cause != null) {
			builder.append(NEW_LINE);
			builder.append("Caused by: ");
			builder.append(getPrintedException(cause, countStackTraceElements + length));
		}

		return builder.toString();
	}

}
