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
import org.tinylog.configuration.Configuration
import org.tinylog.format.AdvancedMessageFormatter
import org.tinylog.provider.ProviderRegistry

/**
 * Logger for issuing tagged log entries. Tagged loggers can be received by calling [Logger.tag] or [Logger.tags].
 *
 * @param tags
 * Case-sensitive tag for logger instance
 * @see Logger.tag
 * @see Logger.tags
 */
class TaggedLogger internal constructor(private val tags: Set<String?>) {

	private val stackTraceDepth = 2

	private val formatter = AdvancedMessageFormatter(Configuration.getLocale(), Configuration.isEscapingEnabled())
	private val provider = ProviderRegistry.getLoggingProvider()

	// @formatter:off
	private val traceTags = getCoveredTags(tags, Level.TRACE)
	private val debugTags = getCoveredTags(tags, Level.DEBUG)
	private val infoTags  = getCoveredTags(tags, Level.INFO)
	private val warnTags  = getCoveredTags(tags, Level.WARN)
	private val errorTags = getCoveredTags(tags, Level.ERROR)

	private val minimumLevelCoversTrace = traceTags.isNotEmpty()
	private val minimumLevelCoversDebug = debugTags.isNotEmpty()
	private val minimumLevelCoversInfo  = infoTags.isNotEmpty()
	private val minimumLevelCoversWarn  = warnTags.isNotEmpty()
	private val minimumLevelCoversError = errorTags.isNotEmpty()
	// @formatter:on

	/**
	 * Checks whether log entries at [TRACE][Level.TRACE] level will be output.
	 *
	 * @return `true` if [TRACE][Level.TRACE] level is enabled, `false` if disabled
	 */
	fun isTraceEnabled(): Boolean {
		return minimumLevelCoversTrace && anyEnabled(traceTags, Level.TRACE)
	}

	/**
	 * Logs a message at [TRACE][Level.TRACE] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun trace(message: Any?) {
		if (minimumLevelCoversTrace) {
			for (it in traceTags) {
				provider.log(stackTraceDepth, it, Level.TRACE, null, null, message)
			}
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
			for (it in traceTags) {
				provider.log(stackTraceDepth, it, Level.TRACE, null, null, message)
			}
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
			for (it in traceTags) {
				provider.log(stackTraceDepth, it, Level.TRACE, null, null, message.asSupplier())
			}
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
			for (it in traceTags) {
				provider.log(stackTraceDepth, it, Level.TRACE, null, formatter, message, *arguments)
			}
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
			for (it in traceTags) {
				provider.log(stackTraceDepth, it, Level.TRACE, null, formatter, message, *arguments.asSuppliers())
			}
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
			for (it in traceTags) {
				provider.log(stackTraceDepth, it, Level.TRACE, exception, null, null)
			}
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
			for (it in traceTags) {
				provider.log(stackTraceDepth, it, Level.TRACE, exception, null, message)
			}
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
			for (it in traceTags) {
				provider.log(stackTraceDepth, it, Level.TRACE, exception, null, message.asSupplier())
			}
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
			for (it in traceTags) {
				provider.log(stackTraceDepth, it, Level.TRACE, exception, formatter, message, *arguments)
			}
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
			for (it in traceTags) {
				provider.log(stackTraceDepth, it, Level.TRACE, exception, formatter, message, *arguments.asSuppliers())
			}
		}
	}

	/**
	 * Checks whether log entries at [DEBUG][Level.DEBUG] level will be output.
	 *
	 * @return `true` if [DEBUG][Level.DEBUG] level is enabled, `false` if disabled
	 */
	fun isDebugEnabled(): Boolean {
		return minimumLevelCoversDebug && anyEnabled(debugTags, Level.DEBUG)
	}

