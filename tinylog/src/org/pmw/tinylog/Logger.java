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
import java.util.Date;
import java.util.Set;

import org.pmw.tinylog.writers.LogEntry;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.LoggingWriter;

import sun.reflect.Reflection; // SUPPRESS CHECKSTYLE Illegal Imports

/**
 * Static class to create log entries.
 * 
 * The default logging level is {@link org.pmw.tinylog.LoggingLevel#INFO LoggingLevel.INFO}, which ignores trace and
 * debug log entries.
 */
public final class Logger {

	/**
	 * Default deep in stack trace to find the needed stack trace element.
	 */
	static final int DEEP_OF_STACK_TRACE = 3;

	private static final String NEW_LINE = EnvironmentHelper.getNewLine();

	private static volatile Configuration configuration = Configurator.defaultConfig().create();

	private static Method stackTraceMethod;
	private static boolean hasSunReflection;

	static {
		Configurator.init().activate();

		try {
			stackTraceMethod = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
			stackTraceMethod.setAccessible(true);
			StackTraceElement stackTraceElement = (StackTraceElement) stackTraceMethod.invoke(new Throwable(), 0);
			if (!Logger.class.getName().equals(stackTraceElement.getClassName())) {
				stackTraceMethod = null;
			}
		} catch (Exception ex) {
			stackTraceMethod = null;
		}

		try {
			Reflection.getCallerClass();
			hasSunReflection = true;
		} catch (Exception ex) {
			hasSunReflection = false;
		}
	}

	private Logger() {
	}

	/**
	 * Get the current global logging level.
	 * 
	 * @return Global logging level
	 */
	public static LoggingLevel getLoggingLevel() {
		return configuration.getLevel();
	}

	/**
	 * Get the current logging level for a specific package.
	 * 
	 * @param packageObject
	 *            Package
	 * 
	 * @return Logging level for the package
	 */
	public static LoggingLevel getLoggingLevel(final Package packageObject) {
		return configuration.getLevel(packageObject.getName());
	}

	/**
	 * Get the current logging level for a specific class.
	 * 
	 * @param classObject
	 *            Name of the class
	 * 
	 * @return Logging level for the class
	 */
	public static LoggingLevel getLoggingLevel(final Class<?> classObject) {
		return configuration.getLevel(classObject.getName());
	}

	/**
	 * Get the current logging level for a specific package or class.
	 * 
	 * @param packageOrClass
	 *            Name of the package respectively class
	 * 
	 * @return Logging level for the package respectively class
	 */
	public static LoggingLevel getLoggingLevel(final String packageOrClass) {
		return configuration.getLevel(packageOrClass);
	}

