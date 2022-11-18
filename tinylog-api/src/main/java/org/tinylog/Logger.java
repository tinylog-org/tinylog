package org.tinylog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.Tinylog;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Static logger for issuing log entries.
 */
public final class Logger {

    private static final ConcurrentMap<String, TaggedLogger> taggedLoggers;

    private static final Framework framework;
    private static final RuntimeFlavor runtime;
    private static final LoggingBackend backend;
    private static final MessageFormatter formatter;

    private static final OutputDetails visibilityTrace;
    private static final OutputDetails visibilityDebug;
    private static final OutputDetails visibilityInfo;
    private static final OutputDetails visibilityWarn;
    private static final OutputDetails visibilityError;

    static {
        taggedLoggers = new ConcurrentHashMap<>();

        framework = Tinylog.getFramework();
        runtime = framework.getRuntime();
        backend = framework.getLoggingBackend();
        formatter = new EnhancedMessageFormatter(framework);

        LevelVisibility visibility = backend.getLevelVisibilityByTag(null);

        visibilityTrace = visibility.getTrace();
        visibilityDebug = visibility.getDebug();
        visibilityInfo = visibility.getInfo();
        visibilityWarn = visibility.getWarn();
        visibilityError = visibility.getError();
    }

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
        return visibilityTrace != OutputDetails.DISABLED
            && backend.isEnabled(runtime.getDirectCaller(visibilityTrace), null, Level.TRACE);
    }

    /**
     * Issues a trace log entry for a plain text or any object with a suitable {@link #toString()} method.
     *
     * <p>
     *     The {@link #toString()} method of a passed message object will only be called, if the trace severity level
     *     is enabled for the actual class.
     * </p>
     *
     * <p>Example:</p>
     * <pre><code>Logger.trace("Hello World!");</code></pre>
     *
     * @param message The message to log
     */
    public static void trace(Object message) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityTrace);
            backend.log(location.get(), null, Level.TRACE, null, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.trace(() -&gt; "Hello " + person.geName() + "!");</code></pre>
     *
     * @param message The lazy supplier for evaluating the message to log
     */
    public static void trace(Supplier<?> message) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityTrace);
            backend.log(location.get(), null, Level.TRACE, null, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.trace("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    public static void trace(String message, Object... arguments) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityTrace);
            backend.log(location.get(), null, Level.TRACE, null, message, arguments, formatter);
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
     * <p>Example:</p>
     * <pre><code>Logger.trace("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
     *
     * @param message The text message with placeholders to log
     * @param arguments Lazy suppliers for the real values for the placeholders
     */
    public static void trace(String message, Supplier<?>... arguments) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityTrace);
            backend.log(location.get(), null, Level.TRACE, null, message, arguments, formatter);
        }
    }

    /**
     * Issues a trace log entry for an exception or any other kind of throwable.
     *
     * <p>Example:</p>
     * <pre><code>Logger.trace(ex);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     */
    public static void trace(Throwable exception) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityTrace);
            backend.log(location.get(), null, Level.TRACE, exception, null, null, null);
        }
    }

    /**
     * Issues a trace log entry for an exception (or any other kind of throwable) together with a custom plain text
     * message.
     *
     * <p>Example:</p>
     * <pre><code>Logger.trace(ex, "Oops, something went wrong");</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The message to log
     */
    public static void trace(Throwable exception, String message) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityTrace);
            backend.log(location.get(), null, Level.TRACE, exception, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.trace(ex, () -&gt; "User " + person.getName() + " has broken the system");</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supplier for evaluating the message to log
     */
    public static void trace(Throwable exception, Supplier<String> message) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityTrace);
            backend.log(location.get(), null, Level.TRACE, exception, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    public static void trace(Throwable exception, String message, Object... arguments) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityTrace);
            backend.log(location.get(), null, Level.TRACE, exception, message, arguments, formatter);
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
     * <p>Example:</p>
     * <pre><code>Logger.trace(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments Lazy suppliers for the real values for the placeholders
     */
    public static void trace(Throwable exception, String message, Supplier<?>... arguments) {
        if (visibilityTrace != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityTrace);
            backend.log(location.get(), null, Level.TRACE, exception, message, arguments, formatter);
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
        return visibilityDebug != OutputDetails.DISABLED
            && backend.isEnabled(runtime.getDirectCaller(visibilityDebug), null, Level.DEBUG);
    }

    /**
     * Issues a debug log entry for a plain text or any object with a suitable {@link #toString()} method.
     *
     * <p>
     *     The {@link #toString()} method of a passed message object will only be called, if the debug severity level
     *     is enabled for the actual class.
     * </p>
     *
     * <p>Example:</p>
     * <pre><code>Logger.debug("Hello World!");</code></pre>
     *
     * @param message The message to log
     */
    public static void debug(Object message) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityDebug);
            backend.log(location.get(), null, Level.DEBUG, null, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.debug(() -&gt; "Hello " + person.geName() + "!");</code></pre>
     *
     * @param message The lazy supplier for evaluating the message to log
     */
    public static void debug(Supplier<?> message) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityDebug);
            backend.log(location.get(), null, Level.DEBUG, null, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.debug("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    public static void debug(String message, Object... arguments) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityDebug);
            backend.log(location.get(), null, Level.DEBUG, null, message, arguments, formatter);
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
     * <p>Example:</p>
     * <pre><code>Logger.debug("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
     *
     * @param message The text message with placeholders to log
     * @param arguments Lazy suppliers for the real values for the placeholders
     */
    public static void debug(String message, Supplier<?>... arguments) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityDebug);
            backend.log(location.get(), null, Level.DEBUG, null, message, arguments, formatter);
        }
    }

    /**
     * Issues a debug log entry for an exception or any other kind of throwable.
     *
     * <p>Example:</p>
     * <pre><code>Logger.debug(ex);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     */
    public static void debug(Throwable exception) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityDebug);
            backend.log(location.get(), null, Level.DEBUG, exception, null, null, null);
        }
    }

    /**
     * Issues a debug log entry for an exception (or any other kind of throwable) together with a custom plain text
     * message.
     *
     * <p>Example:</p>
     * <pre><code>Logger.debug(ex, "Oops, something went wrong");</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The message to log
     */
    public static void debug(Throwable exception, String message) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityDebug);
            backend.log(location.get(), null, Level.DEBUG, exception, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.debug(ex, () -&gt; "User " + person.getName() + " has broken the system");</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supplier for evaluating the message to log
     */
    public static void debug(Throwable exception, Supplier<String> message) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityDebug);
            backend.log(location.get(), null, Level.DEBUG, exception, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    public static void debug(Throwable exception, String message, Object... arguments) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityDebug);
            backend.log(location.get(), null, Level.DEBUG, exception, message, arguments, formatter);
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
     * <p>Example:</p>
     * <pre><code>Logger.debug(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments Lazy suppliers for the real values for the placeholders
     */
    public static void debug(Throwable exception, String message, Supplier<?>... arguments) {
        if (visibilityDebug != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityDebug);
            backend.log(location.get(), null, Level.DEBUG, exception, message, arguments, formatter);
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
        return visibilityInfo != OutputDetails.DISABLED
            && backend.isEnabled(runtime.getDirectCaller(visibilityInfo), null, Level.INFO);
    }

    /**
     * Issues an info log entry for a plain text or any object with a suitable {@link #toString()} method.
     *
     * <p>
     *     The {@link #toString()} method of a passed message object will only be called, if the info severity level
     *     is enabled for the actual class.
     * </p>
     *
     * <p>Example:</p>
     * <pre><code>Logger.info("Hello World!");</code></pre>
     *
     * @param message The message to log
     */
    public static void info(Object message) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityInfo);
            backend.log(location.get(), null, Level.INFO, null, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.info(() -&gt; "Hello " + person.geName() + "!");</code></pre>
     *
     * @param message The lazy supplier for evaluating the message to log
     */
    public static void info(Supplier<?> message) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityInfo);
            backend.log(location.get(), null, Level.INFO, null, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.info("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    public static void info(String message, Object... arguments) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityInfo);
            backend.log(location.get(), null, Level.INFO, null, message, arguments, formatter);
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
     * <p>Example:</p>
     * <pre><code>Logger.info("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
     *
     * @param message The text message with placeholders to log
     * @param arguments Lazy suppliers for the real values for the placeholders
     */
    public static void info(String message, Supplier<?>... arguments) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityInfo);
            backend.log(location.get(), null, Level.INFO, null, message, arguments, formatter);
        }
    }

    /**
     * Issues an info log entry for an exception or any other kind of throwable.
     *
     * <p>Example:</p>
     * <pre><code>Logger.info(ex);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     */
    public static void info(Throwable exception) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityInfo);
            backend.log(location.get(), null, Level.INFO, exception, null, null, null);
        }
    }

    /**
     * Issues an info log entry for an exception (or any other kind of throwable) together with a custom plain text
     * message.
     *
     * <p>Example:</p>
     * <pre><code>Logger.info(ex, "Oops, something went wrong");</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The message to log
     */
    public static void info(Throwable exception, String message) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityInfo);
            backend.log(location.get(), null, Level.INFO, exception, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.info(ex, () -&gt; "User " + person.getName() + " has broken the system");</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supplier for evaluating the message to log
     */
    public static void info(Throwable exception, Supplier<String> message) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityInfo);
            backend.log(location.get(), null, Level.INFO, exception, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.info(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    public static void info(Throwable exception, String message, Object... arguments) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityInfo);
            backend.log(location.get(), null, Level.INFO, exception, message, arguments, formatter);
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
     * <p>Example:</p>
     * <pre><code>Logger.info(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments Lazy suppliers for the real values for the placeholders
     */
    public static void info(Throwable exception, String message, Supplier<?>... arguments) {
        if (visibilityInfo != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityInfo);
            backend.log(location.get(), null, Level.INFO, exception, message, arguments, formatter);
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
        return visibilityWarn != OutputDetails.DISABLED
            && backend.isEnabled(runtime.getDirectCaller(visibilityWarn), null, Level.WARN);
    }

    /**
     * Issues a warning log entry for a plain text or any object with a suitable {@link #toString()} method.
     *
     * <p>
     *     The {@link #toString()} method of a passed message object will only be called, if the warn severity level
     *     is enabled for the actual class.
     * </p>
     *
     * <p>Example:</p>
     * <pre><code>Logger.warn("Hello World!");</code></pre>
     *
     * @param message The message to log
     */
    public static void warn(Object message) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityWarn);
            backend.log(location.get(), null, Level.WARN, null, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.warn(() -&gt; "Hello " + person.geName() + "!");</code></pre>
     *
     * @param message The lazy supplier for evaluating the message to log
     */
    public static void warn(Supplier<?> message) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityWarn);
            backend.log(location.get(), null, Level.WARN, null, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.warn("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    public static void warn(String message, Object... arguments) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityWarn);
            backend.log(location.get(), null, Level.WARN, null, message, arguments, formatter);
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
     * <p>Example:</p>
     * <pre><code>Logger.warn("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
     *
     * @param message The text message with placeholders to log
     * @param arguments Lazy suppliers for the real values for the placeholders
     */
    public static void warn(String message, Supplier<?>... arguments) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityWarn);
            backend.log(location.get(), null, Level.WARN, null, message, arguments, formatter);
        }
    }

    /**
     * Issues a warning log entry for an exception or any other kind of throwable.
     *
     * <p>Example:</p>
     * <pre><code>Logger.warn(ex);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     */
    public static void warn(Throwable exception) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityWarn);
            backend.log(location.get(), null, Level.WARN, exception, null, null, null);
        }
    }

    /**
     * Issues a warning log entry for an exception (or any other kind of throwable) together with a custom plain text
     * message.
     *
     * <p>Example:</p>
     * <pre><code>Logger.warn(ex, "Oops, something went wrong");</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The message to log
     */
    public static void warn(Throwable exception, String message) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityWarn);
            backend.log(location.get(), null, Level.WARN, exception, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.warn(ex, () -&gt; "User " + person.getName() + " has broken the system");</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supplier for evaluating the message to log
     */
    public static void warn(Throwable exception, Supplier<String> message) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityWarn);
            backend.log(location.get(), null, Level.WARN, exception, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    public static void warn(Throwable exception, String message, Object... arguments) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityWarn);
            backend.log(location.get(), null, Level.WARN, exception, message, arguments, formatter);
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
     * <p>Example:</p>
     * <pre><code>Logger.warn(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments Lazy suppliers for the real values for the placeholders
     */
    public static void warn(Throwable exception, String message, Supplier<?>... arguments) {
        if (visibilityWarn != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityWarn);
            backend.log(location.get(), null, Level.WARN, exception, message, arguments, formatter);
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
        return visibilityError != OutputDetails.DISABLED
            && backend.isEnabled(runtime.getDirectCaller(visibilityError), null, Level.ERROR);
    }

    /**
     * Issues an error log entry for a plain text or any object with a suitable {@link #toString()} method.
     *
     * <p>
     *     The {@link #toString()} method of a passed message object will only be called, if the error severity level
     *     is enabled for the actual class.
     * </p>
     *
     * <p>Example:</p>
     * <pre><code>Logger.error("Hello World!");</code></pre>
     *
     * @param message The message to log
     */
    public static void error(Object message) {
        if (visibilityError != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityError);
            backend.log(location.get(), null, Level.ERROR, null, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.error(() -&gt; "Hello " + person.geName() + "!");</code></pre>
     *
     * @param message The lazy supplier for evaluating the message to log
     */
    public static void error(Supplier<?> message) {
        if (visibilityError != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityError);
            backend.log(location.get(), null, Level.ERROR, null, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.error("The radius of {} are {#,###} km", "earth", 6371);</code></pre>
     *
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    public static void error(String message, Object... arguments) {
        if (visibilityError != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityError);
            backend.log(location.get(), null, Level.ERROR, null, message, arguments, formatter);
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
     * <p>Example:</p>
     * <pre><code>Logger.error("The radius of {} are {#,###} km", planet::getName, planet::getRadius);</code></pre>
     *
     * @param message The text message with placeholders to log
     * @param arguments Lazy suppliers for the real values for the placeholders
     */
    public static void error(String message, Supplier<?>... arguments) {
        if (visibilityError != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityError);
            backend.log(location.get(), null, Level.ERROR, null, message, arguments, formatter);
        }
    }

    /**
     * Issues an error log entry for an exception or any other kind of throwable.
     *
     * <p>Example:</p>
     * <pre><code>Logger.error(ex);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     */
    public static void error(Throwable exception) {
        if (visibilityError != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityError);
            backend.log(location.get(), null, Level.ERROR, exception, null, null, null);
        }
    }

    /**
     * Issues an error log entry for an exception (or any other kind of throwable) together with a custom plain text
     * message.
     *
     * <p>Example:</p>
     * <pre><code>Logger.error(ex, "Oops, something went wrong");</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The message to log
     */
    public static void error(Throwable exception, String message) {
        if (visibilityError != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityError);
            backend.log(location.get(), null, Level.ERROR, exception, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.error(ex, () -&gt; "User " + person.getName() + " has broken the system");</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The lazy supplier for evaluating the message to log
     */
    public static void error(Throwable exception, Supplier<String> message) {
        if (visibilityError != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityError);
            backend.log(location.get(), null, Level.ERROR, exception, message, null, null);
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
     * <p>Example:</p>
     * <pre><code>Logger.error(ex, "User {} broke it on {dd/MM/yyyy}", "Alice", date);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments The real values for the placeholders
     */
    public static void error(Throwable exception, String message, Object... arguments) {
        if (visibilityError != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityError);
            backend.log(location.get(), null, Level.ERROR, exception, message, arguments, formatter);
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
     * <p>Example:</p>
     * <pre><code>Logger.error(ex, "User {} broke it on {dd/MM/yyyy}", person::getName, Date::new);</code></pre>
     *
     * @param exception The exception or other kind of throwable to log
     * @param message The text message with placeholders to log
     * @param arguments Lazy suppliers for the real values for the placeholders
     */
    public static void error(Throwable exception, String message, Supplier<?>... arguments) {
        if (visibilityError != OutputDetails.DISABLED) {
            Supplier<?> location = runtime.getDirectCaller(visibilityError);
            backend.log(location.get(), null, Level.ERROR, exception, message, arguments, formatter);
        }
    }

}
