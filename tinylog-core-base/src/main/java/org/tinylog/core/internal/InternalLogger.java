package org.tinylog.core.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.format.message.EnhancedMessageFormatter;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Static logger for issuing internal log entries.
 */
public final class InternalLogger {

    /**
     * The tag to use for internal tinylog log entries.
     */
    public static final String TAG = "tinylog";

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static State state = new State(null, null, null);

    /** */
    private InternalLogger() {
    }

    /**
     * Initializes the internal logger.
     *
     * @param framework Fully initialized framework for setting this logger up
     */
    public static void init(Framework framework) {
        RuntimeFlavor runtime = framework.getRuntime();
        LoggingBackend backend = framework.getLoggingBackend();
        MessageFormatter formatter = new EnhancedMessageFormatter(framework);

        try {
            lock.writeLock().lock();
            List<LogEntry> entries = state.entries;
            state = new State(runtime, backend, formatter);
            state.log(entries);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Resets the internal logger to an uninitialized state.
     */
    public static void reset() {
        try {
            lock.writeLock().lock();
            state = new State(null, null, null);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Issues a trace log entry.
     *
     * @param ex Exception or any other kind of throwable
     * @param message Human-readable text message
     */
    public static void trace(Throwable ex, String message) {
        try {
            lock.readLock().lock();
            OutputDetails outputDetails = state.visibility.getTrace();
            if (outputDetails != OutputDetails.DISABLED) {
                Object location = state.runtime == null ? null : state.runtime.getDirectCaller(outputDetails).get();
                state.log(location, Level.TRACE, ex, message, null);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Issues a trace log entry.
     *
     * @param ex Exception or any other kind of throwable
     * @param message Human-readable text message with placeholders
     * @param arguments Argument values for placeholders in the text message
     */
    public static void trace(Throwable ex, String message, Object... arguments) {
        try {
            lock.readLock().lock();
            OutputDetails outputDetails = state.visibility.getTrace();
            if (outputDetails != OutputDetails.DISABLED) {
                Object location = state.runtime == null ? null : state.runtime.getDirectCaller(outputDetails).get();
                state.log(location, Level.TRACE, ex, message, arguments);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Issues a debug log entry.
     *
     * @param ex Exception or any other kind of throwable
     * @param message Human-readable text message
     */
    public static void debug(Throwable ex, String message) {
        try {
            lock.readLock().lock();
            OutputDetails outputDetails = state.visibility.getDebug();
            if (outputDetails != OutputDetails.DISABLED) {
                Object location = state.runtime == null ? null : state.runtime.getDirectCaller(outputDetails).get();
                state.log(location, Level.DEBUG, ex, message, null);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Issues a debug log entry.
     *
     * @param ex Exception or any other kind of throwable
     * @param message Human-readable text message with placeholders
     * @param arguments Argument values for placeholders in the text message
     */
    public static void debug(Throwable ex, String message, Object... arguments) {
        try {
            lock.readLock().lock();
            OutputDetails outputDetails = state.visibility.getDebug();
            if (outputDetails != OutputDetails.DISABLED) {
                Object location = state.runtime == null ? null : state.runtime.getDirectCaller(outputDetails).get();
                state.log(location, Level.DEBUG, ex, message, arguments);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Issues an info log entry.
     *
     * @param ex Exception or any other kind of throwable
     * @param message Human-readable text message
     */
    public static void info(Throwable ex, String message) {
        try {
            lock.readLock().lock();
            OutputDetails outputDetails = state.visibility.getInfo();
            if (outputDetails != OutputDetails.DISABLED) {
                Object location = state.runtime == null ? null : state.runtime.getDirectCaller(outputDetails).get();
                state.log(location, Level.INFO, ex, message, null);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Issues an info log entry.
     *
     * @param ex Exception or any other kind of throwable
     * @param message Human-readable text message with placeholders
     * @param arguments Argument values for placeholders in the text message
     */
    public static void info(Throwable ex, String message, Object... arguments) {
        try {
            lock.readLock().lock();
            OutputDetails outputDetails = state.visibility.getInfo();
            if (outputDetails != OutputDetails.DISABLED) {
                Object location = state.runtime == null ? null : state.runtime.getDirectCaller(outputDetails).get();
                state.log(location, Level.INFO, ex, message, arguments);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Issues a warn log entry.
     *
     * @param ex Exception or any other kind of throwable
     * @param message Human-readable text message
     */
    public static void warn(Throwable ex, String message) {
        try {
            lock.readLock().lock();
            OutputDetails outputDetails = state.visibility.getWarn();
            if (outputDetails != OutputDetails.DISABLED) {
                Object location = state.runtime == null ? null : state.runtime.getDirectCaller(outputDetails).get();
                state.log(location, Level.WARN, ex, message, null);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Issues a warn log entry.
     *
     * @param ex Exception or any other kind of throwable
     * @param message Human-readable text message with placeholders
     * @param arguments Argument values for placeholders in the text message
     */
    public static void warn(Throwable ex, String message, Object... arguments) {
        try {
            lock.readLock().lock();
            OutputDetails outputDetails = state.visibility.getWarn();
            if (outputDetails != OutputDetails.DISABLED) {
                Object location = state.runtime == null ? null : state.runtime.getDirectCaller(outputDetails).get();
                state.log(location, Level.WARN, ex, message, arguments);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Issues an error log entry.
     *
     * @param ex Exception or any other kind of throwable
     * @param message Human-readable text message
     */
    public static void error(Throwable ex, String message) {
        try {
            lock.readLock().lock();
            OutputDetails outputDetails = state.visibility.getError();
            if (outputDetails != OutputDetails.DISABLED) {
                Object location = state.runtime == null ? null : state.runtime.getDirectCaller(outputDetails).get();
                state.log(location, Level.ERROR, ex, message, null);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Issues an error log entry.
     *
     * @param ex Exception or any other kind of throwable
     * @param message Human-readable text message with placeholders
     * @param arguments Argument values for placeholders in the text message
     */
    public static void error(Throwable ex, String message, Object... arguments) {
        try {
            lock.readLock().lock();
            OutputDetails outputDetails = state.visibility.getError();
            if (outputDetails != OutputDetails.DISABLED) {
                Object location = state.runtime == null ? null : state.runtime.getDirectCaller(outputDetails).get();
                state.log(location, Level.ERROR, ex, message, arguments);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Internal logger state with {@link RuntimeFlavor}, {@link LoggingBackend}, and {@link MessageFormatter}.
     */
    private static final class State {

        private final List<LogEntry> entries;
        private final RuntimeFlavor runtime;
        private final LoggingBackend backend;
        private final MessageFormatter formatter;
        private final LevelVisibility visibility;

        /**
         * @param runtime Runtime flavor for extraction of stack trace location
         * @param backend Logging backend for output log entries
         * @param formatter Message formatter for replacing placeholders by real values
         */
        private State(RuntimeFlavor runtime, LoggingBackend backend, MessageFormatter formatter) {
            this.entries = new ArrayList<>();
            this.runtime = runtime;
            this.backend = backend;
            this.formatter = formatter;

            if (backend == null) {
                visibility = new LevelVisibility(
                    OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
                    OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
                    OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
                    OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
                    OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION
                );
            } else {
                visibility = backend.getLevelVisibilityByTag(TAG);
            }
        }

        /**
         * Issues a log entry at the defined severity level.
         *
         * @param location Location information of caller
         * @param level Severity level
         * @param throwable Exception or any other kind of throwable
         * @param message Human-readable text message
         * @param arguments Argument values for placeholders in the text message
         */
        private void log(Object location, Level level, Throwable throwable, String message, Object[] arguments) {
            if (backend == null) {
                entries.add(new LogEntry(level, throwable, message, arguments));
            } else {
                backend.log(location, TAG, level, throwable, message, arguments, formatter);
            }
        }

        /**
         * Issues a list of stored log entries via the actual logging backend.
         *
         * @param entries The log entries to issue
         */
        public void log(List<LogEntry> entries) {
            for (LogEntry entry : entries) {
                OutputDetails outputDetails = visibility.get(entry.getLevel());
                if (outputDetails != OutputDetails.DISABLED) {
                    Supplier<?> location = runtime.getDirectCaller(outputDetails);
                    backend.log(location.get(), TAG, entry.getLevel(), entry.getThrowable(), entry.getMessage(),
                        entry.getArguments(), formatter);
                }
            }
        }

    }

}
