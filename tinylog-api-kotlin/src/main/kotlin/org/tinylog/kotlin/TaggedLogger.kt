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

import org.tinylog.Level
import org.tinylog.provider.ProviderRegistry

/**
 * Logger for issuing tagged log entries. Tagged loggers can be received by calling [Logger.tag].
 *
 * @param tag
 * Case-sensitive tag for logger instance
 * @see Logger.tag
 */
class TaggedLogger internal constructor(private val tag: String?) {

	private val stackTraceDepth = 2
	private val provider = ProviderRegistry.getLoggingProvider()

	// @formatter:off
	private val minimumLevelCoversTrace = isCoveredByMinimumLevel(tag, Level.TRACE)
	private val minimumLevelCoversDebug = isCoveredByMinimumLevel(tag, Level.TRACE)
	private val minimumLevelCoversInfo  = isCoveredByMinimumLevel(tag, Level.TRACE)
	private val minimumLevelCoversWarn  = isCoveredByMinimumLevel(tag, Level.TRACE)
	private val minimumLevelCoversError = isCoveredByMinimumLevel(tag, Level.TRACE)
	// @formatter:on

	/**
	 * Checks whether log entries at [TRACE][Level.TRACE] level will be output.
	 *
	 * @return `true` if [TRACE][Level.TRACE] level is enabled, `false` if disabled
	 */
	fun isTraceEnabled(): Boolean {
		return minimumLevelCoversTrace && provider.isEnabled(stackTraceDepth, tag, Level.TRACE)
	}

