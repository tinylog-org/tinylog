package org.tinylog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.tinylog.core.Framework;
import org.tinylog.core.Tinylog;

/**
 * Static logger for issuing log entries.
 */
public final class Logger {

	private static final int CALLER_STACK_TRACE_DEPTH = 2;

	private static final Framework framework;
	private static final ConcurrentMap<String, TaggedLogger> taggedLoggers;
	private static final TaggedLogger logger;

	static {
		framework = Tinylog.getFramework();
		taggedLoggers = new ConcurrentHashMap<>();
		logger = new TaggedLogger(CALLER_STACK_TRACE_DEPTH + 1, null, framework);
	}

	/** */
	private Logger() {
	}

	/**
	 * Retrieves a tagged logger instance. Category tags are case-sensitive. If a tagged logger does not yet exists for
	 * the passed tag, a new logger will be created. This method always returns the same logger instance for the same
	 * tag.
	 *
	 * @param tag The case-sensitive category tag of the requested logger, or {@code null} for receiving an untagged
	 *            logger
	 * @return Logger instance
	 */
	public static TaggedLogger tag(String tag) {
		if (tag == null || tag.isEmpty()) {
			return taggedLoggers.computeIfAbsent(
				"",
				key -> new TaggedLogger(CALLER_STACK_TRACE_DEPTH, null, framework)
			);
		} else {
			return taggedLoggers.computeIfAbsent(
				tag,
				key -> new TaggedLogger(CALLER_STACK_TRACE_DEPTH, key, framework)
			);
		}
	}

	/**
	 * Checks if the trace severity level is enabled for the actual class.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued trace log entry will be output. If this method returns
	 *     {@code false}, issued trace log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public static boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	/**
	 * Issues a trace log entry for a plain text or any object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The {@link #toString()} method of a passed message object will only be called, if the trace severity level
	 *     is enabled for the actual class.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.trace("Hello World!");</code></pre>
	 * </p>
	 *
	 * @param message The message to log
	 */
	public static void trace(Object message) {
		logger.trace(message);
	}

	/**
	 * Issues a trace log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the trace severity level is enabled for the actual
	 *     class. This prevents unnecessary computing of the message if the trace severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.trace(() -> "Hello " + person.geName() + "!");</code></pre>
	 * </p>
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public static void trace(Supplier<?> message) {
		logger.trace(message);
	}

	/**
	 * Issues a trace log entry for a text message with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the trace severity level is enabled for the actual class.
	 *     Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format
	 *     patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.trace("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public static void trace(String message, Object... arguments) {
		logger.trace(message, arguments);
	}

	/**
	 * Issues a trace log entry for a text message with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     trace severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.trace("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public static void trace(String message, Supplier<?>... arguments) {
		logger.trace(message, arguments);
	}

	/**
	 * Issues a trace log entry for an exception or any other kind of throwable.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.trace(ex);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	public static void trace(Throwable exception) {
		logger.trace(exception);
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.trace(ex, "Oops, something went wrong");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	public static void trace(Throwable exception, String message) {
		logger.trace(exception, message);
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the trace severity level is enabled for the actual
	 *     class. This prevents unnecessary computing of the message if the trace severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.trace(ex, () -> "User " + person.getName() + " has broken the system");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public static void trace(Throwable exception, Supplier<String> message) {
		logger.trace(exception, message);
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the trace severity level is enabled for the actual class.
	 *     Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format
	 *     patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public static void trace(Throwable exception, String message, Object... arguments) {
		logger.trace(exception, message, arguments);
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     trace severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public static void trace(Throwable exception, String message, Supplier<?>... arguments) {
		logger.trace(exception, message, arguments);
	}

	/**
	 * Checks if the debug severity level is enabled for the actual class.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued debug log entry will be output. If this method returns
	 *     {@code false}, issued debug log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public static boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	/**
	 * Issues a debug log entry for a plain text or any object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The {@link #toString()} method of a passed message object will only be called, if the debug severity level
	 *     is enabled for the actual class.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.debug("Hello World!");</code></pre>
	 * </p>
	 *
	 * @param message The message to log
	 */
	public static void debug(Object message) {
		logger.debug(message);
	}

