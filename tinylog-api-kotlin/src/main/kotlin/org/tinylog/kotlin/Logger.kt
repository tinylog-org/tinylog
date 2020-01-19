/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.kotlin

import java.util.concurrent.ConcurrentHashMap

import org.tinylog.Level
import org.tinylog.configuration.Configuration
import org.tinylog.format.AdvancedMessageFormatter
import org.tinylog.provider.ProviderRegistry

/**
 * Static logger for issuing log entries.
 */
object Logger {

	private const val STACKTRACE_DEPTH = 2

	private val formatter = AdvancedMessageFormatter(Configuration.getLocale())
	private val provider = ProviderRegistry.getLoggingProvider()

	// @formatter:off
	private val MINIMUM_LEVEL_COVERS_TRACE = isCoveredByMinimumLevel(Level.TRACE)
	private val MINIMUM_LEVEL_COVERS_DEBUG = isCoveredByMinimumLevel(Level.DEBUG)
	private val MINIMUM_LEVEL_COVERS_INFO  = isCoveredByMinimumLevel(Level.INFO)
	private val MINIMUM_LEVEL_COVERS_WARN  = isCoveredByMinimumLevel(Level.WARN)
	private val MINIMUM_LEVEL_COVERS_ERROR = isCoveredByMinimumLevel(Level.ERROR)
	// @formatter:on

	private val instance = TaggedLogger(null)
	private val loggers = ConcurrentHashMap<String, TaggedLogger>()

	/**
	 * Gets a tagged logger instance. Tags are case-sensitive.
	 *
	 * @param tag
	 * Tag for logger or `null` for receiving an untagged logger
	 * @return Logger instance
	 */
	fun tag(tag: String?): TaggedLogger {
		if (tag == null || tag.isEmpty()) {
			return instance
		} else {
			var logger = loggers[tag]
			if (logger == null) {
				logger = TaggedLogger(tag)
				val existing = loggers.putIfAbsent(tag, logger)
				return existing ?: logger
			} else {
				return logger
			}
		}
	}

	/**
	 * Checks whether log entries at [TRACE][Level.TRACE] level will be output.
	 *
	 * @return `true` if [TRACE][Level.TRACE] level is enabled, `false` if disabled
	 */
	fun isTraceEnabled(): Boolean {
		return MINIMUM_LEVEL_COVERS_TRACE && provider.isEnabled(STACKTRACE_DEPTH, null, Level.TRACE)
	}

