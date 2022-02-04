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

package org.tinylog.scala

import java.util.concurrent.ConcurrentHashMap

import scala.language.experimental.macros

/**
	* Static logger for issuing log entries.
	*/
object Logger {

	private val instance = new TaggedLogger(Set(null))
	private val loggers = new ConcurrentHashMap[Set[String], TaggedLogger]()

	loggers.put(Set(null: String), instance)

	/**
		* Gets a tagged logger instance. Tags are case-sensitive.
		*
		* @param tag
		* Tag for logger or `null` for receiving an untagged logger
		* @return Logger instance
		*/
	def tag(tag: String): TaggedLogger = {
		if (tag == null || tag.isEmpty()) {
			return instance
		} else {
			tags(tag)
		}
	}

	/**
		* Gets a tagged logger instance that logs to multiple tags. Tags are case-sensitive.
		*
		* @param tags
		* Tags for the logger or nothing for an untagged logger. If specified, each tag should be unique
		* @return Logger instance
		*/
	def tags(tags: String*): TaggedLogger = {
		if (tags == null || tags.isEmpty) {
			instance
		} else {
			val tagsSet = tags.map(t => if (t == null || t.isEmpty) null else t).toSet
			var logger = loggers.get(tagsSet)
			if (logger == null) {
				logger = new TaggedLogger(tagsSet)
				val existing = loggers.putIfAbsent(tagsSet, logger)
				if (existing == null) logger else existing
			} else logger
		}
	}

	/**
		* Checks whether log entries at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]] will be output.
		*
		* @return `true` if [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]] level is enabled, `false` if disabled
		*/
	def isTraceEnabled(): Boolean = macro LoggerMacro.isTraceEnabled

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def trace(message: Any): Unit = macro LoggerMacro.tracePlainMessage

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def trace(message: String): Unit = macro LoggerMacro.tracePlainMessage

