/*
 * Copyright 2018 Martin Winandy
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

import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * Static logger for issuing log entries for tinylog 2 via a tinylog 1.3 compatible API.
 */
public final class Logger {

	private static final int STACKTRACE_DEPTH = 2;

	private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();

	// @formatter:off
	private static final boolean MINIMUM_LEVEL_COVERS_TRACE = isCoveredByMinimumLevel(org.tinylog.Level.TRACE);
	private static final boolean MINIMUM_LEVEL_COVERS_DEBUG = isCoveredByMinimumLevel(org.tinylog.Level.DEBUG);
	private static final boolean MINIMUM_LEVEL_COVERS_INFO  = isCoveredByMinimumLevel(org.tinylog.Level.INFO);
	private static final boolean MINIMUM_LEVEL_COVERS_WARN  = isCoveredByMinimumLevel(org.tinylog.Level.WARN);
	private static final boolean MINIMUM_LEVEL_COVERS_ERROR = isCoveredByMinimumLevel(org.tinylog.Level.ERROR);
	// @formatter:on

	/** */
	private Logger() {
	}

	/**
	 * Gets the minimum enabled severity level.
	 * 
	 * @return Minimum enabled severity level
	 */
	public static Level getLevel() {
		return translateLevel(provider.getMinimumLevel(null));
	}

	/**
	 * Gets the minimum enabled severity level.
	 * 
	 * @param packageObject
	 *            Will be ignored
	 * @return Minimum enabled severity level
	 */
	public static Level getLevel(final Package packageObject) {
		return translateLevel(provider.getMinimumLevel(null));
	}

	/**
	 * Gets the minimum enabled severity level.
	 * 
	 * @param classObject
	 *            Will be ignored
	 * @return Minimum enabled severity level
	 */
	public static Level getLevel(final Class<?> classObject) {
		return translateLevel(provider.getMinimumLevel(null));
	}

