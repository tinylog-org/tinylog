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

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * Static class to create log entries.
 * 
 * The default logging level is {@link org.pmw.tinylog.LoggingLevel#INFO}, which ignores trace and debug log entries.
 */
public final class Logger {

	static final int DEEP_OF_STACK_TRACE = 3;

	private static final String DEFAULT_LOGGING_FORMAT = "{date} [{thread}] {class}.{method}()\n{level}: {message}";
	private static final String NEW_LINE = System.getProperty("line.separator");

	private static Method stackTraceMethod;
	private static volatile WritingThread writingThread = null;
	private static volatile int maxLoggingStackTraceElements = 40;
	private static volatile LoggingWriter loggingWriter = new ConsoleWriter();
	private static volatile LoggingLevel loggingLevel = LoggingLevel.INFO;
	private static final Map<String, LoggingLevel> packageLoggingLevels = Collections.synchronizedMap(new HashMap<String, LoggingLevel>());
	private static volatile String loggingFormat = DEFAULT_LOGGING_FORMAT;
	private static volatile Locale locale = Locale.getDefault();
	private static volatile List<Token> loggingEntryTokens = Tokenizer.parse(loggingFormat, locale);

	static {
		PropertiesLoader.reload();
		try {
			stackTraceMethod = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
			stackTraceMethod.setAccessible(true);
			stackTraceMethod.invoke(new Throwable(), 0); // Test if method can be invoked
		} catch (Exception ex) {
			stackTraceMethod = null;
		}
	}

	private Logger() {
	}

	/**
	 * Returns the current logging level.
	 * 
	 * @return The current logging level
	 */
	public static LoggingLevel getLoggingLevel() {
		return loggingLevel;
	}

	/**
	 * Change the logging level. The logger creates and outputs only log entries for the current logging level and
	 * higher.
	 * 
	 * @param level
	 *            New logging level
	 */
	public static void setLoggingLevel(final LoggingLevel level) {
		if (level == null) {
			loggingLevel = LoggingLevel.OFF;
		} else {
			loggingLevel = level;
		}
	}

	/**
	 * Returns the logging level for a package.
	 * 
	 * @param packageName
	 *            Name of the package
	 * 
	 * @return The logging level
	 */
	public static LoggingLevel getLoggingLevel(final String packageName) {
		return getLoggingLevelOfPackage(packageName);
	}

	/**
	 * Set the logging level for a package.
	 * 
	 * This will override the default logging level for this package.
	 * 
	 * @param packageName
	 *            Name of the package
	 * @param level
	 *            The logging level (or <code>null</code> to reset it to the default logging level)
	 */
	public static void setLoggingLevel(final String packageName, final LoggingLevel level) {
		if (level == null) {
			packageLoggingLevels.remove(packageName);
		} else {
			packageLoggingLevels.put(packageName, level);
		}
	}

	/**
	 * Reset the logging level for a package (to use the default logging level again).
	 * 
	 * @param packageName
	 *            Name of the package
	 */
	public static void resetLoggingLevel(final String packageName) {
		packageLoggingLevels.remove(packageName);
	}

	/**
	 * Reset all package depending logging levels (to use the default logging level again).
	 */
	public static void resetAllLoggingLevel() {
		packageLoggingLevels.clear();
	}

	/**
	 * Returns the format pattern for log entries.
	 * 
	 * @return Format pattern for log entries
	 */
	public static String getLoggingFormat() {
		return loggingFormat;
	}

	/**
	 * Change the format pattern for log entries.
	 * <code>"{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}()\n{level}: {message}"</code> is the default format
	 * pattern. The date format pattern is compatible with {@link SimpleDateFormat}.
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
		loggingEntryTokens = Tokenizer.parse(loggingFormat, locale);
	}

	/**
	 * Returns the locale, which is used in format patterns for log entries.
	 * 
	 * @return Locale for format patterns
	 */
	public static Locale getLocale() {
		return locale;
	}

	/**
	 * Change the locale, which is used in format patterns for log entries.
	 * 
	 * It will be used e. g. to format numbers and dates.
	 * 
	 * @param locale
	 *            Locale for format patterns
	 */
	public static void setLocale(final Locale locale) {
		if (locale == null) {
			Logger.locale = Locale.getDefault();
		} else {
			Logger.locale = locale;
		}
		loggingEntryTokens = Tokenizer.parse(loggingFormat, Logger.locale);
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
	 * Change the limit of stack traces for exceptions (default is 40). Can be set to "-1" for no limitation and to "0"
	 * to disable any stack traces.
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
	 * Returns the current logging writer.
	 * 
	 * @return The current logging writer
	 */
	public static LoggingWriter getWriter() {
		return loggingWriter;
	}

	/**
	 * Register a logging writer for outputting the created log entries.
	 * 
	 * @param writer
	 *            New logging writer (can be <code>null</code> to disable any output)
	 */
	public static void setWriter(final LoggingWriter writer) {
		loggingWriter = writer;
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
		output(DEEP_OF_STACK_TRACE, LoggingLevel.TRACE, null, message, arguments);
	}

	/**
	 * Start a separate thread with a low priority (2) for writing log entries. This thread will automatically shutdown,
	 * if the main thread is dead.
	 */
	public static void startWritingThread() {
		startWritingThread(WritingThread.DEFAULT_THREAD_TO_OBSERVE, WritingThread.DEFAULT_PRIORITY);
	}

	/**
	 * Start a separate thread for writing log entries. This thread will automatically shutdown, if the observed thread
	 * is dead.
	 * 
	 * @param nameOfThreadToObserve
	 *            Name of the tread to observe (e.g. "main" for the main thread) or <code>null</code> to disable
	 *            automatic shutdown
	 * @param priority
	 *            Priority for the writing thread (must be between {@link Thread#MIN_PRIORITY} and
	 *            {@link Thread#MAX_PRIORITY}).
	 */
	public static synchronized void startWritingThread(final String nameOfThreadToObserve, final int priority) {
		if (writingThread == null) {
			WritingThread thread = new WritingThread(nameOfThreadToObserve);
			thread.setPriority(priority);
			thread.setName("tinylog-WritingThread");
			thread.start();
			writingThread = thread;
		}
	}

	/**
	 * Shutdown the writing thread.
	 * 
	 * @param wait
	 *            <code>true</code> to wait for the successful shutdown, <code>false</code> for an asynchronous shutdown
	 */
	public static synchronized void shutdownWritingThread(final boolean wait) {
		WritingThread thread = writingThread;
		if (thread != null) {
			writingThread = null;
			thread.shutdown();
			if (wait) {
				boolean finished;
				do {
					try {
						thread.join();
						finished = true;
					} catch (InterruptedException ex) {
						finished = false;
					}
				} while (!finished);
			}
		}
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
		output(DEEP_OF_STACK_TRACE, LoggingLevel.TRACE, exception, message, arguments);
	}

	/**
	 * Create a trace log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void trace(final Throwable exception) {
		output(DEEP_OF_STACK_TRACE, LoggingLevel.TRACE, exception, null);
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
		output(DEEP_OF_STACK_TRACE, LoggingLevel.DEBUG, null, message, arguments);
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
		output(DEEP_OF_STACK_TRACE, LoggingLevel.DEBUG, exception, message, arguments);
	}

	/**
	 * Create a debug log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void debug(final Throwable exception) {
		output(DEEP_OF_STACK_TRACE, LoggingLevel.DEBUG, exception, null);
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
		output(DEEP_OF_STACK_TRACE, LoggingLevel.INFO, null, message, arguments);
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
		output(DEEP_OF_STACK_TRACE, LoggingLevel.INFO, exception, message, arguments);
	}

	/**
	 * Create an info log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void info(final Throwable exception) {
		output(DEEP_OF_STACK_TRACE, LoggingLevel.INFO, exception, null);
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
		output(DEEP_OF_STACK_TRACE, LoggingLevel.WARNING, null, message, arguments);
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
		output(DEEP_OF_STACK_TRACE, LoggingLevel.WARNING, exception, message, arguments);
	}

	/**
	 * Create a warning log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void warn(final Throwable exception) {
		output(DEEP_OF_STACK_TRACE, LoggingLevel.WARNING, exception, null);
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
		output(DEEP_OF_STACK_TRACE, LoggingLevel.ERROR, null, message, arguments);
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
		output(DEEP_OF_STACK_TRACE, LoggingLevel.ERROR, exception, message, arguments);
	}

	/**
	 * Create an error log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void error(final Throwable exception) {
		output(DEEP_OF_STACK_TRACE, LoggingLevel.ERROR, exception, null);
	}

	/**
	 * Add a log entry. This method is helpful for adding log entries form logger bridges.
	 * 
	 * @param strackTraceDeep
	 *            Deep of stack trace for finding the class, source line etc.
	 * @param level
	 *            Logging level of the log entry
	 * @param exception
	 *            Exception to log (can be <code>null</code> if there is no exception to log)
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	static void output(final int strackTraceDeep, final LoggingLevel level, final Throwable exception, final String message, final Object... arguments) {
		LoggingWriter currentWriter = loggingWriter;

		if (currentWriter != null) {
			StackTraceElement stackTraceElement = null;
			LoggingLevel activeLoggingLevel = loggingLevel;

			if (!packageLoggingLevels.isEmpty()) {
				stackTraceElement = getStackTraceElement(strackTraceDeep);
				activeLoggingLevel = getLoggingLevelOfClass(stackTraceElement.getClassName());
			}

			if (activeLoggingLevel.ordinal() <= level.ordinal()) {
				String logEntry;
				try {
					logEntry = createLogEntry(strackTraceDeep + 1, level, stackTraceElement, exception, message, arguments);
				} catch (Exception ex) {
					logEntry = createLogEntry(strackTraceDeep + 1, LoggingLevel.ERROR, stackTraceElement, ex, "Could not created log entry");
				}

				WritingThread currentWritingThread = writingThread;
				if (currentWritingThread == null) {
					currentWriter.write(activeLoggingLevel, logEntry);
				} else {
					currentWritingThread.putLogEntry(currentWriter, activeLoggingLevel, logEntry);
				}
			}
		}
	}

	private static String createLogEntry(final int strackTraceDeep, final LoggingLevel level, final StackTraceElement createdStackTraceElement,
			final Throwable exception, final String message, final Object... arguments) {
		StringBuilder builder = new StringBuilder();

		String threadName = null;
		StackTraceElement stackTraceElement = createdStackTraceElement;
		Date now = null;

		for (Token token : loggingEntryTokens) {
			switch (token.getType()) {
				case THREAD:
					if (threadName == null) {
						threadName = Thread.currentThread().getName();
					}
					builder.append(threadName);
					break;

				case CLASS:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep);
					}
					builder.append(stackTraceElement.getClassName());
					break;

				case METHOD:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep);
					}
					builder.append(stackTraceElement.getMethodName());
					break;

				case FILE:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep);
					}
					builder.append(stackTraceElement.getFileName());
					break;

				case LINE_NUMBER:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep);
					}
					builder.append(stackTraceElement.getLineNumber());
					break;

				case LOGGING_LEVEL:
					builder.append(level);
					break;

				case DATE:
					if (now == null) {
						now = new Date();
					}
					DateFormat formatter = (DateFormat) token.getData();
					String format;
					synchronized (formatter) {
						format = formatter.format(now);
					}
					builder.append(format);
					break;

				case MESSAGE:
					if (message != null) {
						builder.append(new MessageFormat(message, locale).format(arguments));
					}
					if (exception != null) {
						if (message != null) {
							builder.append(": ");
						}
						int countLoggingStackTraceElements = maxLoggingStackTraceElements;
						if (countLoggingStackTraceElements == 0) {
							builder.append(exception.getClass().getName());
							String exceptionMessage = exception.getMessage();
							if (exceptionMessage != null) {
								builder.append(": ");
								builder.append(exceptionMessage);
							}
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

	private static LoggingLevel getLoggingLevelOfClass(final String className) {
		int index = className.lastIndexOf('.');
		if (index > 0) {
			return getLoggingLevelOfPackage(className.substring(0, index));
		} else {
			return loggingLevel;
		}
	}

	private static LoggingLevel getLoggingLevelOfPackage(final String packageName) {
		String packageKey = packageName;
		while (true) {
			LoggingLevel level = packageLoggingLevels.get(packageKey);
			if (level != null) {
				return level;
			}
			int index = packageKey.lastIndexOf('.');
			if (index > 0) {
				packageKey = packageKey.substring(0, index);
			} else {
				return loggingLevel;
			}
		}
	}

	private static StackTraceElement getStackTraceElement(final int deep) {
		if (stackTraceMethod != null) {
			try {
				return (StackTraceElement) stackTraceMethod.invoke(new Throwable(), deep);
			} catch (Exception ex) {
				// Fallback
			}
		}

		StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
		if (stackTraceElements.length > deep) {
			return stackTraceElements[deep];
		} else {
			return new StackTraceElement("<unknown>", "<unknown>", "<unknown>", 0);
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
			builder.append(getPrintedException(cause, countStackTraceElements - length));
		}

		return builder.toString();
	}

}