	/**
	 * Logs a message at [DEBUG][Level.DEBUG] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun debug(message: Any?) {
		if (minimumLevelCoversDebug) {
			for (it in debugTags) {
				provider.log(stackTraceDepth, it, Level.DEBUG, null, null, message)
			}
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
			for (it in debugTags) {
				provider.log(stackTraceDepth, it, Level.DEBUG, null, null, message)
			}
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
			for (it in debugTags) {
				provider.log(stackTraceDepth, it, Level.DEBUG, null, null, message.asSupplier())
			}
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
			for (it in debugTags) {
				provider.log(stackTraceDepth, it, Level.DEBUG, null, formatter, message, *arguments)
			}
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
			for (it in debugTags) {
				provider.log(stackTraceDepth, it, Level.DEBUG, null, formatter, message, *arguments.asSuppliers())
			}
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
			for (it in debugTags) {
				provider.log(stackTraceDepth, it, Level.DEBUG, exception, null, null)
			}
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
			for (it in debugTags) {
				provider.log(stackTraceDepth, it, Level.DEBUG, exception, null, message)
			}
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
			for (it in debugTags) {
				provider.log(stackTraceDepth, it, Level.DEBUG, exception, null, message.asSupplier())
			}
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
			for (it in debugTags) {
				provider.log(stackTraceDepth, it, Level.DEBUG, exception, formatter, message, *arguments)
			}
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
			for (it in debugTags) {
				provider.log(stackTraceDepth, it, Level.DEBUG, exception, formatter, message, *arguments.asSuppliers())
			}
		}
	}

	/**
	 * Checks whether log entries at [INFO][Level.INFO] level will be output.
	 *
	 * @return `true` if [INFO][Level.INFO] level is enabled, `false` if disabled
	 */
	fun isInfoEnabled(): Boolean {
		return minimumLevelCoversInfo && infoTags.any { anyEnabled(infoTags, Level.INFO) }
	}

	/**
	 * Logs a message at [INFO][Level.INFO] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun info(message: Any?) {
		if (minimumLevelCoversInfo) {
			for (it in infoTags) {
				provider.log(stackTraceDepth, it, Level.INFO, null, null, message)
			}
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
			for (it in infoTags) {
				provider.log(stackTraceDepth, it, Level.INFO, null, null, message)
			}
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
			for (it in infoTags) {
				provider.log(stackTraceDepth, it, Level.INFO, null, null, message.asSupplier())
			}
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
			for (it in infoTags) {
				provider.log(stackTraceDepth, it, Level.INFO, null, formatter, message, *arguments)
			}
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
			for (it in infoTags) {
				provider.log(stackTraceDepth, it, Level.INFO, null, formatter, message, *arguments.asSuppliers())
			}
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
			for (it in infoTags) {
				provider.log(stackTraceDepth, it, Level.INFO, exception, null, null)
			}
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
			for (it in infoTags) {
				provider.log(stackTraceDepth, it, Level.INFO, exception, null, message)
			}
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
			for (it in infoTags) {
				provider.log(stackTraceDepth, it, Level.INFO, exception, null, message.asSupplier())
			}
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
			for (it in infoTags) {
				provider.log(stackTraceDepth, it, Level.INFO, exception, formatter, message, *arguments)
			}
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
			for (it in infoTags) {
				provider.log(stackTraceDepth, it, Level.INFO, exception, formatter, message, *arguments.asSuppliers())
			}
		}
	}

	/**
	 * Checks whether log entries at [WARN][Level.WARN] level will be output.
	 *
	 * @return `true` if [WARN][Level.WARN] level is enabled, `false` if disabled
	 */
	fun isWarnEnabled(): Boolean {
		return minimumLevelCoversWarn && warnTags.any{ anyEnabled(warnTags, Level.WARN) }
	}