	/**
	 * Logs a message at [TRACE][Level.TRACE] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun trace(message: Any?) {
		if (minimumLevelCoversTrace) {
			provider.log(stackTraceDepth, tag, Level.TRACE, null, message)
		}
	}

	/**
	 * Logs a message at [TRACE][Level.TRACE] level.
	 *
	 * @param message
	 * Text message to log
	 */
	fun trace(message: String) {
		if (minimumLevelCoversTrace) {
			provider.log(stackTraceDepth, tag, Level.TRACE, null, message)
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
		if (minimumLevelCoversTrace) {
			provider.log(stackTraceDepth, tag, Level.TRACE, null, message.asSupplier())
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
		if (minimumLevelCoversTrace) {
			provider.log(stackTraceDepth, tag, Level.TRACE, null, message, *arguments)
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
		if (minimumLevelCoversTrace) {
			provider.log(stackTraceDepth, tag, Level.TRACE, null, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Logs an exception at [TRACE][Level.TRACE] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 */
	fun trace(exception: Throwable) {
		if (minimumLevelCoversTrace) {
			provider.log(stackTraceDepth, tag, Level.TRACE, exception, null)
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
		if (minimumLevelCoversTrace) {
			provider.log(stackTraceDepth, tag, Level.TRACE, exception, message)
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
		if (minimumLevelCoversTrace) {
			provider.log(stackTraceDepth, tag, Level.TRACE, exception, message.asSupplier())
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
		if (minimumLevelCoversTrace) {
			provider.log(stackTraceDepth, tag, Level.TRACE, exception, message, *arguments)
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
		if (minimumLevelCoversTrace) {
			provider.log(stackTraceDepth, tag, Level.TRACE, exception, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Checks whether log entries at [DEBUG][Level.DEBUG] level will be output.
	 *
	 * @return `true` if [DEBUG][Level.DEBUG] level is enabled, `false` if disabled
	 */
	fun isDebugEnabled(): Boolean {
		return minimumLevelCoversDebug && provider.isEnabled(stackTraceDepth, tag, Level.DEBUG)
	}

	/**
	 * Logs a message at [DEBUG][Level.DEBUG] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun debug(message: Any?) {
		if (minimumLevelCoversDebug) {
			provider.log(stackTraceDepth, tag, Level.DEBUG, null, message)
		}
	}

	/**
	 * Logs a message at [DEBUG][Level.DEBUG] level.
	 *
	 * @param message
	 * Text message to log
	 */
	fun debug(message: String) {
		if (minimumLevelCoversDebug) {
			provider.log(stackTraceDepth, tag, Level.DEBUG, null, message)
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
		if (minimumLevelCoversDebug) {
			provider.log(stackTraceDepth, tag, Level.DEBUG, null, message.asSupplier())
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
		if (minimumLevelCoversDebug) {
			provider.log(stackTraceDepth, tag, Level.DEBUG, null, message, *arguments)
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
		if (minimumLevelCoversDebug) {
			provider.log(stackTraceDepth, tag, Level.DEBUG, null, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Logs an exception at [DEBUG][Level.DEBUG] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 */
	fun debug(exception: Throwable) {
		if (minimumLevelCoversDebug) {
			provider.log(stackTraceDepth, tag, Level.DEBUG, exception, null)
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
		if (minimumLevelCoversDebug) {
			provider.log(stackTraceDepth, tag, Level.DEBUG, exception, message)
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
		if (minimumLevelCoversDebug) {
			provider.log(stackTraceDepth, tag, Level.DEBUG, exception, message.asSupplier())
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
		if (minimumLevelCoversDebug) {
			provider.log(stackTraceDepth, tag, Level.DEBUG, exception, message, *arguments)
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
		if (minimumLevelCoversDebug) {
			provider.log(stackTraceDepth, tag, Level.DEBUG, exception, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Checks whether log entries at [INFO][Level.INFO] level will be output.
	 *
	 * @return `true` if [INFO][Level.INFO] level is enabled, `false` if disabled
	 */
	fun isInfoEnabled(): Boolean {
		return minimumLevelCoversInfo && provider.isEnabled(stackTraceDepth, tag, Level.INFO)
	}

	/**
	 * Logs a message at [INFO][Level.INFO] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun info(message: Any?) {
		if (minimumLevelCoversInfo) {
			provider.log(stackTraceDepth, tag, Level.INFO, null, message)
		}
	}

	/**
	 * Logs a message at [INFO][Level.INFO] level.
	 *
	 * @param message
	 * Text message to log
	 */
	fun info(message: String) {
		if (minimumLevelCoversInfo) {
			provider.log(stackTraceDepth, tag, Level.INFO, null, message)
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
		if (minimumLevelCoversInfo) {
			provider.log(stackTraceDepth, tag, Level.INFO, null, message.asSupplier())
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
		if (minimumLevelCoversInfo) {
			provider.log(stackTraceDepth, tag, Level.INFO, null, message, *arguments)
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
		if (minimumLevelCoversInfo) {
			provider.log(stackTraceDepth, tag, Level.INFO, null, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Logs an exception at [INFO][Level.INFO] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 */
	fun info(exception: Throwable) {
		if (minimumLevelCoversInfo) {
			provider.log(stackTraceDepth, tag, Level.INFO, exception, null)
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
		if (minimumLevelCoversInfo) {
			provider.log(stackTraceDepth, tag, Level.INFO, exception, message)
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
		if (minimumLevelCoversInfo) {
			provider.log(stackTraceDepth, tag, Level.INFO, exception, message.asSupplier())
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
		if (minimumLevelCoversInfo) {
			provider.log(stackTraceDepth, tag, Level.INFO, exception, message, *arguments)
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
		if (minimumLevelCoversInfo) {
			provider.log(stackTraceDepth, tag, Level.INFO, exception, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Checks whether log entries at [WARN][Level.WARN] level will be output.
	 *
	 * @return `true` if [WARN][Level.WARN] level is enabled, `false` if disabled
	 */
	fun isWarnEnabled(): Boolean {
		return minimumLevelCoversWarn && provider.isEnabled(stackTraceDepth, tag, Level.WARN)
	}

	/**
	 * Logs a message at [WARN][Level.WARN] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun warn(message: Any?) {
		if (minimumLevelCoversWarn) {
			provider.log(stackTraceDepth, tag, Level.WARN, null, message)
		}
	}

	/**
	 * Logs a message at [WARN][Level.WARN] level.
	 *
	 * @param message
	 * Text message to log
	 */
	fun warn(message: String) {
		if (minimumLevelCoversWarn) {
			provider.log(stackTraceDepth, tag, Level.WARN, null, message)
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
		if (minimumLevelCoversWarn) {
			provider.log(stackTraceDepth, tag, Level.WARN, null, message.asSupplier())
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
		if (minimumLevelCoversWarn) {
			provider.log(stackTraceDepth, tag, Level.WARN, null, message, *arguments)
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
		if (minimumLevelCoversWarn) {
			provider.log(stackTraceDepth, tag, Level.WARN, null, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Logs an exception at [WARN][Level.WARN] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 */
	fun warn(exception: Throwable) {
		if (minimumLevelCoversWarn) {
			provider.log(stackTraceDepth, tag, Level.WARN, exception, null)
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
		if (minimumLevelCoversWarn) {
			provider.log(stackTraceDepth, tag, Level.WARN, exception, message)
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
		if (minimumLevelCoversWarn) {
			provider.log(stackTraceDepth, tag, Level.WARN, exception, message.asSupplier())
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
		if (minimumLevelCoversWarn) {
			provider.log(stackTraceDepth, tag, Level.WARN, exception, message, *arguments)
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
		if (minimumLevelCoversWarn) {
			provider.log(stackTraceDepth, tag, Level.WARN, exception, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Checks whether log entries at [ERROR][Level.ERROR] level will be output.
	 *
	 * @return `true` if [ERROR][Level.ERROR] level is enabled, `false` if disabled
	 */
	fun isErrorEnabled(): Boolean {
		return minimumLevelCoversError && provider.isEnabled(stackTraceDepth, tag, Level.ERROR)
	}

	/**
	 * Logs a message at [ERROR][Level.ERROR] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun error(message: Any?) {
		if (minimumLevelCoversError) {
			provider.log(stackTraceDepth, tag, Level.ERROR, null, message)
		}
	}

	/**
	 * Logs a message at [ERROR][Level.ERROR] level.
	 *
	 * @param message
	 * Text message to log
	 */
	fun error(message: String) {
		if (minimumLevelCoversError) {
			provider.log(stackTraceDepth, tag, Level.ERROR, null, message)
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
		if (minimumLevelCoversError) {
			provider.log(stackTraceDepth, tag, Level.ERROR, null, message.asSupplier())
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
		if (minimumLevelCoversError) {
			provider.log(stackTraceDepth, tag, Level.ERROR, null, message, *arguments)
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
		if (minimumLevelCoversError) {
			provider.log(stackTraceDepth, tag, Level.ERROR, null, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Logs an exception at [ERROR][Level.ERROR] level.
	 *
	 * @param exception
	 * Caught exception or any other throwable to log
	 */
	fun error(exception: Throwable) {
		if (minimumLevelCoversError) {
			provider.log(stackTraceDepth, tag, Level.ERROR, exception, null)
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
		if (minimumLevelCoversError) {
			provider.log(stackTraceDepth, tag, Level.ERROR, exception, message)
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
		if (minimumLevelCoversError) {
			provider.log(stackTraceDepth, tag, Level.ERROR, exception, message.asSupplier())
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
		if (minimumLevelCoversError) {
			provider.log(stackTraceDepth, tag, Level.ERROR, exception, message, *arguments)
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
		if (minimumLevelCoversError) {
			provider.log(stackTraceDepth, tag, Level.ERROR, exception, message, *arguments.asSuppliers())
		}
	}

	/**
	 * Checks if a given tag and severity level is covered by the logging provider's minimum level.
	 *
	 * @param tag
	 * Tag to check
	 * @param level
	 * Severity level to check
	 * @return `true` if given severity level is covered, otherwise `false`
	 */
	private fun isCoveredByMinimumLevel(tag: String?, level: Level): Boolean {
		return provider.getMinimumLevel(tag).ordinal <= level.ordinal
	}

}