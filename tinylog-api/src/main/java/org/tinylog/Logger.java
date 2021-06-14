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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.tinylog.configuration.Configuration;
import org.tinylog.format.AdvancedMessageFormatter;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * Static logger for issuing log entries.
 */
public final class Logger {

	private static final int STACKTRACE_DEPTH = 2;
	
	private static final MessageFormatter formatter = new AdvancedMessageFormatter(
			Configuration.getLocale(),
			Configuration.isEscapingEnabled()
		);

	private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();

	// @formatter:off
	private static final boolean MINIMUM_LEVEL_COVERS_TRACE = isCoveredByMinimumLevel(Level.TRACE);
	private static final boolean MINIMUM_LEVEL_COVERS_DEBUG = isCoveredByMinimumLevel(Level.DEBUG);
	private static final boolean MINIMUM_LEVEL_COVERS_INFO  = isCoveredByMinimumLevel(Level.INFO);
	private static final boolean MINIMUM_LEVEL_COVERS_WARN  = isCoveredByMinimumLevel(Level.WARN);
	private static final boolean MINIMUM_LEVEL_COVERS_ERROR = isCoveredByMinimumLevel(Level.ERROR);
	// @formatter:on

	private static final TaggedLogger instance = new TaggedLogger((String) null);
	private static final ConcurrentMap<Set<String>, TaggedLogger> loggers = new ConcurrentHashMap<Set<String>, TaggedLogger>();

	static {
		loggers.put(toUnmodifiableTagsSet((String) null), instance);
	}

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
			return tags(tag);
		}
	}

	/**
	 * Gets a tagged logger instance that logs to multiple tags. Tags are case-sensitive.
	 *
	 * @param tags
	 *            Tags for the logger or nothing for an untagged logger. If specified, each tag should be unique
	 * @return Logger instance
	 */
	public static TaggedLogger tags(final String...tags) {
		if (tags == null || tags.length == 0) {
			return instance;
		} else {
			Set<String> tagsSet = toUnmodifiableTagsSet(tags);
			TaggedLogger logger = loggers.get(tagsSet);
			if (logger == null) {
				logger = new TaggedLogger(tagsSet);
				TaggedLogger existing = loggers.putIfAbsent(tagsSet, logger);
				return existing == null ? logger : existing;
			} else {
				return logger;
			}
		}
	}

	/**
	 * Puts the given tags into an immutable {@link Set}. Any "empty" tags are treated as the same as {@code null} tags
	 *
	 * @param tags
	 *            The tags to put into the immutable {@link Set}
	 * @return Immutable {@link Set} of strings.
	 */
	private static Set<String> toUnmodifiableTagsSet(final String... tags) {
		Set<String> set = new HashSet<String>();
		for (String entry: tags) {
			if (entry == null || entry.isEmpty()) {
				set.add(null);
			} else {
				set.add(entry);
			}
		}
		return Collections.unmodifiableSet(set);
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
	 *            String or any other object with a meaningful {@link #toString()} method
	 */
	public static void trace(final Object message) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, formatter, message, arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, formatter, message, (Object[]) arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, null, null, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, formatter, message, arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, formatter, message, (Object[]) arguments);
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
	 *            String or any other object with a meaningful {@link #toString()} method
	 */
	public static void debug(final Object message) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, formatter, message, arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, formatter, message, (Object[]) arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, null, null, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, formatter, message, arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, formatter, message, (Object[]) arguments);
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
	 *            String or any other object with a meaningful {@link #toString()} method
	 */
	public static void info(final Object message) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, formatter, message, arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, formatter, message, (Object[]) arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, null, null, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, formatter, message, arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, formatter, message, (Object[]) arguments);
		}
	}

	/**
	 * Checks whether log entries at {@link Level#WARN WARN} level will be output.
	 *
	 * @return {@code true} if {@link Level#WARN WARN} level is enabled, {@code false} if disabled
	 */
	public static boolean isWarnEnabled() {
		return MINIMUM_LEVEL_COVERS_WARN && provider.isEnabled(STACKTRACE_DEPTH, null, Level.WARN);
	}

	/**
	 * Logs a message at {@link Level#WARN WARN} level.
	 *
	 * @param message
	 *            String or any other object with a meaningful {@link #toString()} method
	 */
	public static void warn(final Object message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, null, message, (Object[]) null);
		}
	}

	/**
	 * Logs a lazy message at {@link Level#WARN WARN} level. The message will be only evaluated if the log entry
	 * is really output.
	 *
	 * @param message
	 *            Function that produces the message
	 */
	public static void warn(final Supplier<?> message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, null, message, (Object[]) null);
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
	public static void warn(final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, formatter, message, arguments);
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
	public static void warn(final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, formatter, message, (Object[]) arguments);
		}
	}

	/**
	 * Logs an exception at {@link Level#WARN WARN} level.
	 *
	 * @param exception
	 *            Caught exception or any other throwable to log
	 */
	public static void warn(final Throwable exception) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, null, null, (Object[]) null);
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
	public static void warn(final Throwable exception, final String message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, null, message, (Object[]) null);
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
	public static void warn(final Throwable exception, final Supplier<String> message) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, null, message, (Object[]) null);
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
	public static void warn(final Throwable exception, final String message, final Object... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, formatter, message, arguments);
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
	public static void warn(final Throwable exception, final String message, final Supplier<?>... arguments) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, formatter, message, (Object[]) arguments);
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
	 *            String or any other object with a meaningful {@link #toString()} method
	 */
	public static void error(final Object message) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, formatter, message, arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, formatter, message, (Object[]) arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, null, null, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, null, message, (Object[]) null);
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
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, formatter, message, arguments);
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
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, formatter, message, (Object[]) arguments);
		}
	}

	/**
	 * Checks if a given severity level is covered by the logging provider's minimum level.
	 *
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is covered, otherwise {@code false}
	 */
	private static boolean isCoveredByMinimumLevel(final Level level) {
		return provider.getMinimumLevel(null).ordinal() <= level.ordinal();
	}

}