	/**
	 * Logs a message at [WARN][Level.WARN] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun warn(message: Any?) {
		if (minimumLevelCoversWarn) {
			for (it in warnTags) {
				provider.log(stackTraceDepth, it, Level.WARN, null, null, message)
			}
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
			for (it in warnTags) {
				provider.log(stackTraceDepth, it, Level.WARN, null, null, message)
			}
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
			for (it in warnTags) {
				provider.log(stackTraceDepth, it, Level.WARN, null, null, message.asSupplier())
			}
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
			for (it in warnTags) {
				provider.log(stackTraceDepth, it, Level.WARN, null, formatter, message, *arguments)
			}
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
			for (it in warnTags) {
				provider.log(stackTraceDepth, it, Level.WARN, null, formatter, message, *arguments.asSuppliers())
			}
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
			for (it in warnTags) {
				provider.log(stackTraceDepth, it, Level.WARN, exception, null, null)
			}
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
			for (it in warnTags) {
				provider.log(stackTraceDepth, it, Level.WARN, exception, null, message)
			}
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
			for (it in warnTags) {
				provider.log(stackTraceDepth, it, Level.WARN, exception, null, message.asSupplier())
			}
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
			for (it in warnTags) {
				provider.log(stackTraceDepth, it, Level.WARN, exception, formatter, message, *arguments)
			}
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
			for (it in warnTags) {
				provider.log(stackTraceDepth, it, Level.WARN, exception, formatter, message, *arguments.asSuppliers())
			}
		}
	}

	/**
	 * Checks whether log entries at [ERROR][Level.ERROR] level will be output.
	 *
	 * @return `true` if [ERROR][Level.ERROR] level is enabled, `false` if disabled
	 */
	fun isErrorEnabled(): Boolean {
		return minimumLevelCoversError && errorTags.any{ anyEnabled(errorTags, Level.ERROR)}
	}

	/**
	 * Logs a message at [ERROR][Level.ERROR] level.
	 *
	 * @param message
	 * Any object with a meaningful [Any.toString] method
	 */
	fun error(message: Any?) {
		if (minimumLevelCoversError) {
			for (it in errorTags) {
				provider.log(stackTraceDepth, it, Level.ERROR, null, null, message)
			}
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
			for (it in errorTags) {
				provider.log(stackTraceDepth, it, Level.ERROR, null, null, message)
			}
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
			for (it in errorTags) {
				provider.log(stackTraceDepth, it, Level.ERROR, null, null, message.asSupplier())
			}
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
			for (it in errorTags) {
				provider.log(stackTraceDepth, it, Level.ERROR, null, formatter, message, *arguments)
			}
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
			for (it in errorTags) {
				provider.log(stackTraceDepth, it, Level.ERROR, null, formatter, message, *arguments.asSuppliers())
			}
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
			for (it in errorTags) {
				provider.log(stackTraceDepth, it, Level.ERROR, exception, null, null)
			}
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
			for (it in errorTags) {
				provider.log(stackTraceDepth, it, Level.ERROR, exception, null, message)
			}
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
			for (it in errorTags) {
				provider.log(stackTraceDepth, it, Level.ERROR, exception, null, message.asSupplier())
			}
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
			for (it in errorTags) {
				provider.log(stackTraceDepth, it, Level.ERROR, exception, formatter, message, *arguments)
			}
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
			for (it in errorTags) {
				provider.log(stackTraceDepth, it, Level.ERROR, exception, formatter, message, *arguments.asSuppliers())
			}
		}
	}

	/**
	 * Checks if any of the tags in a given {@link Set} is enabled for a given {@link Level}.
	 *
	 * @param tags  {@link Set} of tags to check
	 * @param level the log level that at least one of the tags must be enabled
	 * @return {@code true} if the level is enabled for at least one of the tags in the given {@link Set}. otherwise, {@code false}
	 */
	private fun anyEnabled(tags: Set<String?>, level: Level): Boolean {
		for (it in tags) {
			if (provider.isEnabled(stackTraceDepth + 1, it, level)) {
				return true
			}
		}
		return false
	}

	/**
	 * Filters a given {@link Set} of tags by whether they are covered by a level.
	 *
	 * @param tags  {@link Set} of tags to go through
	 * @param level the minimum that the tag must cover
	 * @return the {@link Set} of tags that are covered by the level
	 */
	private fun getCoveredTags(tags: Set<String?>, level: Level): Set<String?> {
		return tags.filter { isCoveredByMinimumLevel(it, level) }.toHashSet()
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