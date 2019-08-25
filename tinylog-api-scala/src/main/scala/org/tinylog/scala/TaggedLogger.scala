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

import scala.language.experimental.macros

/**
	* Logger for issuing tagged log entries. Tagged loggers can be received by calling [[org.tinylog.scala.Logger.tag]].
	*
	* @param tag
	* Case-sensitive tag for logger instance
	* @see org.tinylog.scala.Logger.tag
	*/
final class TaggedLogger private[scala] (private val tag: String) {

	private[scala] final val logger = org.tinylog.Logger.tag(tag)

	/**
		* Checks whether log entries at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]] will be output.
		*
		* @return `true` if [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]] level is enabled, `false` if disabled
		*/
	def isTraceEnabled(): Boolean = macro TaggedLoggerMacro.isTraceEnabled

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def trace(message: Any): Unit = macro TaggedLoggerMacro.tracePlainMessage

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def trace(message: String): Unit = macro TaggedLoggerMacro.tracePlainMessage

	/**
		* Logs a lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]]. The message will be only evaluated if
		* the log entry is really output.
		*
		* @param message
		* Function that produces the message
		*/
	def trace(message: () => String): Unit = macro TaggedLoggerMacro.traceLazyMessage

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]]. "{}" placeholders will be replaced
		* by given arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def trace(message: String, arguments: Any*): Unit = macro TaggedLoggerMacro.traceMessageWithPlainArguments

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]] level. "{}" placeholders will be
		* replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def trace(message: String, arguments: (() => Any)*): Unit = macro TaggedLoggerMacro.traceMessageWithLazyArguments

	/**
		* Logs an exception at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def trace(exception: Throwable): Unit = macro TaggedLoggerMacro.traceException

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
	def trace(exception: Throwable, message: String): Unit = macro TaggedLoggerMacro.traceExceptionWithPlainMessage

	/**
		* Logs an exception with a custom lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]]. The message
		* will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def trace(exception: Throwable, message: () => String): Unit = macro TaggedLoggerMacro.traceExceptionWithLazyMessage

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
	def trace(exception: Throwable, message: String, arguments: Any*): Unit = macro TaggedLoggerMacro.traceExceptionWithMessageWithPlainArguments

	/**
		* Logs an exception with a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#TRACE TRACE]]. "{}" placeholders
		* will be replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def trace(exception: Throwable, message: String, arguments: (() => Any)*): Unit = macro TaggedLoggerMacro.traceExceptionWithMessageWithLazyArguments

	/**
		* Checks whether log entries at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]] will be output.
		*
		* @return `true` if [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]] level is enabled, `false` if disabled
		*/
	def isDebugEnabled(): Boolean = macro TaggedLoggerMacro.isDebugEnabled

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def debug(message: Any): Unit = macro TaggedLoggerMacro.debugPlainMessage

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def debug(message: String): Unit = macro TaggedLoggerMacro.debugPlainMessage

	/**
		* Logs a lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]]. The message will be only evaluated if
		* the log entry is really output.
		*
		* @param message
		* Function that produces the message
		*/
	def debug(message: () => String): Unit = macro TaggedLoggerMacro.debugLazyMessage

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]]. "{}" placeholders will be replaced
		* by given arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def debug(message: String, arguments: Any*): Unit = macro TaggedLoggerMacro.debugMessageWithPlainArguments

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]] level. "{}" placeholders will be
		* replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def debug(message: String, arguments: (() => Any)*): Unit = macro TaggedLoggerMacro.debugMessageWithLazyArguments

	/**
		* Logs an exception at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def debug(exception: Throwable): Unit = macro TaggedLoggerMacro.debugException

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
	def debug(exception: Throwable, message: String): Unit = macro TaggedLoggerMacro.debugExceptionWithPlainMessage

	/**
		* Logs an exception with a custom lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]]. The message
		* will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def debug(exception: Throwable, message: () => String): Unit = macro TaggedLoggerMacro.debugExceptionWithLazyMessage

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
	def debug(exception: Throwable, message: String, arguments: Any*): Unit = macro TaggedLoggerMacro.debugExceptionWithMessageWithPlainArguments

	/**
		* Logs an exception with a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#DEBUG DEBUG]]. "{}" placeholders
		* will be replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def debug(exception: Throwable, message: String, arguments: (() => Any)*): Unit = macro TaggedLoggerMacro.debugExceptionWithMessageWithLazyArguments

	/**
		* Checks whether log entries at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]] will be output.
		*
		* @return `true` if [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]] level is enabled, `false` if disabled
		*/
	def isInfoEnabled(): Boolean = macro TaggedLoggerMacro.isInfoEnabled

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def info(message: Any): Unit = macro TaggedLoggerMacro.infoPlainMessage

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def info(message: String): Unit = macro TaggedLoggerMacro.infoPlainMessage

	/**
		* Logs a lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]]. The message will be only evaluated if
		* the log entry is really output.
		*
		* @param message
		* Function that produces the message
		*/
	def info(message: () => String): Unit = macro TaggedLoggerMacro.infoLazyMessage

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]]. "{}" placeholders will be replaced
		* by given arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def info(message: String, arguments: Any*): Unit = macro TaggedLoggerMacro.infoMessageWithPlainArguments

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]] level. "{}" placeholders will be
		* replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def info(message: String, arguments: (() => Any)*): Unit = macro TaggedLoggerMacro.infoMessageWithLazyArguments

	/**
		* Logs an exception at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def info(exception: Throwable): Unit = macro TaggedLoggerMacro.infoException

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
	def info(exception: Throwable, message: String): Unit = macro TaggedLoggerMacro.infoExceptionWithPlainMessage

	/**
		* Logs an exception with a custom lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#INFO INFO]]. The message
		* will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def info(exception: Throwable, message: () => String): Unit = macro TaggedLoggerMacro.infoExceptionWithLazyMessage

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
	def info(exception: Throwable, message: String, arguments: Any*): Unit = macro TaggedLoggerMacro.infoExceptionWithMessageWithPlainArguments

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
	def info(exception: Throwable, message: String, arguments: (() => Any)*): Unit = macro TaggedLoggerMacro.infoExceptionWithMessageWithLazyArguments

	/**
		* Checks whether log entries at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]] will be output.
		*
		* @return `true` if [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]] level is enabled, `false` if disabled
		*/
	def isWarnEnabled(): Boolean = macro TaggedLoggerMacro.isWarnEnabled

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def warn(message: Any): Unit = macro TaggedLoggerMacro.warnPlainMessage

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def warn(message: String): Unit = macro TaggedLoggerMacro.warnPlainMessage

	/**
		* Logs a lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]]. The message will be only evaluated if
		* the log entry is really output.
		*
		* @param message
		* Function that produces the message
		*/
	def warn(message: () => String): Unit = macro TaggedLoggerMacro.warnLazyMessage

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]]. "{}" placeholders will be replaced
		* by given arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def warn(message: String, arguments: Any*): Unit = macro TaggedLoggerMacro.warnMessageWithPlainArguments

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]] level. "{}" placeholders will be
		* replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def warn(message: String, arguments: (() => Any)*): Unit = macro TaggedLoggerMacro.warnMessageWithLazyArguments

	/**
		* Logs an exception at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def warn(exception: Throwable): Unit = macro TaggedLoggerMacro.warnException

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
	def warn(exception: Throwable, message: String): Unit = macro TaggedLoggerMacro.warnExceptionWithPlainMessage

	/**
		* Logs an exception with a custom lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#WARN WARN]]. The message
		* will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def warn(exception: Throwable, message: () => String): Unit = macro TaggedLoggerMacro.warnExceptionWithLazyMessage

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
	def warn(exception: Throwable, message: String, arguments: Any*): Unit = macro TaggedLoggerMacro.warnExceptionWithMessageWithPlainArguments

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
	def warn(exception: Throwable, message: String, arguments: (() => Any)*): Unit = macro TaggedLoggerMacro.warnExceptionWithMessageWithLazyArguments

	/**
		* Checks whether log entries at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]] will be output.
		*
		* @return `true` if [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]] level is enabled, `false` if disabled
		*/
	def isErrorEnabled(): Boolean = macro TaggedLoggerMacro.isErrorEnabled

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def error(message: Any): Unit = macro TaggedLoggerMacro.errorPlainMessage

	/**
		* Logs a message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def error(message: String): Unit = macro TaggedLoggerMacro.errorPlainMessage

	/**
		* Logs a lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]]. The message will be only evaluated if
		* the log entry is really output.
		*
		* @param message
		* Function that produces the message
		*/
	def error(message: () => String): Unit = macro TaggedLoggerMacro.errorLazyMessage

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]]. "{}" placeholders will be replaced
		* by given arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def error(message: String, arguments: Any*): Unit = macro TaggedLoggerMacro.errorMessageWithPlainArguments

	/**
		* Logs a formatted message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]] level. "{}" placeholders will be
		* replaced by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def error(message: String, arguments: (() => Any)*): Unit = macro TaggedLoggerMacro.errorMessageWithLazyArguments

	/**
		* Logs an exception at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def error(exception: Throwable): Unit = macro TaggedLoggerMacro.errorException

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
	def error(exception: Throwable, message: String): Unit = macro TaggedLoggerMacro.errorExceptionWithPlainMessage

	/**
		* Logs an exception with a custom lazy message at [[https://tinylog.org/v2/javadoc/org/tinylog/Level.html#ERROR ERROR]]. The message
		* will be only evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def error(exception: Throwable, message: () => String): Unit = macro TaggedLoggerMacro.errorExceptionWithLazyMessage

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
	def error(exception: Throwable, message: String, arguments: Any*): Unit = macro TaggedLoggerMacro.errorExceptionWithMessageWithPlainArguments

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
	def error(exception: Throwable, message: String, arguments: (() => Any)*): Unit = macro TaggedLoggerMacro.errorExceptionWithMessageWithLazyArguments

}
