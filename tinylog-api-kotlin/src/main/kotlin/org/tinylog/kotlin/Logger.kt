package org.tinylog.kotlin

import org.tinylog.core.Level
import org.tinylog.core.Tinylog
import org.tinylog.core.backend.OutputDetails
import org.tinylog.core.format.message.EnhancedMessageFormatter
import org.tinylog.core.format.message.MessageFormatter
import java.util.concurrent.ConcurrentHashMap

/**
 * Static logger for issuing log entries.
 */
object Logger {
    private val taggedLoggers = ConcurrentHashMap<String, TaggedLogger>()
    private val framework = Tinylog.getFramework()
    private val runtime = framework.runtime
    private val backend = framework.loggingBackend
    private val formatter: MessageFormatter = EnhancedMessageFormatter(framework.classLoader)

    private val visibilityTrace: OutputDetails
    private val visibilityDebug: OutputDetails
    private val visibilityInfo: OutputDetails
    private val visibilityWarn: OutputDetails
    private val visibilityError: OutputDetails

    init {
        val visibility = backend.getLevelVisibilityByTag(null)
        visibilityTrace = visibility.trace
        visibilityDebug = visibility.debug
        visibilityInfo = visibility.info
        visibilityWarn = visibility.warn
        visibilityError = visibility.error
    }

    /**
     * Retrieves a tagged logger instance. Category tags are case-sensitive. If a tagged logger does not yet exist for
     * the passed tag, a new logger will be created. This method always returns the same logger instance for the same
     * tag.
     *
     * @param tag The case-sensitive category tag of the requested logger, or `null` for receiving an untagged
     *            logger
     * @return Logger instance
     */
    fun tag(tag: String?): TaggedLogger {
        return if (tag.isNullOrEmpty()) {
            taggedLoggers.computeIfAbsent("") {
                TaggedLogger(null, framework)
            }
        } else {
            taggedLoggers.computeIfAbsent(tag) {
                TaggedLogger(it, framework)
            }
        }
    }

    /**
     * Checks if the trace severity level is enabled for the actual class.
     *
     * If this method returns `true`, an issued trace log entry will be output. If this method returns
     * `false`, issued trace log entries will be discarded.
     *
     * @return `true` if enabled, otherwise `false`
     */
    fun isTraceEnabled(): Boolean {
        return visibilityTrace != OutputDetails.DISABLED &&
            backend.isEnabled(runtime.getDirectCaller(visibilityTrace), null, Level.TRACE)
    }