	/**
	 * Create a trace log entry.
	 * 
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void trace(final Object obj) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.TRACE, null, obj, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.TRACE, null, obj, null);
		}
	}

	/**
	 * Create a trace log entry.
	 * 
	 * @param message
	 *            Text message to log
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void trace(final String message) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.TRACE, null, message, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.TRACE, null, message, null);
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.TRACE, null, message, arguments);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.TRACE, null, message, arguments);
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.TRACE, exception, message, arguments);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.TRACE, exception, message, arguments);
		}
	}

	/**
	 * Create a trace log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void trace(final Throwable exception) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.TRACE, exception, null, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.TRACE, exception, null, null);
		}
	}

	/**
	 * Create a debug log entry.
	 * 
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void debug(final Object obj) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.DEBUG, null, obj, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.DEBUG, null, obj, null);
		}
	}

	/**
	 * Create a debug log entry.
	 * 
	 * @param message
	 *            Text message to log
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void debug(final String message) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.DEBUG, null, message, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.DEBUG, null, message, null);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.DEBUG, null, message, arguments);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.DEBUG, null, message, arguments);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.DEBUG, exception, message, arguments);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.DEBUG, exception, message, arguments);
		}
	}

	/**
	 * Create a debug log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void debug(final Throwable exception) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.DEBUG, exception, null, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.DEBUG, exception, null, null);
		}
	}

	/**
	 * Create an info log entry.
	 * 
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void info(final Object obj) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.INFO, null, obj, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.INFO, null, obj, null);
		}
	}

	/**
	 * Create an info log entry.
	 * 
	 * @param message
	 *            Text message to log
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void info(final String message) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.INFO, null, message, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.INFO, null, message, null);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.INFO, null, message, arguments);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.INFO, null, message, arguments);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.INFO, exception, message, arguments);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.INFO, exception, message, arguments);
		}
	}

	/**
	 * Create an info log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void info(final Throwable exception) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.INFO, exception, null, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.INFO, exception, null, null);
		}
	}

	/**
	 * Create a warning log entry.
	 * 
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void warn(final Object obj) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.WARNING, null, obj, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.WARNING, null, obj, null);
		}
	}

	/**
	 * Create a warning log entry.
	 * 
	 * @param message
	 *            Text message to log
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void warn(final String message) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.WARNING, null, message, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.WARNING, null, message, null);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.WARNING, null, message, arguments);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.WARNING, null, message, arguments);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.WARNING, exception, message, arguments);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.WARNING, exception, message, arguments);
		}
	}

	/**
	 * Create a warning log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void warn(final Throwable exception) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.WARNING, exception, null, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.WARNING, exception, null, null);
		}
	}

	/**
	 * Create an error log entry.
	 * 
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void error(final Object obj) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.ERROR, null, obj, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.ERROR, null, obj, null);
		}
	}

	/**
	 * Create an error log entry.
	 * 
	 * @param message
	 *            Text message to log
	 * 
	 * @see MessageFormat#format(String, Object...)
	 */
	public static void error(final String message) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.ERROR, null, message, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.ERROR, null, message, null);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.ERROR, null, message, arguments);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.ERROR, null, message, arguments);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.ERROR, exception, message, arguments);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.ERROR, exception, message, arguments);
		}
	}

	/**
	 * Create an error log entry.
	 * 
	 * @param exception
	 *            Exception to log
	 */
	public static void error(final Throwable exception) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.getRequiredStackTraceInformation() == StackTraceInformation.CLASS_NAME && hasSunReflection) {
			output(currentConfiguration, Reflection.getCallerClass(), LoggingLevel.ERROR, exception, null, null);
		} else {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, LoggingLevel.ERROR, exception, null, null);
		}
	}

	/**
	 * Get a copy of the current configuration.
	 * 
	 * @return A copy of the current configuration
	 */
	public static Configurator getConfiguration() {
		return configuration.copy();
	}

	/**
	 * Set a new configuration.
	 * 
	 * @param configuration
	 *            New configuration
	 * 
	 * @throws Exception
	 *             Failed to initialize the writer
	 */
	static void setConfirguration(final Configuration configuration) throws Exception {
		Configuration previousConfiguration = Logger.configuration;

		LoggingWriter newWriter = configuration.getWriter();
		LoggingWriter oldWriter = previousConfiguration == null ? null : previousConfiguration.getWriter();

		if (newWriter != null && newWriter != oldWriter) {
			newWriter.init(configuration);
		}

		Logger.configuration = configuration;
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
	 *            Formated text or a object to log
	 * @param arguments
	 *            Arguments for the text message
	 */
	static void output(final int strackTraceDeep, final LoggingLevel level, final Throwable exception, final Object message, final Object[] arguments) {
		output(configuration, strackTraceDeep, level, exception, message, arguments);
	}

	/**
	 * Add a log entry. This method is helpful for adding log entries form logger bridges.
	 * 
	 * @param stackTraceElement
	 *            Created stack trace element with class, source line etc.
	 * @param level
	 *            Logging level of the log entry
	 * @param exception
	 *            Exception to log (can be <code>null</code> if there is no exception to log)
	 * @param message
	 *            Formated text or a object to log
	 * @param arguments
	 *            Arguments for the text message
	 */
	static void output(final StackTraceElement stackTraceElement, final LoggingLevel level, final Throwable exception, final Object message,
			final Object[] arguments) {
		output(configuration, stackTraceElement, level, exception, message, arguments);
	}

	private static void output(final Configuration currentConfiguration, final int strackTraceDeep, final LoggingLevel level, final Throwable exception,
			final Object message, final Object[] arguments) {
		LoggingWriter writer = currentConfiguration.getEffectiveWriter();

		if (writer != null) {
			StackTraceElement stackTraceElement = null;
			LoggingLevel activeLoggingLevel = currentConfiguration.getLevel();

			if (currentConfiguration.hasCustomLoggingLevels()) {
				stackTraceElement = getStackTraceElement(strackTraceDeep);
				activeLoggingLevel = currentConfiguration.getLevel(stackTraceElement.getClassName());
			}

			if (activeLoggingLevel.ordinal() <= level.ordinal()) {
				try {
					LogEntry logEntry = createLogEntry(currentConfiguration, strackTraceDeep + 1, level, stackTraceElement, exception, message, arguments);
					if (currentConfiguration.getWritingThread() == null) {
						writer.write(logEntry);
					} else {
						currentConfiguration.getWritingThread().putLogEntry(writer, logEntry);
					}
				} catch (Exception ex) {
					InternalLogger.error(ex, "Failed to write log entry");
				}
			}
		}
	}

	private static void output(final Configuration currentConfiguration, final Class<?> callerClass, final LoggingLevel level, final Throwable exception,
			final Object message, final Object[] arguments) {
		LoggingWriter writer = currentConfiguration.getEffectiveWriter();

		if (writer != null) {
			LoggingLevel activeLoggingLevel = currentConfiguration.getLevel();

			if (currentConfiguration.hasCustomLoggingLevels()) {
				activeLoggingLevel = currentConfiguration.getLevel(callerClass.getName());
			}

			if (activeLoggingLevel.ordinal() <= level.ordinal()) {
				try {
					StackTraceElement stackTraceElement = new StackTraceElement(callerClass.getName(), "<unknown>", "<unknown>", -1);
					LogEntry logEntry = createLogEntry(currentConfiguration, -1, level, stackTraceElement, exception, message, arguments);
					if (currentConfiguration.getWritingThread() == null) {
						writer.write(logEntry);
					} else {
						currentConfiguration.getWritingThread().putLogEntry(writer, logEntry);
					}
				} catch (Exception ex) {
					InternalLogger.error(ex, "Failed to write log entry");
				}
			}
		}
	}

	private static void output(final Configuration currentConfiguration, final StackTraceElement stackTraceElement, final LoggingLevel level,
			final Throwable exception, final Object message, final Object[] arguments) {
		LoggingWriter writer = currentConfiguration.getEffectiveWriter();

		if (writer != null) {
			LoggingLevel activeLoggingLevel = currentConfiguration.getLevel();

			if (currentConfiguration.hasCustomLoggingLevels()) {
				activeLoggingLevel = currentConfiguration.getLevel(stackTraceElement.getClassName());
			}

			if (activeLoggingLevel.ordinal() <= level.ordinal()) {
				try {
					LogEntry logEntry = createLogEntry(currentConfiguration, -1, level, stackTraceElement, exception, message, arguments);
					if (currentConfiguration.getWritingThread() == null) {
						writer.write(logEntry);
					} else {
						currentConfiguration.getWritingThread().putLogEntry(writer, logEntry);
					}
				} catch (Exception ex) {
					InternalLogger.error(ex, "Failed to write log entry");
				}
			}
		}
	}

	private static LogEntry createLogEntry(final Configuration currentConfiguration, final int strackTraceDeep, final LoggingLevel level,
			final StackTraceElement createdStackTraceElement, final Throwable exception, final Object message, final Object[] arguments) {
		Set<LogEntryValue> requiredLogEntryValues = currentConfiguration.getRequiredLogEntryValues();

		Date now = null;
		String processId = null;
		Thread thread = null;
		StackTraceElement stackTraceElement = createdStackTraceElement;
		String fullyQualifiedClassName = null;
		String method = null;
		String filename = null;
		int line = -1;
		String renderedMessage = null;

		for (LogEntryValue logEntryValue : requiredLogEntryValues) {
			switch (logEntryValue) {
				case DATE:
					now = new Date();
					break;

				case PROCESS_ID:
					processId = EnvironmentHelper.getProcessId().toString();
					break;

				case THREAD:
					thread = Thread.currentThread();
					break;

				case CLASS:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep);
					}
					fullyQualifiedClassName = stackTraceElement.getClassName();
					break;

				case METHOD:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep);
					}
					method = stackTraceElement.getMethodName();
					break;

				case FILE:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep);
					}
					filename = stackTraceElement.getFileName();
					break;

				case LINE_NUMBER:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep);
					}
					line = stackTraceElement.getLineNumber();
					break;

				case MESSAGE:
					if (message != null) {
						renderedMessage = getRenderedMessage(currentConfiguration, message, arguments);
					}
					break;

				default:
					break;
			}
		}

		String renderedLogEntry;
		if (requiredLogEntryValues.contains(LogEntryValue.RENDERED_LOG_ENTRY)) {
			StringBuilder builder = new StringBuilder();
			for (Token token : currentConfiguration.getFormatTokens()) {
				switch (token.getType()) {
					case DATE:
						builder.append(getRenderedDate(now, token));
						break;

					case THREAD_NAME:
						builder.append(thread.getName());
						break;

					case THREAD_ID:
						builder.append(thread.getId());
						break;

					case CLASS:
						builder.append(fullyQualifiedClassName);
						break;

					case CLASS_NAME:
						builder.append(getNameOfClass(fullyQualifiedClassName));
						break;

					case PACKAGE:
						builder.append(getPackageOfClass(fullyQualifiedClassName));
						break;

					case METHOD:
						builder.append(method);
						break;

					case FILE:
						builder.append(filename);
						break;

					case LINE_NUMBER:
						builder.append(line);
						break;

					case LOGGING_LEVEL:
						builder.append(level);
						break;

					case MESSAGE:
						if (message != null) {
							builder.append(renderedMessage);
						}
						if (exception != null) {
							if (message != null) {
								builder.append(": ");
							}
							formatException(builder, exception, currentConfiguration.getMaxStackTraceElements());
						}
						break;

					default:
						builder.append(token.getData());
						break;
				}
			}
			builder.append(NEW_LINE);
			renderedLogEntry = builder.toString();
		} else {
			renderedLogEntry = null;
		}

		return new LogEntry(now, processId, thread, fullyQualifiedClassName, method, filename, line, level, renderedMessage, exception, renderedLogEntry);
	}

	private static StackTraceElement getStackTraceElement(final int deep) {
		if (stackTraceMethod != null) {
			try {
				return (StackTraceElement) stackTraceMethod.invoke(new Throwable(), deep);
			} catch (Exception ex) {
				InternalLogger.warn(ex, "Failed to get single stack trace element from throwable");
			}
		}

		return new Throwable().getStackTrace()[deep];
	}

	private static String getNameOfClass(final String fullyQualifiedClassName) {
		int dotIndex = fullyQualifiedClassName.lastIndexOf('.');
		if (dotIndex < 0) {
			return fullyQualifiedClassName;
		} else {
			return fullyQualifiedClassName.substring(dotIndex + 1);
		}
	}

	private static String getPackageOfClass(final String fullyQualifiedClassName) {
		int dotIndex = fullyQualifiedClassName.lastIndexOf('.');
		if (dotIndex < 0) {
			return "";
		} else {
			return fullyQualifiedClassName.substring(0, dotIndex);
		}
	}

	private static String getRenderedDate(final Date now, final Token token) {
		DateFormat formatter = (DateFormat) token.getData();
		synchronized (formatter) {
			return formatter.format(now);
		}
	}

	private static String getRenderedMessage(final Configuration currentConfiguration, final Object message, final Object[] arguments) {
		String renderedMessage;
		if (arguments == null || arguments.length == 0) {
			renderedMessage = message.toString();
		} else {
			renderedMessage = new MessageFormat((String) message, currentConfiguration.getLocale()).format(arguments);
		}
		return renderedMessage;
	}

	private static void formatException(final StringBuilder builder, final Throwable exception, final int countStackTraceElements) {
		if (countStackTraceElements == 0) {
			builder.append(exception.getClass().getName());
			String exceptionMessage = exception.getMessage();
			if (exceptionMessage != null) {
				builder.append(": ");
				builder.append(exceptionMessage);
			}
		} else {
			formatExceptionWithStackTrace(builder, exception, countStackTraceElements);
		}
	}

	private static void formatExceptionWithStackTrace(final StringBuilder builder, final Throwable exception, final int countStackTraceElements) {
		builder.append(exception.getClass().getName());

		String message = exception.getMessage();
		if (message != null) {
			builder.append(": ");
			builder.append(message);
		}

		StackTraceElement[] stackTrace = exception.getStackTrace();
		int length = Math.min(stackTrace.length, Math.max(1, countStackTraceElements));
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
		} else {
			Throwable cause = exception.getCause();
			if (cause != null) {
				builder.append(NEW_LINE);
				builder.append("Caused by: ");
				formatExceptionWithStackTrace(builder, cause, countStackTraceElements - length);
			}
		}
	}

}
