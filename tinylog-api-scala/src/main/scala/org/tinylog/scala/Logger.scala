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
	* Static logger for issuing log entries.
	*/
object Logger {

	/**
		* Checks whether log entries at [[org.tinylog.Level#TRACE]] will be output.
		*
		* @return `true` if [[org.tinylog.Level#TRACE]] level is enabled, `false` if disabled
		*/
	def isTraceEnabled(): Boolean = macro LoggerMacro.isTraceEnabled

	/**
		* Logs a message at [[org.tinylog.Level#TRACE]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def trace(message: Any): Unit = macro LoggerMacro.tracePlainMessage

	/**
		* Logs a message at [[org.tinylog.Level#TRACE]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def trace(message: String): Unit = macro LoggerMacro.tracePlainMessage

	/**
		* Logs a lazy message at [[org.tinylog.Level#TRACE]]. The message will be only evaluated if the log entry is
		* really output.
		*
		* @param message
		* Function that produces the message
		*/
	def trace(message: () => String): Unit = macro LoggerMacro.traceLazyMessage

	/**
		* Logs a formatted message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be replaced by given
		* arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def trace(message: String, arguments: Any*): Unit = macro LoggerMacro.traceMessageWithPlainArguments

	/**
		* Logs a formatted message at [[org.tinylog.Level#TRACE]] level. "{}" placeholders will be replaced by given lazy
		* arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def trace(message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.traceMessageWithLazyArguments

	/**
		* Logs an exception at [[org.tinylog.Level#TRACE]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def trace(exception: Throwable): Unit = macro LoggerMacro.traceException

	/**
		* Logs an exception with a custom message at [[org.tinylog.Level#TRACE]].
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
		* Logs an exception with a custom lazy message at [[org.tinylog.Level#TRACE]]. The message will be only
		* evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def trace(exception: Throwable, message: () => String): Unit = macro LoggerMacro.traceExceptionWithLazyMessage

	/**
		* Logs an exception with a formatted custom message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be
		* replaced by given arguments.
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
		* Logs an exception with a formatted message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be replaced
		* by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
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
		* Checks whether log entries at [[org.tinylog.Level#TRACE]] will be output.
		*
		* @return `true` if [[org.tinylog.Level#TRACE]] level is enabled, `false` if disabled
		*/
	def isDebugEnabled(): Boolean = macro LoggerMacro.isDebugEnabled

	/**
		* Logs a message at [[org.tinylog.Level#TRACE]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def debug(message: Any): Unit = macro LoggerMacro.debugPlainMessage

	/**
		* Logs a message at [[org.tinylog.Level#TRACE]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def debug(message: String): Unit = macro LoggerMacro.debugPlainMessage

	/**
		* Logs a lazy message at [[org.tinylog.Level#TRACE]]. The message will be only evaluated if the log entry is
		* really output.
		*
		* @param message
		* Function that produces the message
		*/
	def debug(message: () => String): Unit = macro LoggerMacro.debugLazyMessage

	/**
		* Logs a formatted message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be replaced by given
		* arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def debug(message: String, arguments: Any*): Unit = macro LoggerMacro.debugMessageWithPlainArguments

	/**
		* Logs a formatted message at [[org.tinylog.Level#TRACE]] level. "{}" placeholders will be replaced by given lazy
		* arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def debug(message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.debugMessageWithLazyArguments

	/**
		* Logs an exception at [[org.tinylog.Level#TRACE]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def debug(exception: Throwable): Unit = macro LoggerMacro.debugException

	/**
		* Logs an exception with a custom message at [[org.tinylog.Level#TRACE]].
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
		* Logs an exception with a custom lazy message at [[org.tinylog.Level#TRACE]]. The message will be only
		* evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def debug(exception: Throwable, message: () => String): Unit = macro LoggerMacro.debugExceptionWithLazyMessage

	/**
		* Logs an exception with a formatted custom message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be
		* replaced by given arguments.
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
		* Logs an exception with a formatted message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be replaced
		* by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
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
		* Checks whether log entries at [[org.tinylog.Level#TRACE]] will be output.
		*
		* @return `true` if [[org.tinylog.Level#TRACE]] level is enabled, `false` if disabled
		*/
	def isInfoEnabled(): Boolean = macro LoggerMacro.isInfoEnabled

	/**
		* Logs a message at [[org.tinylog.Level#TRACE]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def info(message: Any): Unit = macro LoggerMacro.infoPlainMessage

	/**
		* Logs a message at [[org.tinylog.Level#TRACE]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def info(message: String): Unit = macro LoggerMacro.infoPlainMessage

	/**
		* Logs a lazy message at [[org.tinylog.Level#TRACE]]. The message will be only evaluated if the log entry is
		* really output.
		*
		* @param message
		* Function that produces the message
		*/
	def info(message: () => String): Unit = macro LoggerMacro.infoLazyMessage

