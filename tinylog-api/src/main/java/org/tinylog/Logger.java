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

package org.tinylog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * Static logger for issuing log entries.
 */
public final class Logger {

	private static final int STACKTRACE_DEPTH = 1;

	private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();

	// @formatter:off
	private static final boolean MINIMUM_LEVEL_COVERS_TRACE = isCoveredByMinimumLevel(Level.TRACE);
	private static final boolean MINIMUM_LEVEL_COVERS_DEBUG = isCoveredByMinimumLevel(Level.DEBUG);
	private static final boolean MINIMUM_LEVEL_COVERS_INFO  = isCoveredByMinimumLevel(Level.INFO);
	private static final boolean MINIMUM_LEVEL_COVERS_WARN  = isCoveredByMinimumLevel(Level.WARNING);
	private static final boolean MINIMUM_LEVEL_COVERS_ERROR = isCoveredByMinimumLevel(Level.ERROR);
	// @formatter:on

	private static final TaggedLogger instance = new TaggedLogger(null);
	private static final ConcurrentMap<String, TaggedLogger> loggers = new ConcurrentHashMap<String, TaggedLogger>();

	/** */
	private Logger() {
	}

	/**
	 * Gets a tagged logger instance. Tags are case-sensitive.
	 *
	 * @param tag
	 *            Tag for logger or {@code null} for receiving an untagged logger
	 * @return Logger instance
	 */
	public static TaggedLogger tag(final String tag) {
		if (tag == null || tag.isEmpty()) {
			return instance;
		} else {
			TaggedLogger logger = loggers.get(tag);
			if (logger == null) {
				logger = new TaggedLogger(tag);
				TaggedLogger existing = loggers.putIfAbsent(tag, logger);
				return existing == null ? logger : existing;
			} else {
				return logger;
			}
		}
	}

	/**
	 * Checks whether log entries at {@link Level#TRACE TRACE} level will be output.
	 *
	 * @return {@code true} if {@link Level#TRACE TRACE} level is enabled, {@code false} if disabled
	 */
	public static boolean isTraceEnabled() {
		return MINIMUM_LEVEL_COVERS_TRACE && provider.isEnabled(STACKTRACE_DEPTH, null, Level.TRACE);
	}

	/**
	 * Logs a message at {@link Level#TRACE TRACE} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public static void trace(final Object message) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a formatted message at {@link Level#TRACE TRACE} level. "{}" placeholders will be replaced by given
	 * arguments.
	 *
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public static void trace(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, message, arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#TRACE TRACE} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public static void trace(final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, null, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a custom message at {@link Level#TRACE TRACE} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Text message to log
	 */
	public static void trace(final Throwable exception, final String message) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, message, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a formatted custom message at {@link Level#TRACE TRACE} level. "{}" placeholders will be
	 * replaced by given arguments.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public static void trace(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, message, arguments);
		}
	}

	/**
	 * Checks whether log entries at {@link Level#DEBUG DEBUG} level will be output.
	 *
	 * @return {@code true} if {@link Level#DEBUG DEBUG} level is enabled, {@code false} if disabled
	 */
	public static boolean isDebugEnabled() {
		return MINIMUM_LEVEL_COVERS_DEBUG && provider.isEnabled(STACKTRACE_DEPTH, null, Level.DEBUG);
	}

	/**
	 * Logs a message at {@link Level#DEBUG DEBUG} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public static void debug(final Object message) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a formatted message at {@link Level#DEBUG DEBUG} level. "{}" placeholders will be replaced by given
	 * arguments.
	 *
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public static void debug(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, message, arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#DEBUG DEBUG} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public static void debug(final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, null, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a custom message at {@link Level#DEBUG DEBUG} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Text message to log
	 */
	public static void debug(final Throwable exception, final String message) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, message, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a formatted custom message at {@link Level#DEBUG DEBUG} level. "{}" placeholders will be
	 * replaced by given arguments.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public static void debug(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, message, arguments);
		}
	}

	/**
	 * Checks whether log entries at {@link Level#INFO INFO} level will be output.
	 *
	 * @return {@code true} if {@link Level#INFO INFO} level is enabled, {@code false} if disabled
	 */
	public static boolean isInfoEnabled() {
		return MINIMUM_LEVEL_COVERS_INFO && provider.isEnabled(STACKTRACE_DEPTH, null, Level.INFO);
	}

	/**
	 * Logs a message at {@link Level#INFO INFO} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public static void info(final Object message) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a formatted message at {@link Level#INFO INFO} level. "{}" placeholders will be replaced by given arguments.
	 *
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public static void info(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, message, arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#INFO INFO} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public static void info(final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, null, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a custom message at {@link Level#INFO INFO} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Text message to log
	 */
	public static void info(final Throwable exception, final String message) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, message, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a formatted custom message at {@link Level#INFO INFO} level. "{}" placeholders will be
	 * replaced by given arguments.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public static void info(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, message, arguments);
		}
	}

	/**
	 * Checks whether log entries at {@link Level#WARNING WARNING} level will be output.
	 *
	 * @return {@code true} if {@link Level#WARNING WARNING} level is enabled, {@code false} if disabled
	 */
	public static boolean isWarnEnabled() {
		return MINIMUM_LEVEL_COVERS_WARN && provider.isEnabled(STACKTRACE_DEPTH, null, Level.WARNING);
	}

	/**
	 * Logs a message at {@link Level#WARNING WARNING} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public static void warn(final Object message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARNING, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a formatted message at {@link Level#WARNING WARNING} level. "{}" placeholders will be replaced by given
	 * arguments.
	 *
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public static void warn(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARNING, null, message, arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#WARNING WARNING} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public static void warn(final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARNING, exception, null, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a custom message at {@link Level#WARNING WARNING} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Text message to log
	 */
	public static void warn(final Throwable exception, final String message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARNING, exception, message, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a formatted custom message at {@link Level#WARNING WARNING} level. "{}" placeholders will
	 * be replaced by given arguments.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public static void warn(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARNING, exception, message, arguments);
		}
	}

	/**
	 * Checks whether log entries at {@link Level#ERROR ERROR} level will be output.
	 *
	 * @return {@code true} if {@link Level#ERROR ERROR} level is enabled, {@code false} if disabled
	 */
	public static boolean isErrorEnabled() {
		return MINIMUM_LEVEL_COVERS_ERROR && provider.isEnabled(STACKTRACE_DEPTH, null, Level.ERROR);
	}

	/**
	 * Logs a message at {@link Level#ERROR ERROR} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public static void error(final Object message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a formatted message at {@link Level#ERROR ERROR} level. "{}" placeholders will be replaced by given
	 * arguments.
	 *
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public static void error(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, message, arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#ERROR ERROR} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public static void error(final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, null, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a custom message at {@link Level#ERROR ERROR} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Text message to log
	 */
	public static void error(final Throwable exception, final String message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, message, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a formatted custom message at {@link Level#ERROR ERROR} level. "{}" placeholders will be
	 * replaced by given arguments.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public static void error(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, message, arguments);
		}
	}

	/**
	 * Checks if a given severity level is covered by the logging providers minimum level.
	 *
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is covered, otherwise {@code false}
	 */
	private static boolean isCoveredByMinimumLevel(final Level level) {
		return provider.getMinimumLevel(null).ordinal() <= level.ordinal();
	}

}
