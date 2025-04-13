package org.tinylog.core;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.LocationResolver;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Immutable Log entry record. All fields are immutable, but some are lazy initialized without ensuring thread-safety
 * for performance reasons.
 */
public class LogEntry {

    private final Thread thread;
    private final Map<String, String> context;
    private final Object location;
    private final String tag;
    private final Level severityLevel;
    private final Throwable throwable;
    private final MessageFormatter formatter;
    private final String message;
    private final Object[] arguments;

    private Instant instant;
    private Duration uptime;
    private String formattedMessage;

    /**
     * @param thread The source thread of issue
     * @param context The present thread context values
     * @param location The stack trace location of the caller (supported types: {@link StackTraceElement},
     *                 {@link String}, {@link Class}, and {@code null})
     * @param tag The assigned tag (can be {@code null})
     * @param level The severity level
     * @param throwable The logged throwable (can be {@code null})
     * @param formatter The formatter to use for replacing placeholders in the passed message with real values (can be
     *                  {@code null} if there are no arguments)
     * @param message The human-readable logged text message with or without placeholders (can be {@code null})
     * @param arguments Argument values for all placeholders in the text message (can be {@code null})
     */
    public LogEntry(Thread thread, Map<String, String> context, Object location, String tag, Level level,
            Throwable throwable, MessageFormatter formatter, String message, Object[] arguments) {
        this.thread = thread;
        this.context = context;
        this.location = location;
        this.tag = tag;
        this.severityLevel = level;
        this.throwable = throwable;
        this.formatter = formatter;
        this.message = message;
        this.arguments = arguments;
    }

    /**
     * Gets the timestamp when this log entry was issued.
     *
     * <p>
     *     The timestamp of the first call is also returned for future calls.
     * </p>
     *
     * @return The date and time
     */
    public Instant getTimestamp() {
        if (instant == null) {
            instant = Instant.now();
        }

        return instant;
    }

    /**
     * Gets the uptime when this log entry was issued.
     *
     * <p>
     *     The uptime of the first call is also returned for future calls.
     * </p>
     *
     * @param runtime The runtime flavor to use for receiving the current uptime
     * @return The uptime
     */
    public Duration getUptime(RuntimeFlavor runtime) {
        if (uptime == null) {
            uptime = runtime.getUptime();
        }

        return uptime;
    }

    /**
     * Gets the thread, in which this log entry has been issued.
     *
     * @return The source thread
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * Gets the context values, which were set for the source thread, when issuing this log entry.
     *
     * @return The present thread context values
     */
    public Map<String, String> getContext() {
        return context;
    }

    /**
     * Gets the fully-qualified name of the class, in which this log entry has been issued.
     *
     * @return The fully-qualified source class name or {@code null}
     */
    public String getClassName() {
        return LocationResolver.getClassName(location);
    }

    /**
     * Gets the name of the method, in which this log entry has been issued.
     *
     * @return The source method name or {@code null}
     */
    public String getMethodName() {
        return LocationResolver.getMethodName(location);
    }

    /**
     * Gets the name of the file, in which this log entry has been issued.
     *
     * @return The source file name or {@code null}
     */
    public String getFileName() {
        return LocationResolver.getFileName(location);
    }

    /**
     * Gets the line number of the source file, in which this log entry has been issued.
     *
     * @return The line number of the source file or {@code -1}
     */
    public int getLineNumber() {
        return LocationResolver.getLineNumber(location);
    }

    /**
     * Gets the assigned tag.
     *
     * @return The assigned tag or {@code null}
     */
    public String getTag() {
        return tag;
    }

    /**
     * Gets the {@link Level severity level} of this log entry.
     *
     * @return The severity level
     */
    public Level getSeverityLevel() {
        return severityLevel;
    }

    /**
     * Gets the logged throwable.
     *
     * @return The logged throwable or {@code null}
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Gets the formatted human-readable logged text message.
     *
     * @param configuration The current tinylog configuration
     * @return The formatted human-readable logged text message or {@code null}
     */
    public String getFormattedMessage(Configuration configuration) {
        if (formattedMessage == null) {
            if (arguments == null || arguments.length == 0) {
                formattedMessage = message;
            } else {
                formattedMessage = formatter.format(configuration, message, arguments);
            }
        }

        return formattedMessage;
    }

}