    /**
     * Issues a trace log entry for any object with a suitable [toString()] method.
     *
     * The [toString()] method of a passed message object will only be called, if the trace severity level is enabled
     * for the actual class.
     *
     * Example:
     *
     *    Logger.trace(42)
     *
     * @param message The message to log
     */
    fun trace(message: Any?) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            backend.log(location.get(), null, Level.TRACE, null, message, null, null)
        }
    }

    /**
     * Issues a trace log entry for a plain text.
     *
     * Example:
     *
     *    Logger.trace("Hello World!")
     *
     * @param message The message to log
     */
    fun trace(message: String) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            backend.log(location.get(), null, Level.TRACE, null, message, null, null)
        }
    }

    /**
     * Issues a trace log entry for the result of a lazy message supplier. The result can be a plain text or any object
     * with a suitable [toString()] method.
     *
     * The passed message supplier will only be evaluated, if the trace severity level is enabled for the actual class.
     * This prevents unnecessary computing of the message if the trace severity level is disabled.
     *
     * Example:
     *
     *    Logger.trace { "Hello ${person.name}!" }
     *
     * @param message The lazy supplier for evaluating the message to log
     */
    fun trace(message: () -> Any?) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            backend.log(location.get(), null, Level.TRACE, null, message.asSupplier(), null, null)
        }
    }

    /**
     * Issues a trace log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted, if the trace severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
     * if the trace severity level is enabled for the actual class.
     *
     * Example:
     *
     *    Logger.trace("User {} registered on {dd/MM/yyyy}", "Alice", date)
     *    Logger.trace("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values or lazy suppliers for the placeholders
     */
    fun trace(message: String, vararg arguments: Any?) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            backend.log(location.get(), null, Level.TRACE, null, message, arguments.withSuppliers(), formatter)
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
            backend.log(location.get(), null, Level.TRACE, exception, null, null, null)
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
    fun trace(exception: Throwable, message: String) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            backend.log(location.get(), null, Level.TRACE, exception, message, null, null)
        }
    }

    /**
     * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom lazy message
     * supplier.
     *
     * The passed message supplier will only be evaluated, if the trace severity level is enabled for the actual class.
     * This prevents unnecessary computing of the message if the trace severity level is disabled.
     *
     * Example:
     *
     *    Logger.trace(ex) { "User ${person.name} has broken the system" }
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supplier for evaluating the message to log
     */
    fun trace(exception: Throwable, message: () -> String) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            backend.log(location.get(), null, Level.TRACE, exception, message.asSupplier(), null, null)
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
     * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
     * if the trace severity level is enabled for the actual class.
     *
     * Example:
     *
     *    Logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
     *    Logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values or lazy suppliers for the placeholders
     */
    fun trace(exception: Throwable, message: String?, vararg arguments: Any?) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityTrace)
            backend.log(location.get(), null, Level.TRACE, exception, message, arguments.withSuppliers(), formatter)
        }
    }

    /**
     * Checks if the debug severity level is enabled for the actual class.
     *
     * If this method returns `true`, an issued debug log entry will be output. If this method returns `false`, issued
     * debug log entries will be discarded.
     *
     * @return `true` if enabled, otherwise `false`
     */
    fun isDebugEnabled(): Boolean {
        return visibilityDebug != OutputDetails.DISABLED &&
            backend.isEnabled(runtime.getDirectCaller(visibilityDebug), null, Level.DEBUG)
    }

    /**
     * Issues a debug log entry for any object with a suitable [toString()] method.
     *
     * The [toString()] method of a passed message object will only be called, if the debug severity level is enabled
     * for the actual class.
     *
     * Example:
     *
     *    Logger.debug(42)
     *
     * @param message The message to log
     */
    fun debug(message: Any?) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            backend.log(location.get(), null, Level.DEBUG, null, message, null, null)
        }
    }

    /**
     * Issues a debug log entry for a plain text.
     *
     * Example:
     *
     *    Logger.debug("Hello World!")
     *
     * @param message The message to log
     */
    fun debug(message: String) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            backend.log(location.get(), null, Level.DEBUG, null, message, null, null)
        }
    }

    /**
     * Issues a debug log entry for the result of a lazy message supplier. The result can be a plain text or any object
     * with a suitable [toString()] method.
     *
     * The passed message supplier will only be evaluated, if the debug severity level is enabled for the actual class.
     * This prevents unnecessary computing of the message if the debug severity level is disabled.
     *
     * Example:
     *
     *    Logger.debug { "Hello ${person.name}!" }
     *
     * @param message The lazy supplier for evaluating the message to log
     */
    fun debug(message: () -> Any?) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            backend.log(location.get(), null, Level.DEBUG, null, message.asSupplier(), null, null)
        }
    }

    /**
     * Issues a debug log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted, if the debug severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
     * if the debug severity level is enabled for the actual class.
     *
     * Example:
     *
     *    Logger.debug("User {} registered on {dd/MM/yyyy}", "Alice", date)
     *    Logger.debug("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values or lazy suppliers for the placeholders
     */
    fun debug(message: String, vararg arguments: Any?) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            backend.log(location.get(), null, Level.DEBUG, null, message, arguments.withSuppliers(), formatter)
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
            backend.log(location.get(), null, Level.DEBUG, exception, null, null, null)
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
    fun debug(exception: Throwable, message: String) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            backend.log(location.get(), null, Level.DEBUG, exception, message, null, null)
        }
    }

    /**
     * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom lazy message
     * supplier.
     *
     * The passed message supplier will only be evaluated, if the debug severity level is enabled for the actual class.
     * This prevents unnecessary computing of the message if the debug severity level is disabled.
     *
     * Example:
     *
     *    Logger.debug(ex) { "User ${person.name} has broken the system" }
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supplier for evaluating the message to log
     */
    fun debug(exception: Throwable, message: () -> String) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            backend.log(location.get(), null, Level.DEBUG, exception, message.asSupplier(), null, null)
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
     * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
     * if the debug severity level is enabled for the actual class.
     *
     * Example:
     *
     *    Logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
     *    Logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values or lazy suppliers for the placeholders
     */
    fun debug(exception: Throwable, message: String?, vararg arguments: Any?) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityDebug)
            backend.log(location.get(), null, Level.DEBUG, exception, message, arguments.withSuppliers(), formatter)
        }
    }

    /**
     * Checks if the info severity level is enabled for the actual class.
     *
     * If this method returns `true`, an issued info log entry will be output. If this method returns `false`, issued
     * info log entries will be discarded.
     *
     * @return `true` if enabled, otherwise `false`
     */
    fun isInfoEnabled(): Boolean {
        return visibilityInfo != OutputDetails.DISABLED &&
            backend.isEnabled(runtime.getDirectCaller(visibilityInfo), null, Level.INFO)
    }

    /**
     * Issues an info log entry for any object with a suitable [toString()] method.
     *
     * The [toString()] method of a passed message object will only be called, if the info severity level is enabled for
     * the actual class.
     *
     * Example:
     *
     *    Logger.info(42)
     *
     * @param message The message to log
     */
    fun info(message: Any?) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            backend.log(location.get(), null, Level.INFO, null, message, null, null)
        }
    }

    /**
     * Issues an info log entry for a plain text.
     *
     * Example:
     *
     *    Logger.info("Hello World!")
     *
     * @param message The message to log
     */
    fun info(message: String) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            backend.log(location.get(), null, Level.INFO, null, message, null, null)
        }
    }

    /**
     * Issues an info log entry for the result of a lazy message supplier. The result can be a plain text or any object
     * with a suitable [toString()] method.
     *
     * The passed message supplier will only be evaluated, if the info severity level is enabled for the actual class.
     * This prevents unnecessary computing of the message if the info severity level is disabled.
     *
     * Example:
     *
     *    Logger.info { "Hello ${person.name}!" }
     *
     * @param message The lazy supplier for evaluating the message to log
     */
    fun info(message: () -> Any?) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            backend.log(location.get(), null, Level.INFO, null, message.asSupplier(), null, null)
        }
    }

    /**
     * Issues an info log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted, if the info severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
     * if the info severity level is enabled for the actual class.
     *
     * Example:
     *
     *    Logger.info("User {} registered on {dd/MM/yyyy}", "Alice", date)
     *    Logger.info("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values or lazy suppliers for the placeholders
     */
    fun info(message: String, vararg arguments: Any?) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            backend.log(location.get(), null, Level.INFO, null, message, arguments.withSuppliers(), formatter)
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
            backend.log(location.get(), null, Level.INFO, exception, null, null, null)
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
    fun info(exception: Throwable, message: String) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            backend.log(location.get(), null, Level.INFO, exception, message, null, null)
        }
    }

    /**
     * Issues an info log entry for an exception (or any other kind of throwable) together with a custom lazy message
     * supplier.
     *
     * The passed message supplier will only be evaluated, if the info severity level is enabled for the actual class.
     * This prevents unnecessary computing of the message if the info severity level is disabled.
     *
     * Example:
     *
     *    Logger.info(ex) { "User ${person.name} has broken the system" }
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supplier for evaluating the message to log
     */
    fun info(exception: Throwable, message: () -> String) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            backend.log(location.get(), null, Level.INFO, exception, message.asSupplier(), null, null)
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
     * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
     * if the info severity level is enabled for the actual class.
     *
     * Example:
     *
     *    Logger.info(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
     *    Logger.info(ex, "User {} broke it on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values or lazy suppliers for the placeholders
     */
    fun info(exception: Throwable, message: String?, vararg arguments: Any?) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityInfo)
            backend.log(location.get(), null, Level.INFO, exception, message, arguments.withSuppliers(), formatter)
        }
    }

    /**
     * Checks if the warn severity level is enabled for the actual class.
     *
     * If this method returns `true`, an issued warning log entry will be output. If this method returns `false`,
     * issued warning log entries will be discarded.
     *
     * @return `true` if enabled, otherwise `false`
     */
    fun isWarnEnabled(): Boolean {
        return visibilityWarn != OutputDetails.DISABLED &&
            backend.isEnabled(runtime.getDirectCaller(visibilityWarn), null, Level.WARN)
    }

    /**
     * Issues a warning log entry for any object with a suitable [toString()] method.
     *
     * The [toString()] method of a passed message object will only be called, if the warn severity level is enabled for
     * the actual class.
     *
     * Example:
     *
     *    Logger.warn(42)
     *
     * @param message The message to log
     */
    fun warn(message: Any?) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            backend.log(location.get(), null, Level.WARN, null, message, null, null)
        }
    }

    /**
     * Issues a warning log entry for a plain text.
     *
     * Example:
     *
     *    Logger.warn("Hello World!")
     *
     * @param message The message to log
     */
    fun warn(message: String) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            backend.log(location.get(), null, Level.WARN, null, message, null, null)
        }
    }

    /**
     * Issues a warning log entry for the result of a lazy message supplier. The result can be a plain text or any
     * object with a suitable [toString()] method.
     *
     * The passed message supplier will only be evaluated, if the warn severity level is enabled for the actual class.
     * This prevents unnecessary computing of the message if the warn severity level is disabled.
     *
     * Example:
     *
     *    Logger.warn { "Hello ${person.name}!" }
     *
     * @param message The lazy supplier for evaluating the message to log
     */
    fun warn(message: () -> Any?) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            backend.log(location.get(), null, Level.WARN, null, message.asSupplier(), null, null)
        }
    }

    /**
     * Issues a warning log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted, if the warn severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved, if
     * the warn severity level is enabled for the actual class.
     *
     * Example:
     *
     *    Logger.warn("User {} registered on {dd/MM/yyyy}", "Alice", date)
     *    Logger.warn("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values or lazy suppliers for the placeholders
     */
    fun warn(message: String, vararg arguments: Any?) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            backend.log(location.get(), null, Level.WARN, null, message, arguments.withSuppliers(), formatter)
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
            backend.log(location.get(), null, Level.WARN, exception, null, null, null)
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
    fun warn(exception: Throwable, message: String) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            backend.log(location.get(), null, Level.WARN, exception, message, null, null)
        }
    }

    /**
     * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom lazy message
     * supplier.
     *
     * The passed message supplier will only be evaluated, if the warn severity level is enabled for the actual class.
     * This prevents unnecessary computing of the message if the warn severity level is disabled.
     *
     * Example:
     *
     *    Logger.warn(ex) { "User ${person.name} has broken the system" }
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supplier for evaluating the message to log
     */
    fun warn(exception: Throwable, message: () -> String) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            backend.log(location.get(), null, Level.WARN, exception, message.asSupplier(), null, null)
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
     * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
     * if the warn severity level is enabled for the actual class.
     *
     * Example:
     *
     *    Logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
     *    Logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values or lazy suppliers for the placeholders
     */
    fun warn(exception: Throwable, message: String?, vararg arguments: Any?) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityWarn)
            backend.log(location.get(), null, Level.WARN, exception, message, arguments.withSuppliers(), formatter)
        }
    }

    /**
     * Checks if the error severity level is enabled for the actual class.
     *
     * If this method returns `true`, an issued error log entry will be output. If this method returns `false`, issued
     * error log entries will be discarded.
     *
     * @return `true` if enabled, otherwise `false`
     */
    fun isErrorEnabled(): Boolean {
        return visibilityError != OutputDetails.DISABLED &&
            backend.isEnabled(runtime.getDirectCaller(visibilityError), null, Level.ERROR)
    }

    /**
     * Issues an error log entry for any object with a suitable [toString()] method.
     *
     * The [toString()] method of a passed message object will only be called, if the error severity level is enabled
     * for the actual class.
     *
     * Example:
     *
     *    Logger.error(42)
     *
     * @param message The message to log
     */
    fun error(message: Any?) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            backend.log(location.get(), null, Level.ERROR, null, message, null, null)
        }
    }

    /**
     * Issues an error log entry for a plain text.
     *
     * Example:
     *
     *    Logger.error("Hello World!")
     *
     * @param message The message to log
     */
    fun error(message: String) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            backend.log(location.get(), null, Level.ERROR, null, message, null, null)
        }
    }

    /**
     * Issues an error log entry for the result of a lazy message supplier. The result can be a plain text or any object
     * with a suitable [toString()] method.
     *
     * The passed message supplier will only be evaluated, if the error severity level is enabled for the actual class.
     * This prevents unnecessary computing of the message if the error severity level is disabled.
     *
     * Example:
     *
     *    Logger.error { "Hello ${person.name}!" }
     *
     * @param message The lazy supplier for evaluating the message to log
     */
    fun error(message: () -> Any?) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            backend.log(location.get(), null, Level.ERROR, null, message.asSupplier(), null, null)
        }
    }

    /**
     * Issues an error log entry for a text message with placeholders.
     *
     * The passed text message will only be formatted, if the error severity level is enabled for the actual class.
     * Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format patterns
     * can be provided for numbers, dates, and other formattable values.
     *
     * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
     * if the error severity level is enabled for the actual class.
     *
     * Example:
     *
     *    Logger.error("User {} registered on {dd/MM/yyyy}", "Alice", date)
     *    Logger.error("User {} registered on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values or lazy suppliers for the placeholders
     */
    fun error(message: String, vararg arguments: Any?) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            backend.log(location.get(), null, Level.ERROR, null, message, arguments.withSuppliers(), formatter)
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
            backend.log(location.get(), null, Level.ERROR, exception, null, null, null)
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
    fun error(exception: Throwable, message: String) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            backend.log(location.get(), null, Level.ERROR, exception, message, null, null)
        }
    }

    /**
     * Issues an error log entry for an exception (or any other kind of throwable) together with a custom lazy message
     * supplier.
     *
     * The passed message supplier will only be evaluated, if the error severity level is enabled for the actual class.
     * This prevents unnecessary computing of the message if the error severity level is disabled.
     *
     * Example:
     *
     *    Logger.error(ex) { "User ${person.name} has broken the system" }
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supplier for evaluating the message to log
     */
    fun error(exception: Throwable, message: () -> String) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            backend.log(location.get(), null, Level.ERROR, exception, message.asSupplier(), null, null)
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
     * Arguments can be either passed as real values or as lazy suppliers. Lazy suppliers will be only resolved,
     * if the error severity level is enabled for the actual class.
     *
     * Example:
     *
     *    Logger.error(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date)
     *    Logger.error(ex, "User {} broke it on {dd/MM/yyyy}", person::name, { Instant.now() })
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values or lazy suppliers for the placeholders
     */
    fun error(exception: Throwable, message: String?, vararg arguments: Any?) {
        if (visibilityError != OutputDetails.DISABLED) {
            val location = runtime.getDirectCaller(visibilityError)
            backend.log(location.get(), null, Level.ERROR, exception, message, arguments.withSuppliers(), formatter)
        }
    }
}