	/**
		* Logs a formatted message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be replaced by given
		* arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def info(message: String, arguments: Any*): Unit = macro LoggerMacro.infoMessageWithPlainArguments

	/**
		* Logs a formatted message at [[org.tinylog.Level#TRACE]] level. "{}" placeholders will be replaced by given lazy
		* arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def info(message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.infoMessageWithLazyArguments

	/**
		* Logs an exception at [[org.tinylog.Level#TRACE]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def info(exception: Throwable): Unit = macro LoggerMacro.infoException

	/**
		* Logs an exception with a custom message at [[org.tinylog.Level#TRACE]].
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
		* Logs an exception with a custom lazy message at [[org.tinylog.Level#TRACE]]. The message will be only
		* evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def info(exception: Throwable, message: () => String): Unit = macro LoggerMacro.infoExceptionWithLazyMessage

	/**
		* Logs an exception with a formatted custom message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be
		* replaced by given arguments.
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
		* Logs an exception with a formatted message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be replaced
		* by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
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
		* Checks whether log entries at [[org.tinylog.Level#TRACE]] will be output.
		*
		* @return `true` if [[org.tinylog.Level#TRACE]] level is enabled, `false` if disabled
		*/
	def isWarnEnabled(): Boolean = macro LoggerMacro.isWarnEnabled

	/**
		* Logs a message at [[org.tinylog.Level#TRACE]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def warn(message: Any): Unit = macro LoggerMacro.warnPlainMessage

	/**
		* Logs a message at [[org.tinylog.Level#TRACE]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def warn(message: String): Unit = macro LoggerMacro.warnPlainMessage

	/**
		* Logs a lazy message at [[org.tinylog.Level#TRACE]]. The message will be only evaluated if the log entry is
		* really output.
		*
		* @param message
		* Function that produces the message
		*/
	def warn(message: () => String): Unit = macro LoggerMacro.warnLazyMessage

	/**
		* Logs a formatted message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be replaced by given
		* arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def warn(message: String, arguments: Any*): Unit = macro LoggerMacro.warnMessageWithPlainArguments

	/**
		* Logs a formatted message at [[org.tinylog.Level#TRACE]] level. "{}" placeholders will be replaced by given lazy
		* arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def warn(message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.warnMessageWithLazyArguments

	/**
		* Logs an exception at [[org.tinylog.Level#TRACE]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def warn(exception: Throwable): Unit = macro LoggerMacro.warnException

	/**
		* Logs an exception with a custom message at [[org.tinylog.Level#TRACE]].
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
		* Logs an exception with a custom lazy message at [[org.tinylog.Level#TRACE]]. The message will be only
		* evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def warn(exception: Throwable, message: () => String): Unit = macro LoggerMacro.warnExceptionWithLazyMessage

	/**
		* Logs an exception with a formatted custom message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be
		* replaced by given arguments.
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
		* Logs an exception with a formatted message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be replaced
		* by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
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
		* Checks whether log entries at [[org.tinylog.Level#TRACE]] will be output.
		*
		* @return `true` if [[org.tinylog.Level#TRACE]] level is enabled, `false` if disabled
		*/
	def isErrorEnabled(): Boolean = macro LoggerMacro.isErrorEnabled

	/**
		* Logs a message at [[org.tinylog.Level#TRACE]].
		*
		* @param message
		* Any object with a meaningful `toString()` method
		*/
	def error(message: Any): Unit = macro LoggerMacro.errorPlainMessage

	/**
		* Logs a message at [[org.tinylog.Level#TRACE]].
		*
		* Strings with embedded variables will be evaluated lazy by a macro.
		*
		* @param message
		* Text message to log
		*/
	def error(message: String): Unit = macro LoggerMacro.errorPlainMessage

	/**
		* Logs a lazy message at [[org.tinylog.Level#TRACE]]. The message will be only evaluated if the log entry is
		* really output.
		*
		* @param message
		* Function that produces the message
		*/
	def error(message: () => String): Unit = macro LoggerMacro.errorLazyMessage

	/**
		* Logs a formatted message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be replaced by given
		* arguments.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Arguments for formatted text message
		*/
	def error(message: String, arguments: Any*): Unit = macro LoggerMacro.errorMessageWithPlainArguments

	/**
		* Logs a formatted message at [[org.tinylog.Level#TRACE]] level. "{}" placeholders will be replaced by given lazy
		* arguments. The arguments will be only evaluated if the log entry is really output.
		*
		* @param message
		* Formatted text message to log
		* @param arguments
		* Functions that produce the arguments for formatted text message
		*/
	def error(message: String, arguments: (() => Any)*): Unit = macro LoggerMacro.errorMessageWithLazyArguments

	/**
		* Logs an exception at [[org.tinylog.Level#TRACE]].
		*
		* @param exception
		* Caught exception or any other throwable to log
		*/
	def error(exception: Throwable): Unit = macro LoggerMacro.errorException

	/**
		* Logs an exception with a custom message at [[org.tinylog.Level#TRACE]].
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
		* Logs an exception with a custom lazy message at [[org.tinylog.Level#TRACE]]. The message will be only
		* evaluated if the log entry is really output.
		*
		* @param exception
		* Caught exception or any other throwable to log
		* @param message
		* Function that produces the message
		*/
	def error(exception: Throwable, message: () => String): Unit = macro LoggerMacro.errorExceptionWithLazyMessage

	/**
		* Logs an exception with a formatted custom message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be
		* replaced by given arguments.
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
		* Logs an exception with a formatted message at [[org.tinylog.Level#TRACE]]. "{}" placeholders will be replaced
		* by given lazy arguments. The arguments will be only evaluated if the log entry is really output.
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
