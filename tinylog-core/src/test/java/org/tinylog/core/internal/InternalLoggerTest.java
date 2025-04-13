package org.tinylog.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.TaskExecutor;
import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.context.NopContextStorage;
import org.tinylog.test.util.SynchronousTaskExecutor;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

class InternalLoggerTest {

    /**
     * Logs a plain text message without any arguments.
     */
    @Test
    void logTextMessageWithoutArguments() {
        ListLoggingBackend backend = new ListLoggingBackend();
        TaskExecutor executor = new SynchronousTaskExecutor(backend);
        InternalLogger logger = new InternalLogger(executor);
        Configuration configuration = new Configuration(emptyMap(), logger);

        logger.log(Level.INFO, "Hello World!");

        assertThat(backend.entries).singleElement().satisfies(entry -> {
            assertThat(entry.getThread()).isEqualTo(Thread.currentThread());
            assertThat(entry.getContext()).isEmpty();
            assertThat(entry.getClassName()).isEqualTo(InternalLoggerTest.class.getName());
            assertThat(entry.getMethodName()).isEqualTo("logTextMessageWithoutArguments");
            assertThat(entry.getFileName()).isEqualTo("InternalLoggerTest.java");
            assertThat(entry.getLineNumber()).isEqualTo(33);
            assertThat(entry.getTag()).isEqualTo("tinylog");
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.INFO);
            assertThat(entry.getThrowable()).isNull();
            assertThat(entry.getFormattedMessage(configuration)).isEqualTo("Hello World!");
        });
    }

    /**
     * Logs a formatted text message with arguments.
     */
    @Test
    void logTextMessageWithArguments() {
        ListLoggingBackend backend = new ListLoggingBackend();
        TaskExecutor executor = new SynchronousTaskExecutor(backend);
        InternalLogger logger = new InternalLogger(executor);
        Configuration configuration = new Configuration(emptyMap(), logger);

        logger.log(Level.DEBUG, "{} + {} = {}", 1, 1, 2);

        assertThat(backend.entries).singleElement().satisfies(entry -> {
            assertThat(entry.getThread()).isEqualTo(Thread.currentThread());
            assertThat(entry.getContext()).isEmpty();
            assertThat(entry.getClassName()).isEqualTo(InternalLoggerTest.class.getName());
            assertThat(entry.getMethodName()).isEqualTo("logTextMessageWithArguments");
            assertThat(entry.getFileName()).isEqualTo("InternalLoggerTest.java");
            assertThat(entry.getLineNumber()).isEqualTo(59);
            assertThat(entry.getTag()).isEqualTo("tinylog");
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.DEBUG);
            assertThat(entry.getThrowable()).isNull();
            assertThat(entry.getFormattedMessage(configuration)).isEqualTo("1 + 1 = 2");
        });
    }

    /**
     * Logs an exception and a plain text message without any arguments.
     */
    @Test
    void logExceptionAndMessageWithoutArguments() {
        ListLoggingBackend backend = new ListLoggingBackend();
        TaskExecutor executor = new SynchronousTaskExecutor(backend);
        InternalLogger logger = new InternalLogger(executor);
        Configuration configuration = new Configuration(emptyMap(), logger);

        Exception exception = new Exception();
        logger.log(Level.ERROR, exception, "Oops!");

        assertThat(backend.entries).singleElement().satisfies(entry -> {
            assertThat(entry.getThread()).isEqualTo(Thread.currentThread());
            assertThat(entry.getContext()).isEmpty();
            assertThat(entry.getClassName()).isEqualTo(InternalLoggerTest.class.getName());
            assertThat(entry.getMethodName()).isEqualTo("logExceptionAndMessageWithoutArguments");
            assertThat(entry.getFileName()).isEqualTo("InternalLoggerTest.java");
            assertThat(entry.getLineNumber()).isEqualTo(86);
            assertThat(entry.getTag()).isEqualTo("tinylog");
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getThrowable()).isSameAs(exception);
            assertThat(entry.getFormattedMessage(configuration)).isEqualTo("Oops!");
        });
    }

    /**
     * Logs an exception and a formatted text message but with arguments.
     */
    @Test
    void logExceptionAndMessageWithArguments() {
        ListLoggingBackend backend = new ListLoggingBackend();
        TaskExecutor executor = new SynchronousTaskExecutor(backend);
        InternalLogger logger = new InternalLogger(executor);
        Configuration configuration = new Configuration(emptyMap(), logger);

        Exception exception = new Exception();
        logger.log(Level.ERROR, exception, "Failed to handle <{}> and <{}>", "foo", "bar");

        assertThat(backend.entries).singleElement().satisfies(entry -> {
            assertThat(entry.getThread()).isEqualTo(Thread.currentThread());
            assertThat(entry.getContext()).isEmpty();
            assertThat(entry.getClassName()).isEqualTo(InternalLoggerTest.class.getName());
            assertThat(entry.getMethodName()).isEqualTo("logExceptionAndMessageWithArguments");
            assertThat(entry.getFileName()).isEqualTo("InternalLoggerTest.java");
            assertThat(entry.getLineNumber()).isEqualTo(113);
            assertThat(entry.getTag()).isEqualTo("tinylog");
            assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
            assertThat(entry.getThrowable()).isSameAs(exception);
            assertThat(entry.getFormattedMessage(configuration)).isEqualTo("Failed to handle <foo> and <bar>");
        });
    }

    /**
     * Simple logging backend implementation that stores all log entries in an array list.
     */
    private static final class ListLoggingBackend implements LoggingBackend {

        private final List<LogEntry> entries;

        /** */
        private ListLoggingBackend() {
            this.entries = new ArrayList<>();
        }

        @Override
        public ContextStorage getContextStorage() {
            return new NopContextStorage();
        }

        @Override
        public LevelVisibility getLevelVisibilityByClass(String className) {
            return new LevelVisibility(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
        }

        @Override
        public LevelVisibility getLevelVisibilityByTag(String tag) {
            return new LevelVisibility(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
        }

        @Override
        public boolean isEnabled(Object location, String tag, Level level) {
            return true;
        }

        @Override
        public void output(LogEntry entry, boolean last) {
            entries.add(entry);
        }

        @Override
        public void close() {
            // Nothing to do
        }

    }

}
