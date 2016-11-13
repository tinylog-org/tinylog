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

import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * Logger for issuing tagged log entries. Tagged loggers can be received by calling {@link Logger#tag(String)}.
 *
 * @see Logger#tag(String)
 */
public final class TaggedLogger {

	private static final int STACKTRACE_DEPTH = 1;

	private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();

	private final boolean minimumLevelCoversTrace;
	private final boolean minimumLevelCoversDebug;
	private final boolean minimumLevelCoversInfo;
	private final boolean minimumLevelCoversWarn;
	private final boolean minimumLevelCoversError;

	private final String tag;

	/**
	 * @param tag
	 *            Case-sensitive tag for logger instance
	 */
	TaggedLogger(final String tag) {
		this.tag = tag;

		// @formatter:off
		minimumLevelCoversTrace = isCoveredByMinimumLevel(tag, Level.TRACE);
		minimumLevelCoversDebug = isCoveredByMinimumLevel(tag, Level.DEBUG);
		minimumLevelCoversInfo  = isCoveredByMinimumLevel(tag, Level.INFO);
		minimumLevelCoversWarn  = isCoveredByMinimumLevel(tag, Level.WARNING);
		minimumLevelCoversError = isCoveredByMinimumLevel(tag, Level.ERROR);
		// @formatter:on
	}

	/**
	 * Checks whether log entries at {@link Level#TRACE TRACE} level will be output.
	 *
	 * @return {@code true} if {@link Level#TRACE TRACE} level is enabled, {@code false} if disabled
	 */
	public boolean isTraceEnabled() {
		return minimumLevelCoversTrace && provider.isEnabled(STACKTRACE_DEPTH, tag, Level.TRACE);
	}

	/**
	 * Logs a message at {@link Level#TRACE TRACE} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public void trace(final Object message) {
		if (minimumLevelCoversTrace) {
			provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, null, message, (Object[]) null);
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
	public void trace(final String message, final Object... arguments) {
		if (minimumLevelCoversTrace) {
			provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, null, message, arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#TRACE TRACE} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public void trace(final Throwable exception) {
		if (minimumLevelCoversTrace) {
			provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, exception, null, (Object[]) null);
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
	public void trace(final Throwable exception, final String message) {
		if (minimumLevelCoversTrace) {
			provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, exception, message, (Object[]) null);
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
	public void trace(final Throwable exception, final String message, final Object... arguments) {
		if (minimumLevelCoversTrace) {
			provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, exception, message, arguments);
		}
	}

	/**
	 * Checks whether log entries at {@link Level#DEBUG DEBUG} level will be output.
	 *
	 * @return {@code true} if {@link Level#DEBUG DEBUG} level is enabled, {@code false} if disabled
	 */
	public boolean isDebugEnabled() {
		return minimumLevelCoversDebug && provider.isEnabled(STACKTRACE_DEPTH, tag, Level.DEBUG);
	}

	/**
	 * Logs a message at {@link Level#DEBUG DEBUG} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public void debug(final Object message) {
		if (minimumLevelCoversDebug) {
			provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, null, message, (Object[]) null);
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
	public void debug(final String message, final Object... arguments) {
		if (minimumLevelCoversDebug) {
			provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, null, message, arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#DEBUG DEBUG} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public void debug(final Throwable exception) {
		if (minimumLevelCoversDebug) {
			provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, exception, null, (Object[]) null);
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
	public void debug(final Throwable exception, final String message) {
		if (minimumLevelCoversDebug) {
			provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, exception, message, (Object[]) null);
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
	public void debug(final Throwable exception, final String message, final Object... arguments) {
		if (minimumLevelCoversDebug) {
			provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, exception, message, arguments);
		}
	}

	/**
	 * Checks whether log entries at {@link Level#INFO INFO} level will be output.
	 *
	 * @return {@code true} if {@link Level#INFO INFO} level is enabled, {@code false} if disabled
	 */
	public boolean isInfoEnabled() {
		return minimumLevelCoversInfo && provider.isEnabled(STACKTRACE_DEPTH, tag, Level.INFO);
	}

	/**
	 * Logs a message at {@link Level#INFO INFO} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public void info(final Object message) {
		if (minimumLevelCoversInfo) {
			provider.log(STACKTRACE_DEPTH, tag, Level.INFO, null, message, (Object[]) null);
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
	public void info(final String message, final Object... arguments) {
		if (minimumLevelCoversInfo) {
			provider.log(STACKTRACE_DEPTH, tag, Level.INFO, null, message, arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#INFO INFO} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public void info(final Throwable exception) {
		if (minimumLevelCoversInfo) {
			provider.log(STACKTRACE_DEPTH, tag, Level.INFO, exception, null, (Object[]) null);
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
	public void info(final Throwable exception, final String message) {
		if (minimumLevelCoversInfo) {
			provider.log(STACKTRACE_DEPTH, tag, Level.INFO, exception, message, (Object[]) null);
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
	public void info(final Throwable exception, final String message, final Object... arguments) {
		if (minimumLevelCoversInfo) {
			provider.log(STACKTRACE_DEPTH, tag, Level.INFO, exception, message, arguments);
		}
	}

	/**
	 * Checks whether log entries at {@link Level#WARNING WARNING} level will be output.
	 *
	 * @return {@code true} if {@link Level#WARNING WARNING} level is enabled, {@code false} if disabled
	 */
	public boolean isWarnEnabled() {
		return minimumLevelCoversWarn && provider.isEnabled(STACKTRACE_DEPTH, tag, Level.WARNING);
	}

	/**
	 * Logs a message at {@link Level#WARNING WARNING} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public void warn(final Object message) {
		if (minimumLevelCoversWarn) {
			provider.log(STACKTRACE_DEPTH, tag, Level.WARNING, null, message, (Object[]) null);
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
	public void warn(final String message, final Object... arguments) {
		if (minimumLevelCoversWarn) {
			provider.log(STACKTRACE_DEPTH, tag, Level.WARNING, null, message, arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#WARNING WARNING} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public void warn(final Throwable exception) {
		if (minimumLevelCoversWarn) {
			provider.log(STACKTRACE_DEPTH, tag, Level.WARNING, exception, null, (Object[]) null);
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
	public void warn(final Throwable exception, final String message) {
		if (minimumLevelCoversWarn) {
			provider.log(STACKTRACE_DEPTH, tag, Level.WARNING, exception, message, (Object[]) null);
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
	public void warn(final Throwable exception, final String message, final Object... arguments) {
		if (minimumLevelCoversWarn) {
			provider.log(STACKTRACE_DEPTH, tag, Level.WARNING, exception, message, arguments);
		}
	}

	/**
	 * Checks whether log entries at {@link Level#ERROR ERROR} level will be output.
	 *
	 * @return {@code true} if {@link Level#ERROR ERROR} level is enabled, {@code false} if disabled
	 */
	public boolean isErrorEnabled() {
		return minimumLevelCoversError && provider.isEnabled(STACKTRACE_DEPTH, tag, Level.ERROR);
	}

	/**
	 * Logs a message at {@link Level#ERROR ERROR} level.
	 *
	 * @param message
	 *            String or any other object with meaningful {@link #toString()} method
	 */
	public void error(final Object message) {
		if (minimumLevelCoversError) {
			provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, null, message, (Object[]) null);
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
	public void error(final String message, final Object... arguments) {
		if (minimumLevelCoversError) {
			provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, null, message, arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#ERROR ERROR} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public void error(final Throwable exception) {
		if (minimumLevelCoversError) {
			provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, exception, null, (Object[]) null);
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
	public void error(final Throwable exception, final String message) {
		if (minimumLevelCoversError) {
			provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, exception, message, (Object[]) null);
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
	public void error(final Throwable exception, final String message, final Object... arguments) {
		if (minimumLevelCoversError) {
			provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, exception, message, arguments);
		}
	}

	/**
	 * Checks if a given tag and severity level is covered by the logging providers minimum level.
	 *
	 * @param tag
	 *            Tag to check
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is covered, otherwise {@code false}
	 */
	private static boolean isCoveredByMinimumLevel(final String tag, final Level level) {
		return provider.getMinimumLevel(tag).ordinal() <= level.ordinal();
	}

}
