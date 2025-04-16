package org.tinylog.kotlin

import org.tinylog.core.Framework
import org.tinylog.core.Level
import org.tinylog.core.LogEntry
import org.tinylog.core.backend.OutputDetails
import org.tinylog.core.format.message.MessageFormatter
import org.tinylog.core.runtime.RuntimeFlavor

abstract class AbstractLogger(
    private val tag: String?,
    protected val framework: Framework,
    protected val formatter: MessageFormatter,
) {
    private val runtime: RuntimeFlavor = framework.runtime

    private val visibilityTrace: OutputDetails
    private val visibilityDebug: OutputDetails
    private val visibilityInfo: OutputDetails
    private val visibilityWarn: OutputDetails
    private val visibilityError: OutputDetails

    init {
        val visibility = framework.getLevelVisibilityByTag(tag)
        visibilityTrace = visibility.trace
        visibilityDebug = visibility.debug
        visibilityInfo = visibility.info
        visibilityWarn = visibility.warn
        visibilityError = visibility.error
    }

    /**
     * Checks if the trace severity level is enabled for the actual class.
     *
     * If this method returns `true`, an issued trace log entry will be output. If this method returns
     * `false`, issued trace log entries will be discarded.
     *
     * @return `true` if enabled, otherwise `false`
     */
    fun isTraceEnabled(): Boolean =
        visibilityTrace != OutputDetails.DISABLED &&
            framework.isEnabled(runtime.getDirectCaller(visibilityTrace), tag, Level.TRACE)

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
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            submit(location.get(), Level.TRACE, null, message, null)
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
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            submit(location.get(), Level.TRACE, null, message, null)
        }
    }

    /**
     * Issues a trace log entry for the result of a lazy message supply function. The result can be a plain text or any
     * object with a suitable [toString()] method.
     *
     * The passed message supply function will only be evaluated, if the trace severity level is enabled for the actual
     * class. This prevents unnecessary computing of the message if the trace severity level is disabled.
     *
     * Example:
     *
     *    Logger.trace { "Hello ${person.name}!" }
     *
     * @param message The lazy supply function for evaluating the message to log
     */
    fun trace(message: () -> Any?) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            submit(location.get(), Level.TRACE, null, message(), null)
        }
    }

    /**
     * Issues a trace log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted, if the trace severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Example:
     *
     *    Logger.trace("User {} registered on {dd/MM/yyyy}", "Alice", date)
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    fun trace(
        message: String,
        vararg arguments: Any?,
    ) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            submit(location.get(), Level.TRACE, null, message, arguments)
        }
    }

    /**
     * Issues a trace log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted and the lazy argument supply functions will only be evaluated, if
     * the trace severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
     * placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and other
     * formattable values.
     *
     * Example:
     *
     *    Logger.trace("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param message The text message with placeholders to log
     * @param arguments The lazy supply functions for the placeholders
     */
    fun trace(
        message: String,
        vararg arguments: () -> Any?,
    ) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            submit(location.get(), Level.TRACE, null, message, resolve(arguments))
        }
    }

    /**
     * Issues a trace log entry for an exception or any other kind of throwable.
     *
     * Example:
     *
     *    Logger.trace(ex)
     *
     * @param exception The exception or other kind of throwable to log
     */
    fun trace(exception: Throwable) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            submit(location.get(), Level.TRACE, exception, null, null)
        }
    }

    /**
     * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom plain text
     * message.
     *
     * Example:
     *
     *    Logger.trace(ex, "Oops, something went wrong")
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The message to log
     */
    fun trace(
        exception: Throwable,
        message: String,
    ) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            submit(location.get(), Level.TRACE, exception, message, null)
        }
    }

    /**
     * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom lazy message
     * supply function.
     *
     * The passed message supply function will only be evaluated, if the trace severity level is enabled for the actual
     * class. This prevents unnecessary computing of the message if the trace severity level is disabled.
     *
     * Example:
     *
     *    Logger.trace(ex) { "User ${person.name} has broken the system" }
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supply function for evaluating the message to log
     */
    fun trace(
        exception: Throwable,
        message: () -> String,
    ) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            submit(location.get(), Level.TRACE, exception, message(), null)
        }
    }

    /**
     * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom text message
     * with placeholders.
     *
     * The passed text message will only be formatted, if the trace severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Example:
     *
     *    Logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    fun trace(
        exception: Throwable,
        message: String?,
        vararg arguments: Any?,
    ) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            submit(location.get(), Level.TRACE, exception, message, arguments)
        }
    }

    /**
     * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom text message
     * with placeholders.
     *
     * The passed text message will only be formatted and the lazy argument supply functions will only be evaluated, if
     * the trace severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
     * placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and other
     * formattable values.
     *
     * Example:
     *
     *    Logger.trace("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The lazy supply functions for the placeholders
     */
    fun trace(
        exception: Throwable,
        message: String?,
        vararg arguments: () -> Any?,
    ) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            submit(location.get(), Level.TRACE, exception, message, resolve(arguments))
        }
    }

    /**
     * Checks if the debug severity level is enabled for the actual class.
     *
     * If this method returns `true`, an issued debug log entry will be output. If this method returns
     * `false`, issued debug log entries will be discarded.
     *
     * @return `true` if enabled, otherwise `false`
     */
    fun isDebugEnabled(): Boolean =
        visibilityDebug != OutputDetails.DISABLED &&
            framework.isEnabled(runtime.getDirectCaller(visibilityDebug), tag, Level.DEBUG)

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
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            submit(location.get(), Level.DEBUG, null, message, null)
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
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            submit(location.get(), Level.DEBUG, null, message, null)
        }
    }

    /**
     * Issues a debug log entry for the result of a lazy message supply function. The result can be a plain text or any
     * object with a suitable [toString()] method.
     *
     * The passed message supply function will only be evaluated, if the debug severity level is enabled for the actual
     * class. This prevents unnecessary computing of the message if the debug severity level is disabled.
     *
     * Example:
     *
     *    Logger.debug { "Hello ${person.name}!" }
     *
     * @param message The lazy supply function for evaluating the message to log
     */
    fun debug(message: () -> Any?) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            submit(location.get(), Level.DEBUG, null, message(), null)
        }
    }

    /**
     * Issues a debug log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted, if the debug severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Example:
     *
     *    Logger.debug("User {} registered on {dd/MM/yyyy}", "Alice", date)
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    fun debug(
        message: String,
        vararg arguments: Any?,
    ) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            submit(location.get(), Level.DEBUG, null, message, arguments)
        }
    }

    /**
     * Issues a debug log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted and the lazy argument supply functions will only be evaluated, if
     * the debug severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
     * placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and other
     * formattable values.
     *
     * Example:
     *
     *    Logger.debug("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param message The text message with placeholders to log
     * @param arguments The lazy supply functions for the placeholders
     */
    fun debug(
        message: String,
        vararg arguments: () -> Any?,
    ) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            submit(location.get(), Level.DEBUG, null, message, resolve(arguments))
        }
    }

    /**
     * Issues a debug log entry for an exception or any other kind of throwable.
     *
     * Example:
     *
     *    Logger.debug(ex)
     *
     * @param exception The exception or other kind of throwable to log
     */
    fun debug(exception: Throwable) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            submit(location.get(), Level.DEBUG, exception, null, null)
        }
    }

    /**
     * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom plain text
     * message.
     *
     * Example:
     *
     *    Logger.debug(ex, "Oops, something went wrong")
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The message to log
     */
    fun debug(
        exception: Throwable,
        message: String,
    ) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            submit(location.get(), Level.DEBUG, exception, message, null)
        }
    }

    /**
     * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom lazy message
     * supply function.
     *
     * The passed message supply function will only be evaluated, if the debug severity level is enabled for the actual
     * class. This prevents unnecessary computing of the message if the debug severity level is disabled.
     *
     * Example:
     *
     *    Logger.debug(ex) { "User ${person.name} has broken the system" }
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supply function for evaluating the message to log
     */
    fun debug(
        exception: Throwable,
        message: () -> String,
    ) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            submit(location.get(), Level.DEBUG, exception, message(), null)
        }
    }

    /**
     * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom text message
     * with placeholders.
     *
     * The passed text message will only be formatted, if the debug severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Example:
     *
     *    Logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    fun debug(
        exception: Throwable,
        message: String?,
        vararg arguments: Any?,
    ) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            submit(location.get(), Level.DEBUG, exception, message, arguments)
        }
    }

    /**
     * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom text message
     * with placeholders.
     *
     * The passed text message will only be formatted and the lazy argument supply functions will only be evaluated, if
     * the debug severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
     * placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and other
     * formattable values.
     *
     * Example:
     *
     *    Logger.debug("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The lazy supply functions for the placeholders
     */
    fun debug(
        exception: Throwable,
        message: String?,
        vararg arguments: () -> Any?,
    ) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            submit(location.get(), Level.DEBUG, exception, message, resolve(arguments))
        }
    }

    /**
     * Checks if the info severity level is enabled for the actual class.
     *
     * If this method returns `true`, an issued info log entry will be output. If this method returns
     * `false`, issued info log entries will be discarded.
     *
     * @return `true` if enabled, otherwise `false`
     */
    fun isInfoEnabled(): Boolean =
        visibilityInfo != OutputDetails.DISABLED &&
            framework.isEnabled(runtime.getDirectCaller(visibilityInfo), tag, Level.INFO)

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
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            submit(location.get(), Level.INFO, null, message, null)
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
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            submit(location.get(), Level.INFO, null, message, null)
        }
    }

    /**
     * Issues an info log entry for the result of a lazy message supply function. The result can be a plain text or any
     * object with a suitable [toString()] method.
     *
     * The passed message supply function will only be evaluated, if the info severity level is enabled for the actual
     * class. This prevents unnecessary computing of the message if the info severity level is disabled.
     *
     * Example:
     *
     *    Logger.info { "Hello ${person.name}!" }
     *
     * @param message The lazy supply function for evaluating the message to log
     */
    fun info(message: () -> Any?) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            submit(location.get(), Level.INFO, null, message(), null)
        }
    }

    /**
     * Issues an info log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted, if the info severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Example:
     *
     *    Logger.info("User {} registered on {dd/MM/yyyy}", "Alice", date)
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    fun info(
        message: String,
        vararg arguments: Any?,
    ) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            submit(location.get(), Level.INFO, null, message, arguments)
        }
    }

    /**
     * Issues an info log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted and the lazy argument supply functions will only be evaluated, if
     * the info severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
     * placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and other
     * formattable values.
     *
     * Example:
     *
     *    Logger.info("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param message The text message with placeholders to log
     * @param arguments The lazy supply functions for the placeholders
     */
    fun info(
        message: String,
        vararg arguments: () -> Any?,
    ) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            submit(location.get(), Level.INFO, null, message, resolve(arguments))
        }
    }

    /**
     * Issues an info log entry for an exception or any other kind of throwable.
     *
     * Example:
     *
     *    Logger.info(ex)
     *
     * @param exception The exception or other kind of throwable to log
     */
    fun info(exception: Throwable) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            submit(location.get(), Level.INFO, exception, null, null)
        }
    }

    /**
     * Issues an info log entry for an exception (or any other kind of throwable) together with a custom plain text
     * message.
     *
     * Example:
     *
     *    Logger.info(ex, "Oops, something went wrong")
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The message to log
     */
    fun info(
        exception: Throwable,
        message: String,
    ) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            submit(location.get(), Level.INFO, exception, message, null)
        }
    }

    /**
     * Issues an info log entry for an exception (or any other kind of throwable) together with a custom lazy message
     * supply function.
     *
     * The passed message supply function will only be evaluated, if the info severity level is enabled for the actual
     * class. This prevents unnecessary computing of the message if the info severity level is disabled.
     *
     * Example:
     *
     *    Logger.info(ex) { "User ${person.name} has broken the system" }
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supply function for evaluating the message to log
     */
    fun info(
        exception: Throwable,
        message: () -> String,
    ) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            submit(location.get(), Level.INFO, exception, message(), null)
        }
    }

    /**
     * Issues an info log entry for an exception (or any other kind of throwable) together with a custom text message
     * with placeholders.
     *
     * The passed text message will only be formatted, if the info severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Example:
     *
     *    Logger.info(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    fun info(
        exception: Throwable,
        message: String?,
        vararg arguments: Any?,
    ) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            submit(location.get(), Level.INFO, exception, message, arguments)
        }
    }

    /**
     * Issues an info log entry for an exception (or any other kind of throwable) together with a custom text message
     * with placeholders.
     *
     * The passed text message will only be formatted and the lazy argument supply functions will only be evaluated, if
     * the info severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
     * placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and other
     * formattable values.
     *
     * Example:
     *
     *    Logger.info("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The lazy supply functions for the placeholders
     */
    fun info(
        exception: Throwable,
        message: String?,
        vararg arguments: () -> Any?,
    ) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            submit(location.get(), Level.INFO, exception, message, resolve(arguments))
        }
    }

    /**
     * Checks if the warn severity level is enabled for the actual class.
     *
     * If this method returns `true`, an issued warning log entry will be output. If this method returns
     * `false`, issued warning log entries will be discarded.
     *
     * @return `true` if enabled, otherwise `false`
     */
    fun isWarnEnabled(): Boolean =
        visibilityWarn != OutputDetails.DISABLED &&
            framework.isEnabled(runtime.getDirectCaller(visibilityWarn), tag, Level.WARN)

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
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            submit(location.get(), Level.WARN, null, message, null)
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
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            submit(location.get(), Level.WARN, null, message, null)
        }
    }

    /**
     * Issues a warning log entry for the result of a lazy message supply function. The result can be a plain text or any
     * object with a suitable [toString()] method.
     *
     * The passed message supply function will only be evaluated, if the warn severity level is enabled for the actual
     * class. This prevents unnecessary computing of the message if the warn severity level is disabled.
     *
     * Example:
     *
     *    Logger.warn { "Hello ${person.name}!" }
     *
     * @param message The lazy supply function for evaluating the message to log
     */
    fun warn(message: () -> Any?) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            submit(location.get(), Level.WARN, null, message(), null)
        }
    }

    /**
     * Issues a warning log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted, if the warn severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Example:
     *
     *    Logger.warn("User {} registered on {dd/MM/yyyy}", "Alice", date)
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    fun warn(
        message: String,
        vararg arguments: Any?,
    ) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            submit(location.get(), Level.WARN, null, message, arguments)
        }
    }

    /**
     * Issues a warning log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted and the lazy argument supply functions will only be evaluated, if
     * the warn severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
     * placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and other
     * formattable values.
     *
     * Example:
     *
     *    Logger.warn("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param message The text message with placeholders to log
     * @param arguments The lazy supply functions for the placeholders
     */
    fun warn(
        message: String,
        vararg arguments: () -> Any?,
    ) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            submit(location.get(), Level.WARN, null, message, resolve(arguments))
        }
    }

    /**
     * Issues a warning log entry for an exception or any other kind of throwable.
     *
     * Example:
     *
     *    Logger.warn(ex)
     *
     * @param exception The exception or other kind of throwable to log
     */
    fun warn(exception: Throwable) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            submit(location.get(), Level.WARN, exception, null, null)
        }
    }

    /**
     * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom plain text
     * message.
     *
     * Example:
     *
     *    Logger.warn(ex, "Oops, something went wrong")
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The message to log
     */
    fun warn(
        exception: Throwable,
        message: String,
    ) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            submit(location.get(), Level.WARN, exception, message, null)
        }
    }

    /**
     * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom lazy message
     * supply function.
     *
     * The passed message supply function will only be evaluated, if the warn severity level is enabled for the actual
     * class. This prevents unnecessary computing of the message if the warn severity level is disabled.
     *
     * Example:
     *
     *    Logger.warn(ex) { "User ${person.name} has broken the system" }
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supply function for evaluating the message to log
     */
    fun warn(
        exception: Throwable,
        message: () -> String,
    ) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            submit(location.get(), Level.WARN, exception, message(), null)
        }
    }

    /**
     * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom text message
     * with placeholders.
     *
     * The passed text message will only be formatted, if the warn severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Example:
     *
     *    Logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    fun warn(
        exception: Throwable,
        message: String?,
        vararg arguments: Any?,
    ) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            submit(location.get(), Level.WARN, exception, message, arguments)
        }
    }

    /**
     * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom text message
     * with placeholders.
     *
     * The passed text message will only be formatted and the lazy argument supply functions will only be evaluated, if
     * the warn severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
     * placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and other
     * formattable values.
     *
     * Example:
     *
     *    Logger.warn("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The lazy supply functions for the placeholders
     */
    fun warn(
        exception: Throwable,
        message: String?,
        vararg arguments: () -> Any?,
    ) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            submit(location.get(), Level.WARN, exception, message, resolve(arguments))
        }
    }

    /**
     * Checks if the error severity level is enabled for the actual class.
     *
     * If this method returns `true`, an issued error log entry will be output. If this method returns
     * `false`, issued error log entries will be discarded.
     *
     * @return `true` if enabled, otherwise `false`
     */
    fun isErrorEnabled(): Boolean =
        visibilityError != OutputDetails.DISABLED &&
            framework.isEnabled(runtime.getDirectCaller(visibilityError), tag, Level.ERROR)

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
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            submit(location.get(), Level.ERROR, null, message, null)
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
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            submit(location.get(), Level.ERROR, null, message, null)
        }
    }

    /**
     * Issues an error log entry for the result of a lazy message supply function. The result can be a plain text or any
     * object with a suitable [toString()] method.
     *
     * The passed message supply function will only be evaluated, if the error severity level is enabled for the actual
     * class. This prevents unnecessary computing of the message if the error severity level is disabled.
     *
     * Example:
     *
     *    Logger.error { "Hello ${person.name}!" }
     *
     * @param message The lazy supply function for evaluating the message to log
     */
    fun error(message: () -> Any?) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            submit(location.get(), Level.ERROR, null, message(), null)
        }
    }

    /**
     * Issues an error log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted, if the error severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Example:
     *
     *    Logger.error("User {} registered on {dd/MM/yyyy}", "Alice", date)
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    fun error(
        message: String,
        vararg arguments: Any?,
    ) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            submit(location.get(), Level.ERROR, null, message, arguments)
        }
    }

    /**
     * Issues an error log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted and the lazy argument supply functions will only be evaluated, if
     * the error severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
     * placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and other
     * formattable values.
     *
     * Example:
     *
     *    Logger.error("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param message The text message with placeholders to log
     * @param arguments The lazy supply functions for the placeholders
     */
    fun error(
        message: String,
        vararg arguments: () -> Any?,
    ) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            submit(location.get(), Level.ERROR, null, message, resolve(arguments))
        }
    }

    /**
     * Issues an error log entry for an exception or any other kind of throwable.
     *
     * Example:
     *
     *    Logger.error(ex)
     *
     * @param exception The exception or other kind of throwable to log
     */
    fun error(exception: Throwable) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            submit(location.get(), Level.ERROR, exception, null, null)
        }
    }

    /**
     * Issues an error log entry for an exception (or any other kind of throwable) together with a custom plain text
     * message.
     *
     * Example:
     *
     *    Logger.error(ex, "Oops, something went wrong")
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The message to log
     */
    fun error(
        exception: Throwable,
        message: String,
    ) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            submit(location.get(), Level.ERROR, exception, message, null)
        }
    }

    /**
     * Issues an error log entry for an exception (or any other kind of throwable) together with a custom lazy message
     * supply function.
     *
     * The passed message supply function will only be evaluated, if the error severity level is enabled for the actual
     * class. This prevents unnecessary computing of the message if the error severity level is disabled.
     *
     * Example:
     *
     *    Logger.error(ex) { "User ${person.name} has broken the system" }
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supply function for evaluating the message to log
     */
    fun error(
        exception: Throwable,
        message: () -> String,
    ) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            submit(location.get(), Level.ERROR, exception, message(), null)
        }
    }

    /**
     * Issues an error log entry for an exception (or any other kind of throwable) together with a custom text message
     * with placeholders.
     *
     * The passed text message will only be formatted, if the error severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Example:
     *
     *    Logger.error(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    fun error(
        exception: Throwable,
        message: String?,
        vararg arguments: Any?,
    ) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            submit(location.get(), Level.ERROR, exception, message, arguments)
        }
    }

    /**
     * Issues an error log entry for an exception (or any other kind of throwable) together with a custom text message
     * with placeholders.
     *
     * The passed text message will only be formatted and the lazy argument supply functions will only be evaluated, if
     * the error severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
     * placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and other
     * formattable values.
     *
     * Example:
     *
     *    Logger.error("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The lazy supply functions for the placeholders
     */
    fun error(
        exception: Throwable,
        message: String?,
        vararg arguments: () -> Any?,
    ) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            submit(location.get(), Level.ERROR, exception, message, resolve(arguments))
        }
    }

    /**
     * Resolves an array of functions by invoking them and collect the returned values.
     *
     * @param functions The functions to invoke
     * @return The returned values of the passed functions
     */
    private fun resolve(functions: Array<out () -> Any?>): Array<Any?> {
        val values = arrayOfNulls<Any>(functions.size)
        for (i in functions.indices) {
            values[i] = functions[i]()
        }
        return values
    }

    /**
     * Submits a new log entry to the framework.
     *
     * @param location The location information of the caller
     * @param level The severity level of the log entry
     * @param throwable The throwable to log
     * @param message The message to log
     * @param arguments The replacements for potential placeholders in the message
     */
    private fun submit(
        location: Any,
        level: Level,
        throwable: Throwable?,
        message: Any?,
        arguments: Array<out Any?>?,
    ) {
        framework.submit(
            LogEntry(
                Thread.currentThread(),
                framework.contextStorage.mapping,
                location,
                tag,
                level,
                throwable,
                formatter,
                message?.toString(),
                arguments,
            ),
        )
    }
}
