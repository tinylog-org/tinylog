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
	private static final String FQCN = Logger.class.getName();

	private static final RuntimeDialect runtime = EnvironmentHelper.getRuntimeDialect();

	private Logger() {
	}

	/**
	 * Get the current global severity level.
	 *
	 * @return Global severity level
	 */
	public static Level getLevel() {
		return getLevel(org.jboss.logging.Logger.getLogger(""));
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
		return getLevel(org.jboss.logging.Logger.getLogger(packageObject.getName()));
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
		return getLevel(org.jboss.logging.Logger.getLogger(classObject));
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
		return getLevel(org.jboss.logging.Logger.getLogger(packageOrClass));
	}

	/**
	 * Create a trace log entry.
	 *
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void trace(final Object obj) {
		getJBossLogger().trace(FQCN, obj, null);
	}

	/**
	 * Create a trace log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void trace(final String message) {
		getJBossLogger().trace(FQCN, message, null);
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
		org.jboss.logging.Logger logger = getJBossLogger();
		if (logger.isEnabled(org.jboss.logging.Logger.Level.TRACE)) {
			logger.trace(FQCN, MessageFormatter.format(message, arguments), null);
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
		org.jboss.logging.Logger logger = getJBossLogger();
		if (logger.isEnabled(org.jboss.logging.Logger.Level.TRACE)) {
			logger.trace(FQCN, MessageFormatter.format(message, arguments), exception);
		}
	}

	/**
	 * Create a trace log entry.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void trace(final Throwable exception) {
		getJBossLogger().trace(FQCN, exception, exception);
	}

	/**
	 * Create a debug log entry.
	 *
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void debug(final Object obj) {
		getJBossLogger().debug(FQCN, obj, null);
	}

	/**
	 * Create a debug log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void debug(final String message) {
		getJBossLogger().debug(FQCN, message, null);
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
		org.jboss.logging.Logger logger = getJBossLogger();
		if (logger.isEnabled(org.jboss.logging.Logger.Level.DEBUG)) {
			logger.debug(FQCN, MessageFormatter.format(message, arguments), null);
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
		org.jboss.logging.Logger logger = getJBossLogger();
		if (logger.isEnabled(org.jboss.logging.Logger.Level.DEBUG)) {
			logger.debug(FQCN, MessageFormatter.format(message, arguments), exception);
		}
	}

	/**
	 * Create a debug log entry.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void debug(final Throwable exception) {
		getJBossLogger().debug(FQCN, exception, exception);
	}

	/**
	 * Create an info log entry.
	 *
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void info(final Object obj) {
		getJBossLogger().info(FQCN, obj, null);
	}

	/**
	 * Create an info log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void info(final String message) {
		getJBossLogger().info(FQCN, message, null);
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
		org.jboss.logging.Logger logger = getJBossLogger();
		if (logger.isEnabled(org.jboss.logging.Logger.Level.INFO)) {
			logger.info(FQCN, MessageFormatter.format(message, arguments), null);
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
		org.jboss.logging.Logger logger = getJBossLogger();
		if (logger.isEnabled(org.jboss.logging.Logger.Level.INFO)) {
			logger.info(FQCN, MessageFormatter.format(message, arguments), exception);
		}
	}

	/**
	 * Create an info log entry.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void info(final Throwable exception) {
		getJBossLogger().info(FQCN, exception, exception);
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void warn(final Object obj) {
		getJBossLogger().warn(FQCN, obj, null);
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void warn(final String message) {
		getJBossLogger().warn(FQCN, message, null);
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
		org.jboss.logging.Logger logger = getJBossLogger();
		if (logger.isEnabled(org.jboss.logging.Logger.Level.WARN)) {
			logger.warn(FQCN, MessageFormatter.format(message, arguments), null);
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
		org.jboss.logging.Logger logger = getJBossLogger();
		if (logger.isEnabled(org.jboss.logging.Logger.Level.WARN)) {
			logger.warn(FQCN, MessageFormatter.format(message, arguments), exception);
		}
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void warn(final Throwable exception) {
		getJBossLogger().warn(FQCN, exception, exception);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param obj
	 *            The result of the <code>toString()</code> method will be logged
	 */
	public static void error(final Object obj) {
		getJBossLogger().error(FQCN, obj, null);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void error(final String message) {
		getJBossLogger().error(FQCN, message, null);
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
		org.jboss.logging.Logger logger = getJBossLogger();
		if (logger.isEnabled(org.jboss.logging.Logger.Level.ERROR)) {
			logger.error(FQCN, MessageFormatter.format(message, arguments), null);
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
		org.jboss.logging.Logger logger = getJBossLogger();
		if (logger.isEnabled(org.jboss.logging.Logger.Level.ERROR)) {
			logger.error(FQCN, MessageFormatter.format(message, arguments), exception);
		}
	}

	/**
	 * Create an error log entry.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void error(final Throwable exception) {
		getJBossLogger().error(FQCN, exception, exception);
	}

	private static org.jboss.logging.Logger getJBossLogger() {
		return org.jboss.logging.Logger.getLogger(runtime.getClassName(DEPTH_OF_STACK_TRACE));
	}

	private static Level getLevel(final org.jboss.logging.Logger logger) {
		if (logger.isEnabled(org.jboss.logging.Logger.Level.TRACE)) {
			return Level.TRACE;
		} else if (logger.isEnabled(org.jboss.logging.Logger.Level.DEBUG)) {
			return Level.DEBUG;
		} else if (logger.isEnabled(org.jboss.logging.Logger.Level.INFO)) {
			return Level.INFO;
		} else if (logger.isEnabled(org.jboss.logging.Logger.Level.WARN)) {
			return Level.WARNING;
		} else if (logger.isEnabled(org.jboss.logging.Logger.Level.ERROR)) {
			return Level.ERROR;
		} else {
			return Level.OFF;
		}
	}

}
