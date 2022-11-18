package org.tinylog.impl.test;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;

/**
 * Builder with fluent API for creating an instance of {@link LogEntry}.
 */
public class LogEntryBuilder {

    private Instant timestamp;
    private Duration uptime;
    private Thread thread;
    private final Map<String, String> context;
    private String className;
    private String methodName;
    private String fileName;
    private int lineNumber;
    private String tag;
    private Level severityLevel;
    private String message;
    private Throwable exception;

    /** */
    public LogEntryBuilder() {
        context = new HashMap<>();
        lineNumber = -1;
    }

    /**
     * Sets the date and time when this log entry was issued.
     *
     * @param timestamp Date and time of issue
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
     * @param uptime Passed time since application start
     * @return The same log entry builder instance
     *
     * @see LogEntry#getUptime()
     */
    public LogEntryBuilder uptime(Duration uptime) {
        this.uptime = uptime;
        return this;
    }

    /**
     * Sets the thread, in which this log entry has been issued.
     * 
     * @param thread Source thread of issue
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
     * @param key Key for the new thread context entry
     * @param value Value for the new thread context entry
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
     * @param className Fully-qualified source class name
     * @return The same log entry builder instance
     * 
     * @see LogEntry#getClassName()
     */
    public LogEntryBuilder className(String className) {
        this.className = className;
        return this;
    }

    /**
     * Sets the name of the method, in which this log entry has been issued.
     * 
     * @param methodName Source method name
     * @return The same log entry builder instance
     * 
     * @see LogEntry#getMethodName()
     */
    public LogEntryBuilder methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    /**
     * Sets the name of the file, in which this log entry has been issued.
     * 
     * @param fileName Source file name
     * @return The same log entry builder instance
     * 
     * @see LogEntry#getFileName()
     */
    public LogEntryBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * Sets the line number in the source file, in which this log entry has been issued.
     * 
     * @param lineNumber Line number in source file
     * @return The same log entry builder instance
     * 
     * @see LogEntry#getLineNumber()
     */
    public LogEntryBuilder lineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    /**
     * Sets the assigned tag.
     * 
     * @param tag Assigned tag
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
     * @param severityLevel Severity level
     * @return The same log entry builder instance
     * 
     * @see LogEntry#getSeverityLevel()
     */
    public LogEntryBuilder severityLevel(Level severityLevel) {
        this.severityLevel = severityLevel;
        return this;
    }

    /**
     * Sets the human-readable text message.
     * 
     * @param message Human-readable text message
     * @return The same log entry builder instance
     * 
     * @see LogEntry#getMessage()
     */
    public LogEntryBuilder message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sets the throwable.
     * 
     * @param exception Exception or any other kind of throwable
     * @return The same log entry builder instance
     * 
     * @see LogEntry#getException()
     */
    public LogEntryBuilder exception(Throwable exception) {
        this.exception = exception;
        return this;
    }

    /**
     * Creates a new {@link LogEntry} that is prefilled with all properties defined in this log builder instance.
     *
     * @return Newly created {@link LogEntry} instance
     */
    public LogEntry create() {
        return new LogEntry(timestamp, uptime, thread, context, className, methodName, fileName, lineNumber, tag,
            severityLevel, message, exception);
    }

}
