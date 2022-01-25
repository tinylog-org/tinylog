package org.tinylog.kotlin

import org.tinylog.core.Framework
import org.tinylog.core.Level
import org.tinylog.core.format.message.EnhancedMessageFormatter

/**
 * Logger for issuing tagged log entries.
 *
 * @param depth The depth of the caller class in the stack trace
 * @param tag The case-sensitive category tag of this logger
 * @param framework The actual framework instance
 */
class TaggedLogger(val tag: String?, framework: Framework) {
	companion object {
		private const val CALLER_STACK_TRACE_DEPTH = 2
	}

	private var runtime = framework.runtime
	private var backend = framework.loggingBackend
	private var visibility = backend.getLevelVisibility(tag)
	private var formatter = EnhancedMessageFormatter(framework)

	/**
	 * Checks if the trace severity level is enabled for the actual class and tag.
	 *
	 * If this method returns `true`, an issued trace log entry will be output. If this method returns
	 * `false`, issued trace log entries will be discarded.
	 *
	 * @return `true` if enabled, otherwise `false`
	 */
	fun isTraceEnabled(): Boolean {
		return visibility.isTraceEnabled &&
			backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.TRACE)
	}

	/**
	 * Issues a trace log entry for any object with a suitable [toString()] method.
	 *
	 * The [toString()] method of a passed message object will only be called, if the trace severity level
	 * is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.trace(42)
	 *
	 * @param message The message to log
	 */
	fun trace(message: Any?) {
		if (visibility.isTraceEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.TRACE, null, message, null, null)
		}
	}

	/**
	 * Issues a trace log entry for a plain text.
	 *
	 * Example:
	 *
	 *    logger.trace("Hello World!")
	 *
	 * @param message The message to log
	 */
	fun trace(message: String) {
		if (visibility.isTraceEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.TRACE, null, message, null, null)
		}
	}

	/**
	 * Issues a trace log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable [toString()] method.
	 *
	 * The passed message supplier will only be evaluated, if the trace severity level is enabled for the actual
	 * class and tag. This prevents unnecessary computing of the message if the trace severity level is disabled.
	 *
	 * Example:
	 *
	 *    logger.trace { "Hello ${person.name}!" }
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	fun trace(message: () -> Any?) {
		if (visibility.isTraceEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.TRACE, null, message.asSupplier(), null, null)
		}
	}

	/**
	 * Issues a trace log entry for a text message with placeholders.
	 *
	 * The passed text message will only be formatted, if the trace severity level is enabled for the actual class
	 * and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 * format patterns can be provided for numbers, dates, and other formattable values.
	 *
	 * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
	 * if the trace severity level is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.trace("User {} registered on {dd/MM/yyyy}", "Alice", date)
	 *    logger.trace("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values or lazy suppliers for the placeholders
	 */
	fun trace(message: String, vararg arguments: Any?) {
		if (visibility.isTraceEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.TRACE, null, message, arguments.withSuppliers(), formatter)
		}
	}

	/**
	 * Issues a trace log entry for an exception or any other kind of throwable.
	 *
	 * Example:
	 *
	 *    logger.trace(ex)
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	fun trace(exception: Throwable) {
		if (visibility.isTraceEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.TRACE, exception, null, null, null)
		}
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * Example:
	 *
	 *    logger.trace(ex, "Oops, something went wrong")
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	fun trace(exception: Throwable, message: String) {
		if (visibility.isTraceEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.TRACE, exception, message, null, null)
		}
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * The passed message supplier will only be evaluated, if the trace severity level is enabled for the actual
	 * class and tag. This prevents unnecessary computing of the message if the trace severity level is disabled.
	 *
	 * Example:
	 *
	 *    logger.trace(ex) { "User ${person.name} has broken the system" }
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	fun trace(exception: Throwable, message: () -> String) {
		if (visibility.isTraceEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.TRACE, exception, message.asSupplier(), null, null)
		}
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * The passed text message will only be formatted, if the trace severity level is enabled for the actual class
	 * and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 * format patterns can be provided for numbers, dates, and other formattable values.
	 *
	 * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
	 * if the trace severity level is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
	 *    logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", person::name, { Instant.now() })
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values or lazy suppliers for the placeholders
	 */
	fun trace(exception: Throwable, message: String?, vararg arguments: Any?) {
		if (visibility.isTraceEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.TRACE, exception, message, arguments.withSuppliers(), formatter)
		}
	}

	/**
	 * Checks if the debug severity level is enabled for the actual class and tag.
	 *
	 * If this method returns `true`, an issued debug log entry will be output. If this method returns
	 * `false`, issued debug log entries will be discarded.
	 *
	 * @return `true` if enabled, otherwise `false`
	 */
	fun isDebugEnabled(): Boolean {
		return visibility.isDebugEnabled &&
			backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.DEBUG)
	}

	/**
	 * Issues a debug log entry for any object with a suitable [toString()] method.
	 *
	 * The [toString()] method of a passed message object will only be called, if the debug severity level
	 * is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.debug(42)
	 *
	 * @param message The message to log
	 */
	fun debug(message: Any?) {
		if (visibility.isDebugEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.DEBUG, null, message, null, null)
		}
	}

	/**
	 * Issues a debug log entry for a plain text.
	 *
	 * Example:
	 *
	 *    logger.debug("Hello World!")
	 *
	 * @param message The message to log
	 */
	fun debug(message: String) {
		if (visibility.isDebugEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.DEBUG, null, message, null, null)
		}
	}

	/**
	 * Issues a debug log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable [toString()] method.
	 *
	 * The passed message supplier will only be evaluated, if the debug severity level is enabled for the actual
	 * class and tag. This prevents unnecessary computing of the message if the debug severity level is disabled.
	 *
	 * Example:
	 *
	 *    logger.debug { "Hello ${person.name}!" }
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	fun debug(message: () -> Any?) {
		if (visibility.isDebugEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.DEBUG, null, message.asSupplier(), null, null)
		}
	}

	/**
	 * Issues a debug log entry for a text message with placeholders.
	 *
	 * The passed text message will only be formatted, if the debug severity level is enabled for the actual class
	 * and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 * format patterns can be provided for numbers, dates, and other formattable values.
	 *
	 * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
	 * if the debug severity level is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.debug("User {} registered on {dd/MM/yyyy}", "Alice", date)
	 *    logger.debug("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values or lazy suppliers for the placeholders
	 */
	fun debug(message: String, vararg arguments: Any?) {
		if (visibility.isDebugEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.DEBUG, null, message, arguments.withSuppliers(), formatter)
		}
	}

	/**
	 * Issues a debug log entry for an exception or any other kind of throwable.
	 *
	 * Example:
	 *
	 *    logger.debug(ex)
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	fun debug(exception: Throwable) {
		if (visibility.isDebugEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.DEBUG, exception, null, null, null)
		}
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * Example:
	 *
	 *    logger.debug(ex, "Oops, something went wrong")
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	fun debug(exception: Throwable, message: String) {
		if (visibility.isDebugEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.DEBUG, exception, message, null, null)
		}
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * The passed message supplier will only be evaluated, if the debug severity level is enabled for the actual
	 * class and tag. This prevents unnecessary computing of the message if the debug severity level is disabled.
	 *
	 * Example:
	 *
	 *    logger.debug(ex) { "User ${person.name} has broken the system" }
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	fun debug(exception: Throwable, message: () -> String) {
		if (visibility.isDebugEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.DEBUG, exception, message.asSupplier(), null, null)
		}
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * The passed text message will only be formatted, if the debug severity level is enabled for the actual class
	 * and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 * format patterns can be provided for numbers, dates, and other formattable values.
	 *
	 * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
	 * if the debug severity level is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
	 *    logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", person::name, { Instant.now() })
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values or lazy suppliers for the placeholders
	 */
	fun debug(exception: Throwable, message: String?, vararg arguments: Any?) {
		if (visibility.isDebugEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.DEBUG, exception, message, arguments.withSuppliers(), formatter)
		}
	}

	/**
	 * Checks if the info severity level is enabled for the actual class and tag.
	 *
	 * If this method returns `true`, an issued info log entry will be output. If this method returns
	 * `false`, issued info log entries will be discarded.
	 *
	 * @return `true` if enabled, otherwise `false`
	 */
	fun isInfoEnabled(): Boolean {
		return visibility.isInfoEnabled &&
			backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.INFO)
	}

	/**
	 * Issues an info log entry for any object with a suitable [toString()] method.
	 *
	 * The [toString()] method of a passed message object will only be called, if the info severity level
	 * is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.info(42)
	 *
	 * @param message The message to log
	 */
	fun info(message: Any?) {
		if (visibility.isInfoEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.INFO, null, message, null, null)
		}
	}

	/**
	 * Issues an info log entry for a plain text.
	 *
	 * Example:
	 *
	 *    logger.info("Hello World!")
	 *
	 * @param message The message to log
	 */
	fun info(message: String) {
		if (visibility.isInfoEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.INFO, null, message, null, null)
		}
	}

	/**
	 * Issues an info log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable [toString()] method.
	 *
	 * The passed message supplier will only be evaluated, if the info severity level is enabled for the actual
	 * class and tag. This prevents unnecessary computing of the message if the info severity level is disabled.
	 *
	 * Example:
	 *
	 *    logger.info { "Hello ${person.name}!" }
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	fun info(message: () -> Any?) {
		if (visibility.isInfoEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.INFO, null, message.asSupplier(), null, null)
		}
	}

	/**
	 * Issues an info log entry for a text message with placeholders.
	 *
	 * The passed text message will only be formatted, if the info severity level is enabled for the actual class
	 * and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 * format patterns can be provided for numbers, dates, and other formattable values.
	 *
	 * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
	 * if the info severity level is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.info("User {} registered on {dd/MM/yyyy}", "Alice", date)
	 *    logger.info("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values or lazy suppliers for the placeholders
	 */
	fun info(message: String, vararg arguments: Any?) {
		if (visibility.isInfoEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.INFO, null, message, arguments.withSuppliers(), formatter)
		}
	}

	/**
	 * Issues an info log entry for an exception or any other kind of throwable.
	 *
	 * Example:
	 *
	 *    logger.info(ex)
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	fun info(exception: Throwable) {
		if (visibility.isInfoEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.INFO, exception, null, null, null)
		}
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * Example:
	 *
	 *    logger.info(ex, "Oops, something went wrong")
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	fun info(exception: Throwable, message: String) {
		if (visibility.isInfoEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.INFO, exception, message, null, null)
		}
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * The passed message supplier will only be evaluated, if the info severity level is enabled for the actual
	 * class and tag. This prevents unnecessary computing of the message if the info severity level is disabled.
	 *
	 * Example:
	 *
	 *    logger.info(ex) { "User ${person.name} has broken the system" }
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	fun info(exception: Throwable, message: () -> String) {
		if (visibility.isInfoEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.INFO, exception, message.asSupplier(), null, null)
		}
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * The passed text message will only be formatted, if the info severity level is enabled for the actual class
	 * and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 * format patterns can be provided for numbers, dates, and other formattable values.
	 *
	 * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
	 * if the info severity level is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.info(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
	 *    logger.info(ex, "User {} broke it on {dd/MM/yyyy}", person::name, { Instant.now() })
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values or lazy suppliers for the placeholders
	 */
	fun info(exception: Throwable, message: String?, vararg arguments: Any?) {
		if (visibility.isInfoEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.INFO, exception, message, arguments.withSuppliers(), formatter)
		}
	}

	/**
	 * Checks if the warn severity level is enabled for the actual class and tag.
	 *
	 * If this method returns `true`, an issued warning log entry will be output. If this method returns
	 * `false`, issued warning log entries will be discarded.
	 *
	 * @return `true` if enabled, otherwise `false`
	 */
	fun isWarnEnabled(): Boolean {
		return visibility.isWarnEnabled &&
			backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.WARN)
	}

	/**
	 * Issues a warning log entry for any object with a suitable [toString()] method.
	 *
	 * The [toString()] method of a passed message object will only be called, if the warn severity level
	 * is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.warn(42)
	 *
	 * @param message The message to log
	 */
	fun warn(message: Any?) {
		if (visibility.isWarnEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.WARN, null, message, null, null)
		}
	}

	/**
	 * Issues a warning log entry for a plain text.
	 *
	 * Example:
	 *
	 *    logger.warn("Hello World!")
	 *
	 * @param message The message to log
	 */
	fun warn(message: String) {
		if (visibility.isWarnEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.WARN, null, message, null, null)
		}
	}

	/**
	 * Issues a warning log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable [toString()] method.
	 *
	 * The passed message supplier will only be evaluated, if the warn severity level is enabled for the actual
	 * class and tag. This prevents unnecessary computing of the message if the warn severity level is disabled.
	 *
	 * Example:
	 *
	 *    logger.warn { "Hello ${person.name}!" }
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	fun warn(message: () -> Any?) {
		if (visibility.isWarnEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.WARN, null, message.asSupplier(), null, null)
		}
	}

	/**
	 * Issues a warning log entry for a text message with placeholders.
	 *
	 * The passed text message will only be formatted, if the warn severity level is enabled for the actual class
	 * and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 * format patterns can be provided for numbers, dates, and other formattable values.
	 *
	 * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
	 * if the warn severity level is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.warn("User {} registered on {dd/MM/yyyy}", "Alice", date)
	 *    logger.warn("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values or lazy suppliers for the placeholders
	 */
	fun warn(message: String, vararg arguments: Any?) {
		if (visibility.isWarnEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.WARN, null, message, arguments.withSuppliers(), formatter)
		}
	}

	/**
	 * Issues a warning log entry for an exception or any other kind of throwable.
	 *
	 * Example:
	 *
	 *    logger.warn(ex)
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	fun warn(exception: Throwable) {
		if (visibility.isWarnEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.WARN, exception, null, null, null)
		}
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * Example:
	 *
	 *    logger.warn(ex, "Oops, something went wrong")
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	fun warn(exception: Throwable, message: String) {
		if (visibility.isWarnEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.WARN, exception, message, null, null)
		}
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * The passed message supplier will only be evaluated, if the warn severity level is enabled for the actual
	 * class and tag. This prevents unnecessary computing of the message if the warn severity level is disabled.
	 *
	 * Example:
	 *
	 *    logger.warn(ex) { "User ${person.name} has broken the system" }
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	fun warn(exception: Throwable, message: () -> String) {
		if (visibility.isWarnEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.WARN, exception, message.asSupplier(), null, null)
		}
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * The passed text message will only be formatted, if the warn severity level is enabled for the actual class
	 * and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 * format patterns can be provided for numbers, dates, and other formattable values.
	 *
	 * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
	 * if the warn severity level is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
	 *    logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", person::name, { Instant.now() })
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values or lazy suppliers for the placeholders
	 */
	fun warn(exception: Throwable, message: String?, vararg arguments: Any?) {
		if (visibility.isWarnEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.WARN, exception, message, arguments.withSuppliers(), formatter)
		}
	}

	/**
	 * Checks if the error severity level is enabled for the actual class and tag.
	 *
	 * If this method returns `true`, an issued error log entry will be output. If this method returns
	 * `false`, issued error log entries will be discarded.
	 *
	 * @return `true` if enabled, otherwise `false`
	 */
	fun isErrorEnabled(): Boolean {
		return visibility.isErrorEnabled &&
			backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.ERROR)
	}

	/**
	 * Issues an error log entry for any object with a suitable [toString()] method.
	 *
	 * The [toString()] method of a passed message object will only be called, if the error severity level
	 * is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.error(42)
	 *
	 * @param message The message to log
	 */
	fun error(message: Any?) {
		if (visibility.isErrorEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.ERROR, null, message, null, null)
		}
	}

	/**
	 * Issues an error log entry for a plain text.
	 *
	 * Example:
	 *
	 *    logger.error("Hello World!")
	 *
	 * @param message The message to log
	 */
	fun error(message: String) {
		if (visibility.isErrorEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.ERROR, null, message, null, null)
		}
	}

	/**
	 * Issues an error log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable [toString()] method.
	 *
	 * The passed message supplier will only be evaluated, if the error severity level is enabled for the actual
	 * class and tag. This prevents unnecessary computing of the message if the error severity level is disabled.
	 *
	 * Example:
	 *
	 *    logger.error { "Hello ${person.name}!" }
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	fun error(message: () -> Any?) {
		if (visibility.isErrorEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.ERROR, null, message.asSupplier(), null, null)
		}
	}

	/**
	 * Issues an error log entry for a text message with placeholders.
	 *
	 * The passed text message will only be formatted, if the error severity level is enabled for the actual class
	 * and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 * format patterns can be provided for numbers, dates, and other formattable values.
	 *
	 * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
	 * if the error severity level is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.error("User {} registered on {dd/MM/yyyy}", "Alice", date)
	 *    logger.error("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values or lazy suppliers for the placeholders
	 */
	fun error(message: String, vararg arguments: Any?) {
		if (visibility.isErrorEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.ERROR, null, message, arguments.withSuppliers(), formatter)
		}
	}

	/**
	 * Issues an error log entry for an exception or any other kind of throwable.
	 *
	 * Example:
	 *
	 *    logger.error(ex)
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	fun error(exception: Throwable) {
		if (visibility.isErrorEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.ERROR, exception, null, null, null)
		}
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * Example:
	 *
	 *    logger.error(ex, "Oops, something went wrong")
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	fun error(exception: Throwable, message: String) {
		if (visibility.isErrorEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.ERROR, exception, message, null, null)
		}
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * The passed message supplier will only be evaluated, if the error severity level is enabled for the actual
	 * class and tag. This prevents unnecessary computing of the message if the error severity level is disabled.
	 *
	 * Example:
	 *
	 *    logger.error(ex) { "User ${person.name} has broken the system" }
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	fun error(exception: Throwable, message: () -> String) {
		if (visibility.isErrorEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.ERROR, exception, message.asSupplier(), null, null)
		}
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * The passed text message will only be formatted, if the error severity level is enabled for the actual class
	 * and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 * format patterns can be provided for numbers, dates, and other formattable values.
	 *
	 * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
	 * if the error severity level is enabled for the actual class and tag.
	 *
	 * Example:
	 *
	 *    logger.error(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
	 *    logger.error(ex, "User {} broke it on {dd/MM/yyyy}", person::name, { Instant.now() })
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values or lazy suppliers for the placeholders
	 */
	fun error(exception: Throwable, message: String?, vararg arguments: Any?) {
		if (visibility.isErrorEnabled) {
			val location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH)
			backend.log(location, tag, Level.ERROR, exception, message, arguments.withSuppliers(), formatter)
		}
	}
}
