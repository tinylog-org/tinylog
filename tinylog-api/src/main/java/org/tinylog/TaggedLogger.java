package org.tinylog;

import java.util.function.Supplier;

import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Logger for issuing tagged log entries.
 */
public final class TaggedLogger {

	private static final int CALLER_STACK_TRACE_DEPTH = 2;

	private final String tag;
	private final RuntimeFlavor runtime;
	private final LoggingBackend backend;
	private final LevelVisibility visibility;
	private final MessageFormatter formatter;

	/**
	 * @param tag The case-sensitive category tag for the logger (can be {@code null})
	 * @param framework The actual framework instance
	 */
	TaggedLogger(String tag, Framework framework) {
		this.tag = tag;
		this.runtime = framework.getRuntime();
		this.backend = framework.getLoggingBackend();
		this.visibility = backend.getLevelVisibility(tag);
		this.formatter = new EnhancedMessageFormatter(framework);
	}

	/**
	 * Gets the assigned case-sensitive category tag.
	 *
	 * @return The assigned category tag, or {@code null} if the logger is untagged
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Checks if the trace severity level is enabled for the actual class and tag.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued trace log entry will be output. If this method returns
	 *     {@code false}, issued trace log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isTraceEnabled() {
		return visibility.getTrace() != OutputDetails.DISABLED
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.TRACE);
	}

	/**
	 * Issues a trace log entry for a plain text or any object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The {@link #toString()} method of a passed message object will only be called, if the trace severity level
	 *     is enabled for the actual class and tag.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.trace("Hello World!");</code></pre>
	 * </p>
	 *
	 * @param message The message to log
	 */
	public void trace(Object message) {
		if (visibility.getTrace() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.TRACE, null, message, null, null);
		}
	}

	/**
	 * Issues a trace log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the trace severity level is enabled for the actual
	 *     class and tag. This prevents unnecessary computing of the message if the trace severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.trace(() -> "Hello " + person.geName() + "!");</code></pre>
	 * </p>
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public void trace(Supplier<?> message) {
		if (visibility.getTrace() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.TRACE, null, message, null, null);
		}
	}

	/**
	 * Issues a trace log entry for a text message with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the trace severity level is enabled for the actual class
	 *     and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 *     format patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.trace("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public void trace(String message, Object... arguments) {
		if (visibility.getTrace() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.TRACE, null, message, arguments, formatter);
		}
	}

	/**
	 * Issues a trace log entry for a text message with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     trace severity level is enabled for the actual class and tag. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.trace("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public void trace(String message, Supplier<?>... arguments) {
		if (visibility.getTrace() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.TRACE, null, message, arguments, formatter);
		}
	}

	/**
	 * Issues a trace log entry for an exception or any other kind of throwable.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.trace(ex);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	public void trace(Throwable exception) {
		if (visibility.getTrace() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.TRACE, exception, null, null, null);
		}
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.trace(ex, "Oops, something went wrong");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	public void trace(Throwable exception, String message) {
		if (visibility.getTrace() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.TRACE, exception, message, null, null);
		}
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the trace severity level is enabled for the actual
	 *     class and tag. This prevents unnecessary computing of the message if the trace severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.trace(ex, () -> "User " + person.getName() + " has broken the system");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public void trace(Throwable exception, Supplier<String> message) {
		if (visibility.getTrace() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.TRACE, exception, message, null, null);
		}
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the trace severity level is enabled for the actual class
	 *     and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 *     format patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public void trace(Throwable exception, String message, Object... arguments) {
		if (visibility.getTrace() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.TRACE, exception, message, arguments, formatter);
		}
	}

	/**
	 * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     trace severity level is enabled for the actual class and tag. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public void trace(Throwable exception, String message, Supplier<?>... arguments) {
		if (visibility.getTrace() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.TRACE, exception, message, arguments, formatter);
		}
	}

	/**
	 * Checks if the debug severity level is enabled for the actual class and tag.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued debug log entry will be output. If this method returns
	 *     {@code false}, issued debug log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isDebugEnabled() {
		return visibility.getDebug() != OutputDetails.DISABLED
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.DEBUG);
	}

	/**
	 * Issues a debug log entry for a plain text or any object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The {@link #toString()} method of a passed message object will only be called, if the debug severity level
	 *     is enabled for the actual class and tag.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.debug("Hello World!");</code></pre>
	 * </p>
	 *
	 * @param message The message to log
	 */
	public void debug(Object message) {
		if (visibility.getDebug() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.DEBUG, null, message, null, null);
		}
	}

	/**
	 * Issues a debug log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the debug severity level is enabled for the actual
	 *     class and tag. This prevents unnecessary computing of the message if the debug severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.debug(() -> "Hello " + person.geName() + "!");</code></pre>
	 * </p>
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public void debug(Supplier<?> message) {
		if (visibility.getDebug() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.DEBUG, null, message, null, null);
		}
	}

	/**
	 * Issues a debug log entry for a text message with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the debug severity level is enabled for the actual class
	 *     and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 *     format patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.debug("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public void debug(String message, Object... arguments) {
		if (visibility.getDebug() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.DEBUG, null, message, arguments, formatter);
		}
	}

	/**
	 * Issues a debug log entry for a text message with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     debug severity level is enabled for the actual class and tag. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.debug("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public void debug(String message, Supplier<?>... arguments) {
		if (visibility.getDebug() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.DEBUG, null, message, arguments, formatter);
		}
	}

	/**
	 * Issues a debug log entry for an exception or any other kind of throwable.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.debug(ex);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	public void debug(Throwable exception) {
		if (visibility.getDebug() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.DEBUG, exception, null, null, null);
		}
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.debug(ex, "Oops, something went wrong");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	public void debug(Throwable exception, String message) {
		if (visibility.getDebug() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.DEBUG, exception, message, null, null);
		}
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the debug severity level is enabled for the actual
	 *     class and tag. This prevents unnecessary computing of the message if the debug severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.debug(ex, () -> "User " + person.getName() + " has broken the system");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public void debug(Throwable exception, Supplier<String> message) {
		if (visibility.getDebug() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.DEBUG, exception, message, null, null);
		}
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the debug severity level is enabled for the actual class
	 *     and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 *     format patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public void debug(Throwable exception, String message, Object... arguments) {
		if (visibility.getDebug() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.DEBUG, exception, message, arguments, formatter);
		}
	}

	/**
	 * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     debug severity level is enabled for the actual class and tag. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public void debug(Throwable exception, String message, Supplier<?>... arguments) {
		if (visibility.getDebug() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.DEBUG, exception, message, arguments, formatter);
		}
	}

	/**
	 * Checks if the info severity level is enabled for the actual class and tag.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued info log entry will be output. If this method returns
	 *     {@code false}, issued info log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isInfoEnabled() {
		return visibility.getInfo() != OutputDetails.DISABLED
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.INFO);
	}

	/**
	 * Issues an info log entry for a plain text or any object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The {@link #toString()} method of a passed message object will only be called, if the info severity level
	 *     is enabled for the actual class and tag.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.info("Hello World!");</code></pre>
	 * </p>
	 *
	 * @param message The message to log
	 */
	public void info(Object message) {
		if (visibility.getInfo() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.INFO, null, message, null, null);
		}
	}

	/**
	 * Issues an info log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the info severity level is enabled for the actual
	 *     class and tag. This prevents unnecessary computing of the message if the info severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.info(() -> "Hello " + person.geName() + "!");</code></pre>
	 * </p>
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public void info(Supplier<?> message) {
		if (visibility.getInfo() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.INFO, null, message, null, null);
		}
	}

	/**
	 * Issues an info log entry for a text message with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the info severity level is enabled for the actual class
	 *     and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 *     format patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.info("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public void info(String message, Object... arguments) {
		if (visibility.getInfo() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.INFO, null, message, arguments, formatter);
		}
	}

	/**
	 * Issues an info log entry for a text message with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     info severity level is enabled for the actual class and tag. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.info("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public void info(String message, Supplier<?>... arguments) {
		if (visibility.getInfo() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.INFO, null, message, arguments, formatter);
		}
	}

	/**
	 * Issues an info log entry for an exception or any other kind of throwable.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.info(ex);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	public void info(Throwable exception) {
		if (visibility.getInfo() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.INFO, exception, null, null, null);
		}
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.info(ex, "Oops, something went wrong");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	public void info(Throwable exception, String message) {
		if (visibility.getInfo() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.INFO, exception, message, null, null);
		}
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the info severity level is enabled for the actual
	 *     class and tag. This prevents unnecessary computing of the message if the info severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.info(ex, () -> "User " + person.getName() + " has broken the system");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public void info(Throwable exception, Supplier<String> message) {
		if (visibility.getInfo() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.INFO, exception, message, null, null);
		}
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the info severity level is enabled for the actual class
	 *     and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 *     format patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.info(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public void info(Throwable exception, String message, Object... arguments) {
		if (visibility.getInfo() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.INFO, exception, message, arguments, formatter);
		}
	}

	/**
	 * Issues an info log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     info severity level is enabled for the actual class and tag. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.info(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public void info(Throwable exception, String message, Supplier<?>... arguments) {
		if (visibility.getInfo() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.INFO, exception, message, arguments, formatter);
		}
	}

	/**
	 * Checks if the warn severity level is enabled for the actual class and tag.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued warning log entry will be output. If this method returns
	 *     {@code false}, issued warning log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isWarnEnabled() {
		return visibility.getWarn() != OutputDetails.DISABLED
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.WARN);
	}

	/**
	 * Issues a warning log entry for a plain text or any object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The {@link #toString()} method of a passed message object will only be called, if the warn severity level
	 *     is enabled for the actual class and tag.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.warn("Hello World!");</code></pre>
	 * </p>
	 *
	 * @param message The message to log
	 */
	public void warn(Object message) {
		if (visibility.getWarn() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.WARN, null, message, null, null);
		}
	}

	/**
	 * Issues a warning log entry for the result of a lazy message supplier. The result can be a plain text or any
	 * object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the warn severity level is enabled for the actual
	 *     class and tag. This prevents unnecessary computing of the message if the warn severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.warn(() -> "Hello " + person.geName() + "!");</code></pre>
	 * </p>
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public void warn(Supplier<?> message) {
		if (visibility.getWarn() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.WARN, null, message, null, null);
		}
	}

	/**
	 * Issues a warning log entry for a text message with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the warn severity level is enabled for the actual class
	 *     and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 *     format patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.warn("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public void warn(String message, Object... arguments) {
		if (visibility.getWarn() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.WARN, null, message, arguments, formatter);
		}
	}

	/**
	 * Issues a warning log entry for a text message with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     warn severity level is enabled for the actual class and tag. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.warn("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public void warn(String message, Supplier<?>... arguments) {
		if (visibility.getWarn() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.WARN, null, message, arguments, formatter);
		}
	}

	/**
	 * Issues a warning log entry for an exception or any other kind of throwable.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.warn(ex);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	public void warn(Throwable exception) {
		if (visibility.getWarn() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.WARN, exception, null, null, null);
		}
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.warn(ex, "Oops, something went wrong");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	public void warn(Throwable exception, String message) {
		if (visibility.getWarn() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.WARN, exception, message, null, null);
		}
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the warn severity level is enabled for the actual
	 *     class and tag. This prevents unnecessary computing of the message if the warn severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.warn(ex, () -> "User " + person.getName() + " has broken the system");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public void warn(Throwable exception, Supplier<String> message) {
		if (visibility.getWarn() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.WARN, exception, message, null, null);
		}
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the warn severity level is enabled for the actual class
	 *     and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 *     format patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public void warn(Throwable exception, String message, Object... arguments) {
		if (visibility.getWarn() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.WARN, exception, message, arguments, formatter);
		}
	}

	/**
	 * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     warn severity level is enabled for the actual class and tag. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public void warn(Throwable exception, String message, Supplier<?>... arguments) {
		if (visibility.getWarn() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.WARN, exception, message, arguments, formatter);
		}
	}

	/**
	 * Checks if the error severity level is enabled for the actual class and tag.
	 *
	 * <p>
	 *     If this method returns {@code true}, an issued error log entry will be output. If this method returns
	 *     {@code false}, issued error log entries will be discarded.
	 * </p>
	 *
	 * @return {@code true} if enabled, otherwise {@code false}
	 */
	public boolean isErrorEnabled() {
		return visibility.getError() != OutputDetails.DISABLED
			&& backend.isEnabled(runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH), tag, Level.ERROR);
	}

	/**
	 * Issues an error log entry for a plain text or any object with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The {@link #toString()} method of a passed message object will only be called, if the error severity level
	 *     is enabled for the actual class and tag.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.error("Hello World!");</code></pre>
	 * </p>
	 *
	 * @param message The message to log
	 */
	public void error(Object message) {
		if (visibility.getError() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.ERROR, null, message, null, null);
		}
	}

	/**
	 * Issues an error log entry for the result of a lazy message supplier. The result can be a plain text or any object
	 * with a suitable {@link #toString()} method.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the error severity level is enabled for the actual
	 *     class and tag. This prevents unnecessary computing of the message if the error severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.error(() -> "Hello " + person.geName() + "!");</code></pre>
	 * </p>
	 *
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public void error(Supplier<?> message) {
		if (visibility.getError() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.ERROR, null, message, null, null);
		}
	}

	/**
	 * Issues an error log entry for a text message with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the error severity level is enabled for the actual class
	 *     and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 *     format patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.error("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public void error(String message, Object... arguments) {
		if (visibility.getError() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.ERROR, null, message, arguments, formatter);
		}
	}

	/**
	 * Issues an error log entry for a text message with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     error severity level is enabled for the actual class and tag. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.error("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
	 * </p>
	 *
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public void error(String message, Supplier<?>... arguments) {
		if (visibility.getError() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.ERROR, null, message, arguments, formatter);
		}
	}

	/**
	 * Issues an error log entry for an exception or any other kind of throwable.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.error(ex);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 */
	public void error(Throwable exception) {
		if (visibility.getError() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.ERROR, exception, null, null, null);
		}
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom plain text
	 * message.
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.error(ex, "Oops, something went wrong");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The message to log
	 */
	public void error(Throwable exception, String message) {
		if (visibility.getError() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.ERROR, exception, message, null, null);
		}
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom lazy message
	 * supplier.
	 *
	 * <p>
	 *     The passed message supplier will only be evaluated, if the error severity level is enabled for the actual
	 *     class and tag. This prevents unnecessary computing of the message if the error severity level is disabled.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.error(ex, () -> "User " + person.getName() + " has broken the system");</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The lazy supplier for evaluating the message to log
	 */
	public void error(Throwable exception, Supplier<String> message) {
		if (visibility.getError() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.ERROR, exception, message, null, null);
		}
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders.
	 *
	 * <p>
	 *     The passed text message will only be formatted, if the error severity level is enabled for the actual class
	 *     and tag. Pairs of curly brackets "{}" can be used as placeholders for the passed arguments. Optionally,
	 *     format patterns can be provided for numbers, dates, and other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.error(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments The real values for the placeholders
	 */
	public void error(Throwable exception, String message, Object... arguments) {
		if (visibility.getError() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.ERROR, exception, message, arguments, formatter);
		}
	}

	/**
	 * Issues an error log entry for an exception (or any other kind of throwable) together with a custom text message
	 * with placeholders and lazy arguments.
	 *
	 * <p>
	 *     The passed text message will only be formatted and the lazy argument suppliers will only be evaluated, if the
	 *     error severity level is enabled for the actual class and tag. Pairs of curly brackets "{}" can be used as
	 *     placeholders for the passed arguments. Optionally, format patterns can be provided for numbers, dates, and
	 *     other formattable values.
	 * </p>
	 *
	 * <p>
	 *     Example:
	 *     <pre><code>logger.error(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
	 * </p>
	 *
	 * @param exception The exception or other kind of throwable to log
	 * @param message The text message with placeholders to log
	 * @param arguments Lazy suppliers for the real values for the placeholders
	 */
	public void error(Throwable exception, String message, Supplier<?>... arguments) {
		if (visibility.getError() != OutputDetails.DISABLED) {
			StackTraceLocation location = runtime.getStackTraceLocationAtIndex(CALLER_STACK_TRACE_DEPTH);
			backend.log(location, tag, Level.ERROR, exception, message, arguments, formatter);
		}
	}

}
