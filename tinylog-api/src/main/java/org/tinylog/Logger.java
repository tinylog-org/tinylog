package org.tinylog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.Tinylog;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Static logger for issuing log entries.
 */
public final class Logger {

	private static final int CALLER_STACK_TRACE_DEPTH = 2;

	private static final Framework framework = Tinylog.getFramework();
	private static final RuntimeFlavor runtime = framework.getRuntime();
	private static final LoggingBackend backend = framework.getLoggingBackend();
	private static final LevelVisibility visibility = backend.getLevelVisibility(null);
	private static final MessageFormatter formatter = new EnhancedMessageFormatter(framework);

	private static final ConcurrentMap<String, TaggedLogger> taggedLoggers = new ConcurrentHashMap<>();

	/** */
	private Logger() {
	}

	/**
	 * Retrieves a tagged logger instance. Category tags are case-sensitive. If a tagged logger does not yet exist for
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
				key -> new TaggedLogger(null, framework)
			);
		} else {
			return taggedLoggers.computeIfAbsent(
				tag,
				key -> new TaggedLogger(key, framework)
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
		return visibility.isTraceEnabled()
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), null, Level.TRACE);
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
		if (visibility.isTraceEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.TRACE, null, message, null, null);
		}
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
		if (visibility.isTraceEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.TRACE, null, message, null, null);
		}
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
		if (visibility.isTraceEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.TRACE, null, message, arguments, formatter);
		}
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
		if (visibility.isTraceEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.TRACE, null, message, arguments, formatter);
		}
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
		if (visibility.isTraceEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.TRACE, exception, null, null, null);
		}
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
		if (visibility.isTraceEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.TRACE, exception, message, null, null);
		}
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
		if (visibility.isTraceEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.TRACE, exception, message, null, null);
		}
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
		if (visibility.isTraceEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.TRACE, exception, message, arguments, formatter);
		}
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
		if (visibility.isTraceEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.TRACE, exception, message, arguments, formatter);
		}
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
		return visibility.isDebugEnabled()
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), null, Level.DEBUG);
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
		if (visibility.isDebugEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.DEBUG, null, message, null, null);
		}
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
		if (visibility.isDebugEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.DEBUG, null, message, null, null);
		}
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
		if (visibility.isDebugEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.DEBUG, null, message, arguments, formatter);
		}
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
		if (visibility.isDebugEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.DEBUG, null, message, arguments, formatter);
		}
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
		if (visibility.isDebugEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.DEBUG, exception, null, null, null);
		}
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
		if (visibility.isDebugEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.DEBUG, exception, message, null, null);
		}
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
		if (visibility.isDebugEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.DEBUG, exception, message, null, null);
		}
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
		if (visibility.isDebugEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.DEBUG, exception, message, arguments, formatter);
		}
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
		if (visibility.isDebugEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.DEBUG, exception, message, arguments, formatter);
		}
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
		return visibility.isInfoEnabled()
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), null, Level.INFO);
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
		if (visibility.isInfoEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.INFO, null, message, null, null);
		}
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
		if (visibility.isInfoEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.INFO, null, message, null, null);
		}
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
		if (visibility.isInfoEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.INFO, null, message, arguments, formatter);
		}
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
		if (visibility.isInfoEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.INFO, null, message, arguments, formatter);
		}
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
		if (visibility.isInfoEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.INFO, exception, null, null, null);
		}
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
		if (visibility.isInfoEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.INFO, exception, message, null, null);
		}
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
		if (visibility.isInfoEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.INFO, exception, message, null, null);
		}
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
		if (visibility.isInfoEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.INFO, exception, message, arguments, formatter);
		}
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
		if (visibility.isInfoEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.INFO, exception, message, arguments, formatter);
		}
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
		return visibility.isWarnEnabled()
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), null, Level.WARN);
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
		if (visibility.isWarnEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.WARN, null, message, null, null);
		}
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
		if (visibility.isWarnEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.WARN, null, message, null, null);
		}
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
		if (visibility.isWarnEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.WARN, null, message, arguments, formatter);
		}
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
		if (visibility.isWarnEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.WARN, null, message, arguments, formatter);
		}
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
		if (visibility.isWarnEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.WARN, exception, null, null, null);
		}
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
		if (visibility.isWarnEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.WARN, exception, message, null, null);
		}
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
		if (visibility.isWarnEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.WARN, exception, message, null, null);
		}
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
		if (visibility.isWarnEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.WARN, exception, message, arguments, formatter);
		}
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
		if (visibility.isWarnEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.WARN, exception, message, arguments, formatter);
		}
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
		return visibility.isErrorEnabled()
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), null, Level.ERROR);
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
		if (visibility.isErrorEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.ERROR, null, message, null, null);
		}
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
		if (visibility.isErrorEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.ERROR, null, message, null, null);
		}
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
		if (visibility.isErrorEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.ERROR, null, message, arguments, formatter);
		}
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
		if (visibility.isErrorEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.ERROR, null, message, arguments, formatter);
		}
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
		if (visibility.isErrorEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.ERROR, exception, null, null, null);
		}
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
		if (visibility.isErrorEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.ERROR, exception, message, null, null);
		}
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
		if (visibility.isErrorEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.ERROR, exception, message, null, null);
		}
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
		if (visibility.isErrorEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.ERROR, exception, message, arguments, formatter);
		}
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
		if (visibility.isErrorEnabled()) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, null, Level.ERROR, exception, message, arguments, formatter);
		}
	}

}
