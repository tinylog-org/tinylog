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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.tinylog.configuration.Configuration;
import org.tinylog.format.AdvancedMessageFormatter;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * Logger for issuing tagged log entries. Tagged loggers can be received by calling {@link Logger#tag(String)} or
 * {@link Logger#tags(String...)}.
 *
 * @see Logger#tag(String)
 * @see Logger#tags(String...)
 */
public final class TaggedLogger {

	private static final int STACKTRACE_DEPTH = 2;

	private static final MessageFormatter formatter = new AdvancedMessageFormatter(
			Configuration.getLocale(),
			Configuration.isEscapingEnabled()
	);

	private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();

	private final Set<String> traceTags;
	private final Set<String> debugTags;
	private final Set<String> infoTags;
	private final Set<String> warnTags;
	private final Set<String> errorTags;

	private final boolean minimumLevelCoversTrace;
	private final boolean minimumLevelCoversDebug;
	private final boolean minimumLevelCoversInfo;
	private final boolean minimumLevelCoversWarn;
	private final boolean minimumLevelCoversError;

	private final Set<String> tags;

	/**
	 * @param tag
	 *            Case-sensitive tag for logger instance
	 */
	TaggedLogger(final String tag) {
		this(Collections.singleton(tag));
	}

	/**
	 * @param tags anything logged from this instance will have these tags.
	 */
	TaggedLogger(final Set<String> tags) {
		this.tags = Collections.unmodifiableSet(tags);

		// @formatter:off
		traceTags = getCoveredTags(tags, Level.TRACE);
		debugTags = getCoveredTags(tags, Level.DEBUG);
		infoTags  = getCoveredTags(tags, Level.INFO);
		warnTags  = getCoveredTags(tags, Level.WARN);
		errorTags = getCoveredTags(tags, Level.ERROR);

		minimumLevelCoversTrace	= !traceTags.isEmpty();
		minimumLevelCoversDebug	= !debugTags.isEmpty();
		minimumLevelCoversInfo	= !infoTags.isEmpty();
		minimumLevelCoversWarn	= !warnTags.isEmpty();
		minimumLevelCoversError	= !errorTags.isEmpty();
		// @formatter:on
	}

	/**
	 * Checks whether log entries at {@link Level#TRACE TRACE} level will be output.
	 *
	 * @return {@code true} if {@link Level#TRACE TRACE} level is enabled, {@code false} if disabled
	 */
	public boolean isTraceEnabled() {
		return minimumLevelCoversTrace && anyEnabled(traceTags, Level.TRACE);
	}

	/**
	 * Logs a message at {@link Level#TRACE TRACE} level.
	 *
	 * @param message
	 *            String or any other object with a meaningful {@link #toString()} method
	 */
	public void trace(final Object message) {
		if (minimumLevelCoversTrace) {
			for (String tag : traceTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, null, null, message, (Object[]) null);
			}
		}
	}

	/**
	 * Logs a lazy message at {@link Level#TRACE TRACE} level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public void trace(final Supplier<?> message) {
		if (minimumLevelCoversTrace) {
			for (String tag : traceTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, null, null, message, (Object[]) null);
			}
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
			for (String tag : traceTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, null, formatter, message, arguments);
			}
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
	public void trace(final String message, final Supplier<?>... arguments) {
		if (minimumLevelCoversTrace) {
			for (String tag : traceTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, null, formatter, message, (Object[]) arguments);
			}
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
			for (String tag : traceTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, exception, null, null, (Object[]) null);
			}
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
			for (String tag : traceTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, exception, null, message, (Object[]) null);
			}
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
	public void trace(final Throwable exception, final Supplier<String> message) {
		if (minimumLevelCoversTrace) {
			for (String tag : traceTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, exception, null, message, (Object[]) null);
			}
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
			for (String tag : traceTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, exception, formatter, message, arguments);
			}
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
	public void trace(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (minimumLevelCoversTrace) {
			for (String tag : traceTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.TRACE, exception, formatter, message, (Object[]) arguments);
			}
		}
	}

	/**
	 * Checks whether log entries at {@link Level#DEBUG DEBUG} level will be output.
	 *
	 * @return {@code true} if {@link Level#DEBUG DEBUG} level is enabled, {@code false} if disabled
	 */
	public boolean isDebugEnabled() {
		return minimumLevelCoversDebug && anyEnabled(debugTags, Level.DEBUG);
	}

	/**
	 * Logs a message at {@link Level#DEBUG DEBUG} level.
	 *
	 * @param message
	 *            String or any other object with a meaningful {@link #toString()} method
	 */
	public void debug(final Object message) {
		if (minimumLevelCoversDebug) {
			for (String tag : debugTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, null, null, message, (Object[]) null);
			}
		}
	}

	/**
	 * Logs a lazy message at {@link Level#DEBUG DEBUG} level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public void debug(final Supplier<?> message) {
		if (minimumLevelCoversDebug) {
			for (String tag : debugTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, null, null, message, (Object[]) null);
			}
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
			for (String tag : debugTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, null, formatter, message, arguments);
			}
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
	public void debug(final String message, final Supplier<?>... arguments) {
		if (minimumLevelCoversDebug) {
			for (String tag : debugTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, null, formatter, message, (Object[]) arguments);
			}
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
			for (String tag : debugTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, exception, null, null, (Object[]) null);
			}
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
			for (String tag : debugTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, exception, null, message, (Object[]) null);
			}
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
	public void debug(final Throwable exception, final Supplier<String> message) {
		if (minimumLevelCoversDebug) {
			for (String tag : debugTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, exception, null, message, (Object[]) null);
			}
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
			for (String tag : debugTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, exception, formatter, message, arguments);
			}
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
	public void debug(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (minimumLevelCoversDebug) {
			for (String tag : debugTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.DEBUG, exception, formatter, message, (Object[]) arguments);
			}
		}
	}

	/**
	 * Checks whether log entries at {@link Level#INFO INFO} level will be output.
	 *
	 * @return {@code true} if {@link Level#INFO INFO} level is enabled, {@code false} if disabled
	 */
	public boolean isInfoEnabled() {
		return minimumLevelCoversInfo && anyEnabled(infoTags, Level.INFO);
	}

	/**
	 * Logs a message at {@link Level#INFO INFO} level.
	 *
	 * @param message
	 *            String or any other object with a meaningful {@link #toString()} method
	 */
	public void info(final Object message) {
		if (minimumLevelCoversInfo) {
			for (String tag : infoTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.INFO, null, null, message, (Object[]) null);
			}
		}
	}

	/**
	 * Logs a lazy message at {@link Level#INFO INFO} level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public void info(final Supplier<?> message) {
		if (minimumLevelCoversInfo) {
			for (String tag : infoTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.INFO, null, null, message, (Object[]) null);
			}
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
			for (String tag : infoTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.INFO, null, formatter, message, arguments);
			}
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
	public void info(final String message, final Supplier<?>... arguments) {
		if (minimumLevelCoversInfo) {
			for (String tag : infoTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.INFO, null, formatter, message, (Object[]) arguments);
			}
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
			for (String tag : infoTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.INFO, exception, null, null, (Object[]) null);
			}
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
			for (String tag : infoTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.INFO, exception, null, message, (Object[]) null);
			}
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
	public void info(final Throwable exception, final Supplier<String> message) {
		if (minimumLevelCoversInfo) {
			for (String tag : infoTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.INFO, exception, null, message, (Object[]) null);
			}
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
			for (String tag : infoTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.INFO, exception, formatter, message, arguments);
			}
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
	public void info(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (minimumLevelCoversInfo) {
			for (String tag : infoTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.INFO, exception, formatter, message, (Object[]) arguments);
			}
		}
	}

	/**
	 * Checks whether log entries at {@link Level#WARN WARN} level will be output.
	 *
	 * @return {@code true} if {@link Level#WARN WARN} level is enabled, {@code false} if disabled
	 */
	public boolean isWarnEnabled() {
		return minimumLevelCoversWarn && anyEnabled(warnTags, Level.WARN);
	}

	/**
	 * Logs a message at {@link Level#WARN WARN} level.
	 *
	 * @param message
	 *            String or any other object with a meaningful {@link #toString()} method
	 */
	public void warn(final Object message) {
		if (minimumLevelCoversWarn) {
			for (String tag : warnTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.WARN, null, null, message, (Object[]) null);
			}
		}
	}

	/**
	 * Logs a lazy message at {@link Level#WARN WARN} level. The message will be only evaluated if the log entry
	 * is really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public void warn(final Supplier<?> message) {
		if (minimumLevelCoversWarn) {
			for (String tag : warnTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.WARN, null, null, message, (Object[]) null);
			}
		}
	}

	/**
	 * Logs a formatted message at {@link Level#WARN WARN} level. "{}" placeholders will be replaced by given
	 * arguments.
	 *
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Arguments for formatted text message
	 */
	public void warn(final String message, final Object... arguments) {
		if (minimumLevelCoversWarn) {
			for (String tag : warnTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.WARN, null, formatter, message, arguments);
			}
		}
	}

	/**
	 * Logs a formatted message at {@link Level#WARN WARN} level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public void warn(final String message, final Supplier<?>... arguments) {
		if (minimumLevelCoversWarn) {
			for (String tag : warnTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.WARN, null, formatter, message, (Object[]) arguments);
			}
		}
	}

	/**
	 * Logs an exception at {@link Level#WARN WARN} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public void warn(final Throwable exception) {
		if (minimumLevelCoversWarn) {
			for (String tag : warnTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.WARN, exception, null, null, (Object[]) null);
			}
		}
	}

	/**
	 * Logs an exception with a custom message at {@link Level#WARN WARN} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Text message to log
	 */
	public void warn(final Throwable exception, final String message) {
		if (minimumLevelCoversWarn) {
			for (String tag : warnTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.WARN, exception, null, message, (Object[]) null);
			}
		}
	}

	/**
	 * Logs an exception with a custom lazy message at {@link Level#WARN WARN} level. The message will be only
	 * evaluated if the log entry is really output.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Function that produces the message
	 */
	public void warn(final Throwable exception, final Supplier<String> message) {
		if (minimumLevelCoversWarn) {
			for (String tag : warnTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.WARN, exception, null, message, (Object[]) null);
			}
		}
	}

	/**
	 * Logs an exception with a formatted custom message at {@link Level#WARN WARN} level. "{}" placeholders will
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
			for (String tag : warnTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.WARN, exception, formatter, message, arguments);
			}
		}
	}

	/**
	 * Logs an exception with a formatted message at {@link Level#WARN WARN} level. "{}" placeholders will be
	 * replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 * @param message
	 *            Formatted text message to log
	 * @param arguments
	 *            Functions that produce the arguments for formatted text message
	 */
	public void warn(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (minimumLevelCoversWarn) {
			for (String tag : warnTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.WARN, exception, formatter, message, (Object[]) arguments);
			}
		}
	}

	/**
	 * Checks whether log entries at {@link Level#ERROR ERROR} level will be output.
	 *
	 * @return {@code true} if {@link Level#ERROR ERROR} level is enabled, {@code false} if disabled
	 */
	public boolean isErrorEnabled() {
		return minimumLevelCoversError && anyEnabled(errorTags, Level.ERROR);
	}

	/**
	 * Logs a message at {@link Level#ERROR ERROR} level.
	 *
	 * @param message
	 *            String or any other object with a meaningful {@link #toString()} method
	 */
	public void error(final Object message) {
		if (minimumLevelCoversError) {
			for (String tag : errorTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, null, null, message, (Object[]) null);
			}
		}
	}

	/**
	 * Logs a lazy message at {@link Level#ERROR ERROR} level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public void error(final Supplier<?> message) {
		if (minimumLevelCoversError) {
			for (String tag : errorTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, null, null, message, (Object[]) null);
			}
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
			for (String tag : errorTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, null, formatter, message, arguments);
			}
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
	public void error(final String message, final Supplier<?>... arguments) {
		if (minimumLevelCoversError) {
			for (String tag : errorTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, null, formatter, message, (Object[]) arguments);
			}
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
			for (String tag : errorTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, exception, null, null, (Object[]) null);
			}
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
			for (String tag : errorTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, exception, null, message, (Object[]) null);
			}
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
	public void error(final Throwable exception, final Supplier<String> message) {
		if (minimumLevelCoversError) {
			for (String tag : errorTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, exception, null, message, (Object[]) null);
			}
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
			for (String tag : errorTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, exception, formatter, message, arguments);
			}
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
	public void error(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (minimumLevelCoversError) {
			for (String tag : errorTags) {
				provider.log(STACKTRACE_DEPTH, tag, Level.ERROR, exception, formatter, message, (Object[]) arguments);
			}
		}
	}

	/**
	 * Checks if any of the tags in a given {@link Set} is enabled for a given {@link Level}.
	 *
	 * @param tags
	 *            {@link Set} of tags to check
	 * @param level
	 *            the log level that at least one of the tags must be enabled
	 * @return {@code true} if the level is enabled for at least one of the tags in the given {@link Set}. otherwise, {@code false}
	 */
	private static boolean anyEnabled(final Set<String> tags, final Level level) {
		for (String tag : tags) {
			if (provider.isEnabled(STACKTRACE_DEPTH + 1, tag, level)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Filters a given {@link Set} of tags by whether they are covered by a level.
	 *
	 * @param tags
	 *            {@link Set} of tags to go through
	 * @param level
	 *            the minimum that the tag must cover
	 * @return the {@link Set} of tags that are covered by the level
	 */
	private static Set<String> getCoveredTags(final Set<String> tags, final Level level) {
		Set<String> filtered = new HashSet<String>();
		for (String tag : tags) {
			if (isCoveredByMinimumLevel(tag, level)) {
				filtered.add(tag);
			}
		}
		return Collections.unmodifiableSet(filtered);
	}

	/**
	 * Checks if a given tag and severity level is covered by the logging provider's minimum level.
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
