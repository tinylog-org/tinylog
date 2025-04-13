package org.tinylog.test.util;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Builder with fluent API for creating an instance of {@link LogEntry}.
 */
public class LogEntryBuilder {

    private Instant timestamp;
    private Duration uptime;
    private Thread thread;
    private final Map<String, String> context;
    private Object locationInfo;
    private String tag;
    private Level severityLevel;
    private MessageFormatter formatter;
    private String message;
    private Object[] arguments;
    private Throwable throwable;

    /** */
    public LogEntryBuilder() {
        context = new HashMap<>();
    }

    /**
     * Sets the date and time when this log entry was issued.
     *
     * @param timestamp The date and time of issue
     * @return The same log entry builder instance
     *
     * @see LogEntry#getTimestamp()
     */
    public LogEntryBuilder timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Sets the passed time since application start when this log entry was issued.
     *
     * @param uptime The passed time since application start
     * @return The same log entry builder instance
     *
     * @see LogEntry#getUptime(RuntimeFlavor)
     */
    public LogEntryBuilder uptime(Duration uptime) {
        this.uptime = uptime;
        return this;
    }

    /**
     * Sets the thread, in which this log entry has been issued.
     *
     * @param thread The source thread of issue
     * @return The same log entry builder instance
     *
     * @see LogEntry#getThread()
     */
    public LogEntryBuilder thread(Thread thread) {
        this.thread = thread;
        return this;
    }

    /**
     * Adds a thread context entry with key and value.
     *
     * <p>
     *     If there is already a thread context entry with the same key, it will be overwritten by the new context
     *     entry.
     * </p>
     *
     * @param key The key for the new thread context entry
     * @param value The value for the new thread context entry
     * @return The same log entry builder instance
     *
     * @see LogEntry#getContext()
     */
    public LogEntryBuilder context(String key, String value) {
        context.put(key, value);
        return this;
    }

    /**
     * Sets the fully-qualified name of the class, in which this log entry has been issued.
     *
     * @param className The fully-qualified source class name
     * @return The same log entry builder instance
     *
     * @see LogEntry#getClassName()
     */
    public LogEntryBuilder className(String className) {
        this.locationInfo = className;
        return this;
    }

    /**
     * Sets the source class, in which this log entry has been issued.
     *
     * @param classObject The source class object
     * @return The same log entry builder instance
     *
     * @see LogEntry#getClassName()
     */
    public LogEntryBuilder className(Class<?> classObject) {
        this.locationInfo = classObject;
        return this;
    }

    /**
     * Sets the stack trace element, in which this log entry has been issued.
     *
     * @param className The fully-qualified source class name
     * @param methodName The source method name
     * @param fileName The source file name
     * @param lineNumber The line number in source file name
     * @return The same log entry builder instance
     *
     * @see LogEntry#getClassName()
     * @see LogEntry#getMethodName()
     * @see LogEntry#getFileName()
     * @see LogEntry#getLineNumber()
     */
    public LogEntryBuilder stackTraceElement(String className, String methodName, String fileName, int lineNumber) {
        return stackTraceElement(new StackTraceElement(className, methodName, fileName, lineNumber));
    }

    /**
     * Sets the stack trace element, in which this log entry has been issued.
     *
     * @param element The stack trace element with source location information
     * @return The same log entry builder instance
     *
     * @see LogEntry#getClassName()
     * @see LogEntry#getMethodName()
     * @see LogEntry#getFileName()
     * @see LogEntry#getLineNumber()
     */
    public LogEntryBuilder stackTraceElement(StackTraceElement element) {
        this.locationInfo = element;
        return this;
    }

    /**
     * Sets the assigned tag.
     *
     * @param tag The assigned tag
     * @return The same log entry builder instance
     *
     * @see LogEntry#getTag()
     */
    public LogEntryBuilder tag(String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * Sets the {@link Level severity level} of this log entry.
     *
     * @param severityLevel The severity level
     * @return The same log entry builder instance
     *
     * @see LogEntry#getSeverityLevel()
     */
    public LogEntryBuilder severityLevel(Level severityLevel) {
        this.severityLevel = severityLevel;
        return this;
    }

    /**
     * Sets the formatter for formatting text messages with placeholders.
     *
     * @param formatter The formatter for text messages with placeholders
     * @return The same log entry builder instance
     *
     * @see LogEntry#getFormattedMessage(Configuration)
     */
    public LogEntryBuilder formatter(MessageFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    /**
     * Sets the human-readable text message.
     *
     * @param message The human-readable text message
     * @param arguments The optional arguments for placeholders
     * @return The same log entry builder instance
     *
     * @see LogEntry#getFormattedMessage(Configuration)
     */
    public LogEntryBuilder message(String message, Object... arguments) {
        this.message = message;
        this.arguments = arguments;
        return this;
    }

    /**
     * Sets the throwable.
     *
     * @param throwable The exception or any other kind of throwable
     * @return The same log entry builder instance
     *
     * @see LogEntry#getThrowable()
     */
    public LogEntryBuilder throwable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    /**
     * Creates a new {@link LogEntry} that is prefilled with all properties defined in this log builder instance.
     *
     * @return A newly created {@link LogEntry} instance
     */
    public LogEntry create() {
        return new LogEntry(
            thread,
            context,
            locationInfo,
            tag,
            severityLevel,
            throwable,
            formatter,
            message,
            arguments
        ) {
            @Override
            public Instant getTimestamp() {
                return timestamp == null ? super.getTimestamp() : timestamp;
            }

            @Override
            public Duration getUptime(RuntimeFlavor runtime) {
                return uptime == null ? super.getUptime(runtime) : uptime;
            }
        };
    }

}
