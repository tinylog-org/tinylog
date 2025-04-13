package org.tinylog.core.internal;

import java.util.Collections;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.Task;
import org.tinylog.core.TaskExecutor;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.format.message.SimpleMessageFormatter;

/**
 * Internal logger for issuing log entries within tinylog.
 */
public class InternalLogger {

    /**
     * The category tag to use for internal tinylog log entries.
     */
    public static final String TAG = "tinylog";

    private static final int STACK_TRACE_DEPTH = 2;

    private final MessageFormatter formatter;
    private final TaskExecutor executor;

    /**
     * @param executor The task executor for enqueuing log entries
     */
    public InternalLogger(TaskExecutor executor) {
        this.formatter = new SimpleMessageFormatter();
        this.executor = executor;
    }

    /**
     * Issues a new log entry.
     *
     * @param level The severity level of the log entry to issue
     * @param message Human-readable text message with placeholders
     * @param arguments Argument values for placeholders in the text message
     */
    public void log(Level level, String message, Object... arguments) {
        LogEntry entry = createLogEntry(level, null, message, arguments);
        Task task = (backend, last) -> backend.output(entry, last);
        executor.enqueue(task);
    }

    /**
     * Issues a new log entry.
     *
     * @param level The severity level of the log entry to issue
     * @param throwable The throwable to log
     * @param message Human-readable text message with placeholders
     * @param arguments Argument values for placeholders in the text message
     */
    public void log(Level level, Throwable throwable, String message, Object... arguments) {
        LogEntry entry = createLogEntry(level, throwable, message, arguments);
        Task task = (backend, last) -> backend.output(entry, last);
        executor.enqueue(task);
    }

    /**
     * Creates a new log entry.
     *
     * @param level The severity level of the log entry to issue
     * @param throwable The throwable to log
     * @param message Human-readable text message with placeholders
     * @param arguments Argument values for placeholders in the text message
     * @return The newly created log entry
     */
    private LogEntry createLogEntry(Level level, Throwable throwable, String message, Object[] arguments) {
        StackTraceElement location = new Throwable().getStackTrace()[STACK_TRACE_DEPTH];
        return new LogEntry(
            Thread.currentThread(),
            Collections.emptyMap(),
            location,
            TAG,
            level,
            throwable,
            formatter,
            message,
            arguments
        );
    }

}