	/**
	 * Issues a debug log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the debug severity level is enabled for the actual
	 *     class. This prevents unnecessary computing of the message if the debug severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.debug(() -> "Hello " + person.geName() + "!");</code></pre>
	 * </p>
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public static void debug(Supplier<?> message) {
		logger.debug(message);
	}

	/**
	 * Issues a debug log entry for a text message with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the debug severity level is enabled for the actual class.
	 *     Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format
	 *     patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.debug("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public static void debug(String message, Object... arguments) {
		logger.debug(message, arguments);
	}

	/**
	 * Issues a debug log entry for a text message with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     debug severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.debug("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public static void debug(String message, Supplier<?>... arguments) {
		logger.debug(message, arguments);
	}

	/**
	 * Issues a debug log entry for an exception or any other kind of throwable.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.debug(ex);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	public static void debug(Throwable exception) {
		logger.debug(exception);
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.debug(ex, "Oops, something went wrong");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	public static void debug(Throwable exception, String message) {
		logger.debug(exception, message);
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the debug severity level is enabled for the actual
	 *     class. This prevents unnecessary computing of the message if the debug severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.debug(ex, () -> "User " + person.getName() + " has broken the system");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public static void debug(Throwable exception, Supplier<String> message) {
		logger.debug(exception, message);
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the debug severity level is enabled for the actual class.
	 *     Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format
	 *     patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public static void debug(Throwable exception, String message, Object... arguments) {
		logger.debug(exception, message, arguments);
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     debug severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public static void debug(Throwable exception, String message, Supplier<?>... arguments) {
		logger.debug(exception, message, arguments);
	}

	/**
	 * Checks if the info severity level is enabled for the actual class.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued info log entry will be output. If this method returns
	 *     {@code false}, issued info log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public static boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	/**
	 * Issues an info log entry for a plain text or any object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The {@link #toString()} method of a passed message object will only be called, if the info severity level
	 *     is enabled for the actual class.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.info("Hello World!");</code></pre>
	 * </p>
	 *
	 * @param message The message to log
	 */
	public static void info(Object message) {
		logger.info(message);
	}

	/**
	 * Issues an info log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the info severity level is enabled for the actual
	 *     class. This prevents unnecessary computing of the message if the info severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.info(() -> "Hello " + person.geName() + "!");</code></pre>
	 * </p>
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public static void info(Supplier<?> message) {
		logger.info(message);
	}

	/**
	 * Issues an info log entry for a text message with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the info severity level is enabled for the actual class.
	 *     Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format
	 *     patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.info("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public static void info(String message, Object... arguments) {
		logger.info(message, arguments);
	}

	/**
	 * Issues an info log entry for a text message with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     info severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.info("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public static void info(String message, Supplier<?>... arguments) {
		logger.info(message, arguments);
	}

	/**
	 * Issues an info log entry for an exception or any other kind of throwable.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.info(ex);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	public static void info(Throwable exception) {
		logger.info(exception);
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.info(ex, "Oops, something went wrong");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	public static void info(Throwable exception, String message) {
		logger.info(exception, message);
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the info severity level is enabled for the actual
	 *     class. This prevents unnecessary computing of the message if the info severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.info(ex, () -> "User " + person.getName() + " has broken the system");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public static void info(Throwable exception, Supplier<String> message) {
		logger.info(exception, message);
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the info severity level is enabled for the actual class.
	 *     Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format
	 *     patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.info(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public static void info(Throwable exception, String message, Object... arguments) {
		logger.info(exception, message, arguments);
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     info severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.info(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public static void info(Throwable exception, String message, Supplier<?>... arguments) {
		logger.info(exception, message, arguments);
	}

	/**
	 * Checks if the warn severity level is enabled for the actual class.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued warning log entry will be output. If this method returns
	 *     {@code false}, issued warning log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public static boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	/**
	 * Issues a warning log entry for a plain text or any object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The {@link #toString()} method of a passed message object will only be called, if the warn severity level
	 *     is enabled for the actual class.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.warn("Hello World!");</code></pre>
	 * </p>
	 *
	 * @param message The message to log
	 */
	public static void warn(Object message) {
		logger.warn(message);
	}