	/**
	 * Logs a message at {@link Level#TRACE TRACE} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public static void trace(final Object message) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a message at {@link Level#TRACE TRACE} level.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void trace(final String message) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a lazy message at {@link Level#TRACE TRACE} level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public static void trace(final Supplier<?> message) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, message, arguments);
		}
	}

	/**
	 * Logs a formatted message at {@link Level#TRACE TRACE} level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 * 
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public static void trace(final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, message, (Object[]) arguments);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, null, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, message, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a custom lazy message at {@link Level#TRACE TRACE} level. The message will be only
	 * evaluated if the log entry is really output.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Function that produces the message
	 */
	public static void trace(final Throwable exception, final Supplier<String> message) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, message, arguments);
		}
	}

	/**
	 * Logs an exception with a formatted message at {@link Level#TRACE TRACE} level. "{}" placeholders will be replaced
	 * by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 * 
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public static void trace(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, exception, message, (Object[]) arguments);
		}
	}

	/**
	 * Logs a message at {@link Level#DEBUG DEBUG} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public static void debug(final Object message) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a message at {@link Level#DEBUG DEBUG} level.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void debug(final String message) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a lazy message at {@link Level#DEBUG DEBUG} level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public static void debug(final Supplier<?> message) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, message, arguments);
		}
	}

	/**
	 * Logs a formatted message at {@link Level#DEBUG DEBUG} level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 * 
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public static void debug(final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, null, message, (Object[]) arguments);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, null, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, message, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a custom lazy message at {@link Level#DEBUG DEBUG} level. The message will be only
	 * evaluated if the log entry is really output.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Function that produces the message
	 */
	public static void debug(final Throwable exception, final Supplier<String> message) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, message, arguments);
		}
	}

	/**
	 * Logs an exception with a formatted message at {@link Level#DEBUG DEBUG} level. "{}" placeholders will be replaced
	 * by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 * 
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public static void debug(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.DEBUG, exception, message, (Object[]) arguments);
		}
	}

	/**
	 * Logs a message at {@link Level#INFO INFO} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public static void info(final Object message) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a message at {@link Level#INFO INFO} level.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void info(final String message) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a lazy message at {@link Level#INFO INFO} level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public static void info(final Supplier<?> message) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, message, arguments);
		}
	}

	/**
	 * Logs a formatted message at {@link Level#INFO INFO} level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 * 
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public static void info(final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, null, message, (Object[]) arguments);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, null, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, message, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a custom lazy message at {@link Level#INFO INFO} level. The message will be only evaluated
	 * if the log entry is really output.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Function that produces the message
	 */
	public static void info(final Throwable exception, final Supplier<String> message) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, message, arguments);
		}
	}

	/**
	 * Logs an exception with a formatted message at {@link Level#INFO INFO} level. "{}" placeholders will be replaced
	 * by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 * 
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public static void info(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.INFO, exception, message, (Object[]) arguments);
		}
	}

	/**
	 * Logs a message at {@link Level#WARNING WARNING} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public static void warn(final Object message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a message at {@link Level#WARNING WARNING} level.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void warn(final String message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a lazy message at {@link Level#WARNING WARNING} level. The message will be only evaluated if the log entry
	 * is really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public static void warn(final Supplier<?> message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, message, arguments);
		}
	}

	/**
	 * Logs a formatted message at {@link Level#WARNING WARNING} level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 * 
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public static void warn(final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, null, message, (Object[]) arguments);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, null, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, message, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a custom lazy message at {@link Level#WARNING WARNING} level. The message will be only
	 * evaluated if the log entry is really output.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Function that produces the message
	 */
	public static void warn(final Throwable exception, final Supplier<String> message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, message, arguments);
		}
	}

	/**
	 * Logs an exception with a formatted message at {@link Level#WARNING WARNING} level. "{}" placeholders will be
	 * replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 * 
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public static void warn(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.WARN, exception, message, (Object[]) arguments);
		}
	}

	/**
	 * Logs a message at {@link Level#ERROR ERROR} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public static void error(final Object message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a message at {@link Level#ERROR ERROR} level.
	 *
	 * @param message
	 *            Text message to log
	 */
	public static void error(final String message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a lazy message at {@link Level#ERROR ERROR} level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public static void error(final Supplier<?> message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, message, arguments);
		}
	}

	/**
	 * Logs a formatted message at {@link Level#ERROR ERROR} level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 * 
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public static void error(final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, null, message, (Object[]) arguments);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, null, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, message, (Object[]) null);
		}
	}

	/**
	 * Logs an exception with a custom lazy message at {@link Level#ERROR ERROR} level. The message will be only
	 * evaluated if the log entry is really output.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Function that produces the message
	 */
	public static void error(final Throwable exception, final Supplier<String> message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, message, arguments);
		}
	}

	/**
	 * Logs an exception with a formatted message at {@link Level#ERROR ERROR} level. "{}" placeholders will be replaced
	 * by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 * 
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public static void error(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.ERROR, exception, message, (Object[]) arguments);
		}
	}

	/**
	 * Checks if a given severity level is covered by the logging provider's minimum level.
	 *
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is covered, otherwise {@code false}
	 */
	private static boolean isCoveredByMinimumLevel(final org.tinylog.Level level) {
		return provider.getMinimumLevel(null).ordinal() <= level.ordinal();
	}

	/**
	 * Translates a tinylog 2 severity level into a tinylog 1.3 severity level.
	 * 
	 * @param level
	 *            Severity level of tinylog 2
	 * @return Corresponding severity level of tinylog 1.3
	 */
	private static Level translateLevel(final org.tinylog.Level level) {
		switch (level) {
			case TRACE:
				return Level.TRACE;
			case DEBUG:
				return Level.DEBUG;
			case INFO:
				return Level.INFO;
			case WARN:
				return Level.WARNING;
			case ERROR:
				return Level.ERROR;
			case OFF:
				return Level.OFF;
			default:
				throw new IllegalArgumentException("Unknown tinylog 1 severity level \"" + level + "\"");
		}
	}

}