	/**
		* Logs a lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]]. The message will be only evaluated if
		* the log entry is really output.
		*
		* @param message
		* Function that produces the message
		*/
	def trace(message: () => String): Unit = macro LoggerMacro.traceLazyMessage

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]]. "{}" placeholders will be replaced
		* by given arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def trace(message: String, arguments: Any*): Unit = macro LoggerMacro.traceMessageWithPlainArguments

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]] level. "{}" placeholders will be
		* replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def trace(message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.traceMessageWithLazyArguments

	/**
		* Logs an exception at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def trace(exception: Throwable): Unit = macro LoggerMacro.traceException

	/**
		* Logs an exception with a custom message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]].
		*
		* Messages with embedded variables will be evaluated lazy by a macro.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Text message to log
		*/
	def trace(exception: Throwable, message: String): Unit = macro LoggerMacro.traceExceptionWithPlainMessage

	/**
		* Logs an exception with a custom lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]]. The message
		* will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def trace(exception: Throwable, message: () => String): Unit = macro LoggerMacro.traceExceptionWithLazyMessage

	/**
		* Logs an exception with a formatted custom message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]].
		* "{}" placeholders will be replaced by given arguments.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def trace(exception: Throwable, message: String, arguments: Any*): Unit = macro LoggerMacro.traceExceptionWithMessageWithPlainArguments

	/**
		* Logs an exception with a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]].
		* "{}" placeholders will be replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def trace(exception: Throwable, message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.traceExceptionWithMessageWithLazyArguments

	/**
		* Checks whether log entries at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]] will be output.
		*
		* @return `true` if [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]] level is enabled, `false` if disabled
		*/
	def isDebugEnabled(): Boolean = macro LoggerMacro.isDebugEnabled

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def debug(message: Any): Unit = macro LoggerMacro.debugPlainMessage

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def debug(message: String): Unit = macro LoggerMacro.debugPlainMessage

	/**
		* Logs a lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]]. The message will be only evaluated if
		* the log entry is really output.
		*
		* @param message
		* Function that produces the message
		*/
	def debug(message: () => String): Unit = macro LoggerMacro.debugLazyMessage

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]]. "{}" placeholders will be replaced
		* by given arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def debug(message: String, arguments: Any*): Unit = macro LoggerMacro.debugMessageWithPlainArguments

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]] level. "{}" placeholders will be
		* replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def debug(message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.debugMessageWithLazyArguments

	/**
		* Logs an exception at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def debug(exception: Throwable): Unit = macro LoggerMacro.debugException

	/**
		* Logs an exception with a custom message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]].
		*
		* Messages with embedded variables will be evaluated lazy by a macro.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Text message to log
		*/
	def debug(exception: Throwable, message: String): Unit = macro LoggerMacro.debugExceptionWithPlainMessage

	/**
		* Logs an exception with a custom lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]]. The message
		* will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def debug(exception: Throwable, message: () => String): Unit = macro LoggerMacro.debugExceptionWithLazyMessage

	/**
		* Logs an exception with a formatted custom message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]].
		* "{}" placeholders will be replaced by given arguments.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def debug(exception: Throwable, message: String, arguments: Any*): Unit = macro LoggerMacro.debugExceptionWithMessageWithPlainArguments

	/**
		* Logs an exception with a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]].
		* "{}" placeholders will be replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def debug(exception: Throwable, message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.debugExceptionWithMessageWithLazyArguments

	/**
		* Checks whether log entries at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]] will beoutput.
		*
		* @return `true` if [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]] level is enabled, `false` if disabled
		*/
	def isInfoEnabled(): Boolean = macro LoggerMacro.isInfoEnabled

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def info(message: Any): Unit = macro LoggerMacro.infoPlainMessage

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def info(message: String): Unit = macro LoggerMacro.infoPlainMessage

	/**
		* Logs a lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]]. The message will be only evaluated if
		* the log entry is really output.
		*
		* @param message
		* Function that produces the message
		*/
	def info(message: () => String): Unit = macro LoggerMacro.infoLazyMessage

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]]. "{}" placeholders will be replaced
		* by given arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def info(message: String, arguments: Any*): Unit = macro LoggerMacro.infoMessageWithPlainArguments

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]] level. "{}" placeholders will be
		* replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def info(message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.infoMessageWithLazyArguments

	/**
		* Logs an exception at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def info(exception: Throwable): Unit = macro LoggerMacro.infoException

	/**
		* Logs an exception with a custom message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]].
		*
		* Messages with embedded variables will be evaluated lazy by a macro.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Text message to log
		*/
	def info(exception: Throwable, message: String): Unit = macro LoggerMacro.infoExceptionWithPlainMessage

	/**
		* Logs an exception with a custom lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]]. The message
		* will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def info(exception: Throwable, message: () => String): Unit = macro LoggerMacro.infoExceptionWithLazyMessage

	/**
		* Logs an exception with a formatted custom message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]].
		* "{}" placeholders will be replaced by given arguments.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def info(exception: Throwable, message: String, arguments: Any*): Unit = macro LoggerMacro.infoExceptionWithMessageWithPlainArguments

	/**
		* Logs an exception with a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]]. "{}" placeholders
		* will be replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def info(exception: Throwable, message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.infoExceptionWithMessageWithLazyArguments

	/**
		* Checks whether log entries at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]] will be output.
		*
		* @return `true` if [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]] level is enabled, `false` if disabled
		*/
	def isWarnEnabled(): Boolean = macro LoggerMacro.isWarnEnabled

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def warn(message: Any): Unit = macro LoggerMacro.warnPlainMessage

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def warn(message: String): Unit = macro LoggerMacro.warnPlainMessage

	/**
		* Logs a lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]]. The message will be only evaluated if
		* the log entry is really output.
		*
		* @param message
		* Function that produces the message
		*/
	def warn(message: () => String): Unit = macro LoggerMacro.warnLazyMessage

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]]. "{}" placeholders will be replaced
		* by given arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def warn(message: String, arguments: Any*): Unit = macro LoggerMacro.warnMessageWithPlainArguments

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]] level. "{}" placeholders will be
		* replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def warn(message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.warnMessageWithLazyArguments

	/**
		* Logs an exception at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def warn(exception: Throwable): Unit = macro LoggerMacro.warnException

	/**
		* Logs an exception with a custom message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]].
		*
		* Messages with embedded variables will be evaluated lazy by a macro.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Text message to log
		*/
	def warn(exception: Throwable, message: String): Unit = macro LoggerMacro.warnExceptionWithPlainMessage

	/**
		* Logs an exception with a custom lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]]. The message
		* will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def warn(exception: Throwable, message: () => String): Unit = macro LoggerMacro.warnExceptionWithLazyMessage

	/**
		* Logs an exception with a formatted custom message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]].
		* "{}" placeholders will be replaced by given arguments.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def warn(exception: Throwable, message: String, arguments: Any*): Unit = macro LoggerMacro.warnExceptionWithMessageWithPlainArguments

	/**
		* Logs an exception with a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]]. "{}" placeholders
		* will be replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def warn(exception: Throwable, message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.warnExceptionWithMessageWithLazyArguments

	/**
		* Checks whether log entries at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]] will be output.
		*
		* @return `true` if [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]] level is enabled, `false` if disabled
		*/
	def isErrorEnabled(): Boolean = macro LoggerMacro.isErrorEnabled

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def error(message: Any): Unit = macro LoggerMacro.errorPlainMessage

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def error(message: String): Unit = macro LoggerMacro.errorPlainMessage

	/**
		* Logs a lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]]. The message will be only evaluated if
		* the log entry is really output.
		*
		* @param message
		* Function that produces the message
		*/
	def error(message: () => String): Unit = macro LoggerMacro.errorLazyMessage

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]]. "{}" placeholders will be replaced
		* by given arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def error(message: String, arguments: Any*): Unit = macro LoggerMacro.errorMessageWithPlainArguments

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]] level. "{}" placeholders will be
		* replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def error(message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.errorMessageWithLazyArguments

	/**
		* Logs an exception at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def error(exception: Throwable): Unit = macro LoggerMacro.errorException

	/**
		* Logs an exception with a custom message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]].
		*
		* Messages with embedded variables will be evaluated lazy by a macro.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Text message to log
		*/
	def error(exception: Throwable, message: String): Unit = macro LoggerMacro.errorExceptionWithPlainMessage

	/**
		* Logs an exception with a custom lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]]. The message
		* will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def error(exception: Throwable, message: () => String): Unit = macro LoggerMacro.errorExceptionWithLazyMessage

	/**
		* Logs an exception with a formatted custom message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]].
		* "{}" placeholders will be replaced by given arguments.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def error(exception: Throwable, message: String, arguments: Any*): Unit = macro LoggerMacro.errorExceptionWithMessageWithPlainArguments

	/**
		* Logs an exception with a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]]. "{}" placeholders
		* will be replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def error(exception: Throwable, message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.errorExceptionWithMessageWithLazyArguments

}