	/**
	 * Logs a message at [TRACE][Level.TRACE] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun trace(message: Any?) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, null, message)
		}
	}

	/**
	 * Logs a message at [TRACE][Level.TRACE] level.
	 *
	 * @param message
	 * Text message to log
	 */
	fun trace(message: String) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, null, message)
		}
	}

	/**
	 * Logs a lazy message at [TRACE][Level.TRACE] level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 * Function that produces the message
	 */
	fun trace(message: () -> String) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, null, message.asSupplier())
		}
	}

	/**
	 * Logs a formatted message at [TRACE][Level.TRACE] level. "{}" placeholders will be replaced by given
	 * arguments.
	 *
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Arguments for formatted text message
	 */
	fun trace(message: String, vararg arguments: Any?) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, formatter, message, *arguments)
		}
	}

	/**
	 * Logs a formatted message at [TRACE][Level.TRACE] level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Functions that produce the arguments for formatted text message
	 */
	fun trace(message: String, vararg arguments: () -> Any?) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, null, formatter, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Logs an exception at [TRACE][Level.TRACE] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 */
	fun trace(exception: Throwable) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, null, null)
		}
	}

	/**
	 * Logs an exception with a custom message at [TRACE][Level.TRACE] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Text message to log
	 */
	fun trace(exception: Throwable, message: String) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, null, message)
		}
	}

	/**
	 * Logs an exception with a custom lazy message at [TRACE][Level.TRACE] level. The message will be only
	 * evaluated if the log entry is really output.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Function that produces the message
	 */
	fun trace(exception: Throwable, message: () -> String) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, null, message.asSupplier())
		}
	}

	/**
	 * Logs an exception with a formatted custom message at [TRACE][Level.TRACE] level. "{}" placeholders will be
	 * replaced by given arguments.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Arguments for formatted text message
	 */
	fun trace(exception: Throwable, message: String, vararg arguments: Any?) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, formatter, message, *arguments)
		}
	}

	/**
	 * Logs an exception with a formatted message at [TRACE][Level.TRACE] level. "{}" placeholders will be replaced
	 * by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Functions that produce the arguments for formatted text message
	 */
	fun trace(exception: Throwable, message: String, vararg arguments: () -> Any?) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, Level.TRACE, exception, formatter, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Checks whether log entries at [DEBUG][Level.DEBUG] level will be output.
	 *
	 * @return `true` if [DEBUG][Level.DEBUG] level is enabled, `false` if disabled
	 */
	fun isDebugEnabled(): Boolean {
		return MINIMUM_LEVEL_COVERS_DEBUG && provider.isEnabled(STACKTRACE_DEPTH, null, Level.DEBUG)
	}

	/**
	 * Logs a message at [DEBUG][Level.DEBUG] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun debug(message: Any?) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, null, message)
		}
	}

	/**
	 * Logs a message at [DEBUG][Level.DEBUG] level.
	 *
	 * @param message
	 * Text message to log
	 */
	fun debug(message: String) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, null, message)
		}
	}

	/**
	 * Logs a lazy message at [DEBUG][Level.DEBUG] level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 * Function that produces the message
	 */
	fun debug(message: () -> String) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, null, message.asSupplier())
		}
	}

	/**
	 * Logs a formatted message at [DEBUG][Level.DEBUG] level. "{}" placeholders will be replaced by given
	 * arguments.
	 *
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Arguments for formatted text message
	 */
	fun debug(message: String, vararg arguments: Any?) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, formatter, message, *arguments)
		}
	}

	/**
	 * Logs a formatted message at [DEBUG][Level.DEBUG] level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Functions that produce the arguments for formatted text message
	 */
	fun debug(message: String, vararg arguments: () -> Any?) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, null, formatter, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Logs an exception at [DEBUG][Level.DEBUG] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 */
	fun debug(exception: Throwable) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, null, null)
		}
	}

	/**
	 * Logs an exception with a custom message at [DEBUG][Level.DEBUG] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Text message to log
	 */
	fun debug(exception: Throwable, message: String) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, null, message)
		}
	}

	/**
	 * Logs an exception with a custom lazy message at [DEBUG][Level.DEBUG] level. The message will be only
	 * evaluated if the log entry is really output.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Function that produces the message
	 */
	fun debug(exception: Throwable, message: () -> String) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, null, message.asSupplier())
		}
	}

	/**
	 * Logs an exception with a formatted custom message at [DEBUG][Level.DEBUG] level. "{}" placeholders will be
	 * replaced by given arguments.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Arguments for formatted text message
	 */
	fun debug(exception: Throwable, message: String, vararg arguments: Any?) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, formatter, message, *arguments)
		}
	}

	/**
	 * Logs an exception with a formatted message at [DEBUG][Level.DEBUG] level. "{}" placeholders will be replaced
	 * by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Functions that produce the arguments for formatted text message
	 */
	fun debug(exception: Throwable, message: String, vararg arguments: () -> Any?) {
		if (MINIMUM_LEVEL_COVERS_DEBUG) {
			provider.log(STACKTRACE_DEPTH, null, Level.DEBUG, exception, formatter, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Checks whether log entries at [INFO][Level.INFO] level will be output.
	 *
	 * @return `true` if [INFO][Level.INFO] level is enabled, `false` if disabled
	 */
	fun isInfoEnabled(): Boolean {
		return MINIMUM_LEVEL_COVERS_INFO && provider.isEnabled(STACKTRACE_DEPTH, null, Level.INFO)
	}

	/**
	 * Logs a message at [INFO][Level.INFO] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun info(message: Any?) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, null, message)
		}
	}

	/**
	 * Logs a message at [INFO][Level.INFO] level.
	 *
	 * @param message
	 * Text message to log
	 */
	fun info(message: String) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, null, message)
		}
	}

	/**
	 * Logs a lazy message at [INFO][Level.INFO] level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 * Function that produces the message
	 */
	fun info(message: () -> String) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, null, message.asSupplier())
		}
	}

	/**
	 * Logs a formatted message at [INFO][Level.INFO] level. "{}" placeholders will be replaced by given
	 * arguments.
	 *
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Arguments for formatted text message
	 */
	fun info(message: String, vararg arguments: Any?) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, formatter, message, *arguments)
		}
	}

	/**
	 * Logs a formatted message at [INFO][Level.INFO] level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Functions that produce the arguments for formatted text message
	 */
	fun info(message: String, vararg arguments: () -> Any?) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, null, formatter, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Logs an exception at [INFO][Level.INFO] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 */
	fun info(exception: Throwable) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, null, null)
		}
	}

	/**
	 * Logs an exception with a custom message at [INFO][Level.INFO] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Text message to log
	 */
	fun info(exception: Throwable, message: String) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, null, message)
		}
	}

	/**
	 * Logs an exception with a custom lazy message at [INFO][Level.INFO] level. The message will be only
	 * evaluated if the log entry is really output.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Function that produces the message
	 */
	fun info(exception: Throwable, message: () -> String) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, null, message.asSupplier())
		}
	}

	/**
	 * Logs an exception with a formatted custom message at [INFO][Level.INFO] level. "{}" placeholders will be
	 * replaced by given arguments.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Arguments for formatted text message
	 */
	fun info(exception: Throwable, message: String, vararg arguments: Any?) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, formatter, message, *arguments)
		}
	}

	/**
	 * Logs an exception with a formatted message at [INFO][Level.INFO] level. "{}" placeholders will be replaced
	 * by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Functions that produce the arguments for formatted text message
	 */
	fun info(exception: Throwable, message: String, vararg arguments: () -> Any?) {
		if (MINIMUM_LEVEL_COVERS_INFO) {
			provider.log(STACKTRACE_DEPTH, null, Level.INFO, exception, formatter, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Checks whether log entries at [WARN][Level.WARN] level will be output.
	 *
	 * @return `true` if [WARN][Level.WARN] level is enabled, `false` if disabled
	 */
	fun isWarnEnabled(): Boolean {
		return MINIMUM_LEVEL_COVERS_WARN && provider.isEnabled(STACKTRACE_DEPTH, null, Level.WARN)
	}

	/**
	 * Logs a message at [WARN][Level.WARN] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun warn(message: Any?) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, null, message)
		}
	}

	/**
	 * Logs a message at [WARN][Level.WARN] level.
	 *
	 * @param message
	 * Text message to log
	 */
	fun warn(message: String) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, null, message)
		}
	}

	/**
	 * Logs a lazy message at [WARN][Level.WARN] level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 * Function that produces the message
	 */
	fun warn(message: () -> String) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, null, message.asSupplier())
		}
	}

	/**
	 * Logs a formatted message at [WARN][Level.WARN] level. "{}" placeholders will be replaced by given
	 * arguments.
	 *
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Arguments for formatted text message
	 */
	fun warn(message: String, vararg arguments: Any?) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, formatter, message, *arguments)
		}
	}

	/**
	 * Logs a formatted message at [WARN][Level.WARN] level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Functions that produce the arguments for formatted text message
	 */
	fun warn(message: String, vararg arguments: () -> Any?) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, null, formatter, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Logs an exception at [WARN][Level.WARN] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 */
	fun warn(exception: Throwable) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, null, null)
		}
	}

	/**
	 * Logs an exception with a custom message at [WARN][Level.WARN] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Text message to log
	 */
	fun warn(exception: Throwable, message: String) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, null, message)
		}
	}

	/**
	 * Logs an exception with a custom lazy message at [WARN][Level.WARN] level. The message will be only
	 * evaluated if the log entry is really output.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Function that produces the message
	 */
	fun warn(exception: Throwable, message: () -> String) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, null, message.asSupplier())
		}
	}

	/**
	 * Logs an exception with a formatted custom message at [WARN][Level.WARN] level. "{}" placeholders will be
	 * replaced by given arguments.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Arguments for formatted text message
	 */
	fun warn(exception: Throwable, message: String, vararg arguments: Any?) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, formatter, message, *arguments)
		}
	}

	/**
	 * Logs an exception with a formatted message at [WARN][Level.WARN] level. "{}" placeholders will be replaced
	 * by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Functions that produce the arguments for formatted text message
	 */
	fun warn(exception: Throwable, message: String, vararg arguments: () -> Any?) {
		if (MINIMUM_LEVEL_COVERS_WARN) {
			provider.log(STACKTRACE_DEPTH, null, Level.WARN, exception, formatter, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Checks whether log entries at [ERROR][Level.ERROR] level will be output.
	 *
	 * @return `true` if [ERROR][Level.ERROR] level is enabled, `false` if disabled
	 */
	fun isErrorEnabled(): Boolean {
		return MINIMUM_LEVEL_COVERS_ERROR && provider.isEnabled(STACKTRACE_DEPTH, null, Level.ERROR)
	}

	/**
	 * Logs a message at [ERROR][Level.ERROR] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun error(message: Any?) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, null, message)
		}
	}

	/**
	 * Logs a message at [ERROR][Level.ERROR] level.
	 *
	 * @param message
	 * Text message to log
	 */
	fun error(message: String) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, null, message)
		}
	}

	/**
	 * Logs a lazy message at [ERROR][Level.ERROR] level. The message will be only evaluated if the log entry is
	 * really output.
	 *
	 * @param message
	 * Function that produces the message
	 */
	fun error(message: () -> String) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, null, message.asSupplier())
		}
	}

	/**
	 * Logs a formatted message at [ERROR][Level.ERROR] level. "{}" placeholders will be replaced by given
	 * arguments.
	 *
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Arguments for formatted text message
	 */
	fun error(message: String, vararg arguments: Any?) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, formatter, message, *arguments)
		}
	}

	/**
	 * Logs a formatted message at [ERROR][Level.ERROR] level. "{}" placeholders will be replaced by given lazy
	 * arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Functions that produce the arguments for formatted text message
	 */
	fun error(message: String, vararg arguments: () -> Any?) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, null, formatter, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Logs an exception at [ERROR][Level.ERROR] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 */
	fun error(exception: Throwable) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, null, null)
		}
	}

	/**
	 * Logs an exception with a custom message at [ERROR][Level.ERROR] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Text message to log
	 */
	fun error(exception: Throwable, message: String) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, null, message)
		}
	}

	/**
	 * Logs an exception with a custom lazy message at [ERROR][Level.ERROR] level. The message will be only
	 * evaluated if the log entry is really output.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Function that produces the message
	 */
	fun error(exception: Throwable, message: () -> String) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, null, message.asSupplier())
		}
	}

	/**
	 * Logs an exception with a formatted custom message at [ERROR][Level.ERROR] level. "{}" placeholders will be
	 * replaced by given arguments.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Arguments for formatted text message
	 */
	fun error(exception: Throwable, message: String, vararg arguments: Any?) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, formatter, message, *arguments)
		}
	}

	/**
	 * Logs an exception with a formatted message at [ERROR][Level.ERROR] level. "{}" placeholders will be replaced
	 * by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 * @param message
	 * Formatted text message to log
	 * @param arguments
	 * Functions that produce the arguments for formatted text message
	 */
	fun error(exception: Throwable, message: String, vararg arguments: () -> Any?) {
		if (MINIMUM_LEVEL_COVERS_ERROR) {
			provider.log(STACKTRACE_DEPTH, null, Level.ERROR, exception, formatter, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Checks if a given severity level is covered by the logging provider's minimum level.
	 *
	 * @param level
	 * Severity level to check
	 * @return `true` if given severity level is covered, otherwise `false`
	 */
	private fun isCoveredByMinimumLevel(level: Level): Boolean {
		return provider.getMinimumLevel(null).ordinal <= level.ordinal
	}

}