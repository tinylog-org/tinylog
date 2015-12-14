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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.Writer;

/**
 * Static class to create log entries.
 *
 * The default severity level is {@link org.pmw.tinylog.Level#INFO Level.INFO}, which ignores trace and debug log
 * entries.
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
		} catch (Throwable ex) {
			stackTraceMethod = null;
		}

		try {
			@SuppressWarnings({ "restriction", "deprecation" })
			Class<?> caller = sun.reflect.Reflection.getCallerClass(1);
			hasSunReflection = Logger.class.equals(caller);
		} catch (Throwable ex) {
			hasSunReflection = false;
		}
	}

	private Logger() {
	}

	/**
	 * Get the current global severity level.
	 *
	 * @return Global severity level
	 */
	public static Level getLevel() {
		return configuration.getLevel();
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
		return configuration.getLevel(packageObject.getName());
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
		return configuration.getLevel(classObject.getName());
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
		if (currentConfiguration.isOutputPossible(Level.TRACE)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.TRACE, null, obj, null);
		}
	}

	/**
	 * Create a trace log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void trace(final String message) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.TRACE)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.TRACE, null, message, null);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.TRACE)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.TRACE, null, message, arguments);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.TRACE)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.TRACE, exception, message, arguments);
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
		if (currentConfiguration.isOutputPossible(Level.TRACE)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.TRACE, exception, null, null);
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
		if (currentConfiguration.isOutputPossible(Level.DEBUG)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.DEBUG, null, obj, null);
		}
	}

	/**
	 * Create a debug log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void debug(final String message) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.DEBUG)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.DEBUG, null, message, null);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.DEBUG)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.DEBUG, null, message, arguments);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.DEBUG)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.DEBUG, exception, message, arguments);
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
		if (currentConfiguration.isOutputPossible(Level.DEBUG)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.DEBUG, exception, null, null);
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
		if (currentConfiguration.isOutputPossible(Level.INFO)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.INFO, null, obj, null);
		}
	}

	/**
	 * Create an info log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void info(final String message) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.INFO)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.INFO, null, message, null);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.INFO)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.INFO, null, message, arguments);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.INFO)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.INFO, exception, message, arguments);
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
		if (currentConfiguration.isOutputPossible(Level.INFO)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.INFO, exception, null, null);
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
		if (currentConfiguration.isOutputPossible(Level.WARNING)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.WARNING, null, obj, null);
		}
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void warn(final String message) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.WARNING)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.WARNING, null, message, null);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.WARNING)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.WARNING, null, message, arguments);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.WARNING)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.WARNING, exception, message, arguments);
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
		if (currentConfiguration.isOutputPossible(Level.WARNING)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.WARNING, exception, null, null);
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
		if (currentConfiguration.isOutputPossible(Level.ERROR)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.ERROR, null, obj, null);
		}
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void error(final String message) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.ERROR)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.ERROR, null, message, null);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.ERROR)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.ERROR, null, message, arguments);
		}
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
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(Level.ERROR)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.ERROR, exception, message, arguments);
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
		if (currentConfiguration.isOutputPossible(Level.ERROR)) {
			output(currentConfiguration, DEEP_OF_STACK_TRACE, Level.ERROR, exception, null, null);
		}
	}

	/**
	 * Get a copy of the current configuration.
	 *
	 * @return A copy of the current configuration
	 */
	public static Configurator getConfiguration() {
		return configuration.getConfigurator();
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
	static void setConfiguration(final Configuration configuration) throws Exception {
		Configuration previousConfiguration = Logger.configuration;

		if (previousConfiguration == null) {
			for (Writer writer : configuration.getWriters()) {
				writer.init(configuration);
			}
		} else {
			List<Writer> newWriters = configuration.getWriters();
			List<Writer> oldWriters = previousConfiguration.getWriters();

			for (Writer writer : newWriters) {
				if (!oldWriters.contains(writer)) {
					writer.init(configuration);
				}
			}
		}

		Logger.configuration = configuration;
	}

	/**
	 * Add a log entry. This method is helpful for adding log entries form logger bridges.
	 *
	 * @param strackTraceDeep
	 *            Deep of stack trace for finding the class, source line etc.
	 * @param level
	 *            Severity level
	 * @param exception
	 *            Exception to log (can be <code>null</code> if there is no exception to log)
	 * @param message
	 *            Formated text or a object to log
	 * @param arguments
	 *            Arguments for the text message
	 */
	static void output(final int strackTraceDeep, final Level level, final Throwable exception, final Object message, final Object[] arguments) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(level)) {
			output(currentConfiguration, strackTraceDeep, level, exception, message, arguments);
		}
	}

	/**
	 * Add a log entry. This method is helpful for adding log entries form logger bridges.
	 *
	 * @param stackTraceElement
	 *            Created stack trace element with class, source line etc.
	 * @param level
	 *            Severity level
	 * @param exception
	 *            Exception to log (can be <code>null</code> if there is no exception to log)
	 * @param message
	 *            Formated text or a object to log
	 * @param arguments
	 *            Arguments for the text message
	 */
	static void output(final StackTraceElement stackTraceElement, final Level level, final Throwable exception, final Object message, final Object[] arguments) {
		Configuration currentConfiguration = configuration;
		if (currentConfiguration.isOutputPossible(level)) {
			output(currentConfiguration, stackTraceElement, level, exception, message, arguments);
		}
	}

	private static void output(final Configuration currentConfiguration, final int strackTraceDeep, final Level level, final Throwable exception,
			final Object message, final Object[] arguments) {
		StackTraceElement stackTraceElement = null;
		Level activeLevel = currentConfiguration.getLevel();

		if (currentConfiguration.hasCustomLevels()) {
			boolean onlyClassName = currentConfiguration.getRequiredStackTraceInformation(level) == StackTraceInformation.CLASS_NAME;
			stackTraceElement = getStackTraceElement(strackTraceDeep, onlyClassName);
			activeLevel = currentConfiguration.getLevel(stackTraceElement.getClassName());
		}

		if (activeLevel.ordinal() <= level.ordinal()) {
			try {
				Writer[] writers = currentConfiguration.getEffectiveWriters(level);
				LogEntry[] logEntries = createLogEntries(currentConfiguration, strackTraceDeep + 1, level, stackTraceElement, exception, message, arguments);
				if (currentConfiguration.getWritingThread() == null) {
					for (int i = 0; i < writers.length; ++i) {
						try {
							writers[i].write(logEntries[i]);
						} catch (Exception ex) {
							InternalLogger.error(ex, "Failed to write log entry");
						}
					}
				} else {
					for (int i = 0; i < writers.length; ++i) {
						currentConfiguration.getWritingThread().putLogEntry(writers[i], logEntries[i]);
					}
				}
			} catch (Exception ex) {
				InternalLogger.error(ex, "Failed to create log entry");
			}
		}
	}

	private static void output(final Configuration currentConfiguration, final StackTraceElement stackTraceElement, final Level level,
			final Throwable exception, final Object message, final Object[] arguments) {
		Level activeLevel = currentConfiguration.getLevel();

		if (currentConfiguration.hasCustomLevels()) {
			activeLevel = currentConfiguration.getLevel(stackTraceElement.getClassName());
		}

		if (activeLevel.ordinal() <= level.ordinal()) {
			try {
				Writer[] writers = currentConfiguration.getEffectiveWriters(level);
				LogEntry[] logEntries = createLogEntries(currentConfiguration, -1, level, stackTraceElement, exception, message, arguments);
				if (currentConfiguration.getWritingThread() == null) {
					for (int i = 0; i < writers.length; ++i) {
						try {
							writers[i].write(logEntries[i]);
						} catch (Exception ex) {
							InternalLogger.error(ex, "Failed to write log entry");
						}
					}
				} else {
					for (int i = 0; i < writers.length; ++i) {
						currentConfiguration.getWritingThread().putLogEntry(writers[i], logEntries[i]);
					}
				}
			} catch (Exception ex) {
				InternalLogger.error(ex, "Failed to create log entry");
			}
		}
	}

	private static LogEntry[] createLogEntries(final Configuration currentConfiguration, final int strackTraceDeep, final Level level,
			final StackTraceElement createdStackTraceElement, final Throwable exception, final Object message, final Object[] arguments) {
		Set<LogEntryValue> requiredLogEntryValues = currentConfiguration.getRequiredLogEntryValues(level);
		List<Token>[] formatTokens = currentConfiguration.getEffectiveFormatTokens(level);
		LogEntry[] entries = new LogEntry[formatTokens.length];

		Date now = null;
		String processId = null;
		Thread thread = null;
		Map<String, String> context = null;
		StackTraceElement stackTraceElement = createdStackTraceElement;
		String className = null;
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

				case CONTEXT:
					context = LoggingContext.getMapping();
					break;

				case CLASS:
					if (stackTraceElement == null) {
						boolean onlyClassName = currentConfiguration.getRequiredStackTraceInformation(level) == StackTraceInformation.CLASS_NAME;
						stackTraceElement = getStackTraceElement(strackTraceDeep, onlyClassName);
					}
					className = stackTraceElement.getClassName();
					for (int index = className.indexOf("$", 0); index != -1 && index < className.length() - 1; index = className.indexOf('$', index + 2)) {
						char firstLetter = className.charAt(index + 1);
						if (firstLetter >= '0' && firstLetter <= '9') {
							className = className.substring(0, index);
							break;
						}
					}
					break;

				case METHOD:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep, false);
					}
					method = stackTraceElement.getMethodName();
					break;

				case FILE:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep, false);
					}
					filename = stackTraceElement.getFileName();
					break;

				case LINE:
					if (stackTraceElement == null) {
						stackTraceElement = getStackTraceElement(strackTraceDeep, false);
					}
					line = stackTraceElement.getLineNumber();
					break;

				case MESSAGE:
					if (message != null) {
						if (message instanceof String) {
							renderedMessage = MessageFormatter.format((String) message, arguments);
						} else {
							renderedMessage = message.toString();
						}
					}
					break;

				default:
					break;
			}
		}

		for (int i = 0; i < entries.length; ++i) {
			LogEntry logEntry = new LogEntry(now, processId, thread, context, className, method, filename, line, level, renderedMessage, exception);
			List<Token> formatTokensOfWriter = formatTokens[i];
			if (formatTokensOfWriter != null) {
				StringBuilder builder = new StringBuilder(exception == null ? 256 : 1024);
				for (Token token : formatTokensOfWriter) {
					token.render(logEntry, builder);
				}
				builder.append(NEW_LINE);
				logEntry.setRenderedLogEntry(builder.toString());
			}
			entries[i] = logEntry;
		}

		return entries;
	}

	@SuppressWarnings({ "deprecation", "restriction" })
	private static StackTraceElement getStackTraceElement(final int deep, final boolean onlyClassName) {
		if (onlyClassName && hasSunReflection) {
			try {
				return new StackTraceElement(sun.reflect.Reflection.getCallerClass(deep + 1).getName(), "<unknown>", "<unknown>", -1);
			} catch (Exception ex) {
				InternalLogger.warn(ex, "Failed to get caller class from sun.reflect.Reflection");
			}
		}

		if (stackTraceMethod != null) {
			try {
				return (StackTraceElement) stackTraceMethod.invoke(new Throwable(), deep);
			} catch (Exception ex) {
				InternalLogger.warn(ex, "Failed to get single stack trace element from throwable");
			}
		}

		return new Throwable().getStackTrace()[deep];
	}

}