	/**
	 * Issues a warning log entry for the result of a lazy message supplier. The result can be a plain text or any
	 * object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the warn severity level is enabled for the actual
	 *     class. This prevents unnecessary computing of the message if the warn severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.warn(() -> "Hello " + person.geName() + "!");</code></pre>
	 * </p>
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public static void warn(Supplier<?> message) {
		logger.warn(message);
	}

	/**
	 * Issues a warning log entry for a text message with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the warn severity level is enabled for the actual class.
	 *     Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format
	 *     patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.warn("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public static void warn(String message, Object... arguments) {
		logger.warn(message, arguments);
	}

	/**
	 * Issues a warning log entry for a text message with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     warn severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.warn("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public static void warn(String message, Supplier<?>... arguments) {
		logger.warn(message, arguments);
	}

	/**
	 * Issues a warning log entry for an exception or any other kind of throwable.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.warn(ex);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	public static void warn(Throwable exception) {
		logger.warn(exception);
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.warn(ex, "Oops, something went wrong");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	public static void warn(Throwable exception, String message) {
		logger.warn(exception, message);
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the warn severity level is enabled for the actual
	 *     class. This prevents unnecessary computing of the message if the warn severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.warn(ex, () -> "User " + person.getName() + " has broken the system");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public static void warn(Throwable exception, Supplier<String> message) {
		logger.warn(exception, message);
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the warn severity level is enabled for the actual class.
	 *     Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format
	 *     patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public static void warn(Throwable exception, String message, Object... arguments) {
		logger.warn(exception, message, arguments);
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     warn severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public static void warn(Throwable exception, String message, Supplier<?>... arguments) {
		logger.warn(exception, message, arguments);
	}

	/**
	 * Checks if the error severity level is enabled for the actual class.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued error log entry will be output. If this method returns
	 *     {@code false}, issued error log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public static boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	/**
	 * Issues an error log entry for a plain text or any object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The {@link #toString()} method of a passed message object will only be called, if the error severity level
	 *     is enabled for the actual class.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.error("Hello World!");</code></pre>
	 * </p>
	 *
	 * @param message The message to log
	 */
	public static void error(Object message) {
		logger.error(message);
	}

	/**
	 * Issues an error log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the error severity level is enabled for the actual
	 *     class. This prevents unnecessary computing of the message if the error severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.error(() -> "Hello " + person.geName() + "!");</code></pre>
	 * </p>
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public static void error(Supplier<?> message) {
		logger.error(message);
	}

	/**
	 * Issues an error log entry for a text message with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the error severity level is enabled for the actual class.
	 *     Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format
	 *     patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.error("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public static void error(String message, Object... arguments) {
		logger.error(message, arguments);
	}

	/**
	 * Issues an error log entry for a text message with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     error severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.error("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public static void error(String message, Supplier<?>... arguments) {
		logger.error(message, arguments);
	}

	/**
	 * Issues an error log entry for an exception or any other kind of throwable.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.error(ex);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	public static void error(Throwable exception) {
		logger.error(exception);
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.error(ex, "Oops, something went wrong");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	public static void error(Throwable exception, String message) {
		logger.error(exception, message);
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the error severity level is enabled for the actual
	 *     class. This prevents unnecessary computing of the message if the error severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.error(ex, () -> "User " + person.getName() + " has broken the system");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public static void error(Throwable exception, Supplier<String> message) {
		logger.error(exception, message);
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the error severity level is enabled for the actual class.
	 *     Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally, format
	 *     patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.error(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public static void error(Throwable exception, String message, Object... arguments) {
		logger.error(exception, message, arguments);
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     error severity level is enabled for the actual class. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>Logger.error(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public static void error(Throwable exception, String message, Supplier<?>... arguments) {
		logger.error(exception, message, arguments);
	}

}
