package org.tinylog.core.internal;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.core.test.log.LogEntry;

import static org.assertj.core.api.Assertions.assertThat;

class InternalLoggerTest {

    @Inject
    private Framework framework;

    @Inject
    private Log log;

    /**
     * Verifies that a trace log entry with a plain text message can be issued.
     */
    @CaptureLogEntries(level = Level.TRACE)
    @Test
    void traceMessage() {
        InternalLogger.trace(null, "Hello World!");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.TRACE, null, "Hello World!"));
    }

    /**
     * Verifies that a trace log entry with a placeholder message and arguments can be issued.
     */
    @CaptureLogEntries(level = Level.TRACE)
    @Test
    void traceMessageWithArguments() {
        InternalLogger.trace(null, "Hello {}!", "Alice");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.TRACE, null, "Hello Alice!"));
    }

    /**
     * Verifies that a trace log entry with an exception and a custom text message can be issued.
     */
    @CaptureLogEntries(level = Level.TRACE)
    @Test
    void traceExceptionAndMessage() {
        Exception exception = new Exception();
        InternalLogger.trace(exception, "Oops!");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.TRACE, exception, "Oops!"));
    }

    /**
     * Verifies that no trace log entries wil be issued, if the trace severity level is disabled.
     */
    @CaptureLogEntries(level = Level.DEBUG)
    @Test
    void traceDisabled() {
        InternalLogger.trace(null, "Hello World!");
        InternalLogger.trace(null, "Hello {}!", "Alice");

        assertThat(log.consume()).isEmpty();
    }

    /**
     * Verifies that a debug log entry with a plain text message can be issued.
     */
    @CaptureLogEntries(level = Level.DEBUG)
    @Test
    void debugMessage() {
        InternalLogger.debug(null, "Hello World!");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.DEBUG, null, "Hello World!"));
    }

    /**
     * Verifies that a debug log entry with a placeholder message and arguments can be issued.
     */
    @CaptureLogEntries(level = Level.DEBUG)
    @Test
    void debugMessageWithArguments() {
        InternalLogger.debug(null, "Hello {}!", "Alice");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.DEBUG, null, "Hello Alice!"));
    }

    /**
     * Verifies that a debug log entry with an exception and a custom text message can be issued.
     */
    @CaptureLogEntries(level = Level.DEBUG)
    @Test
    void debugExceptionAndMessage() {
        Exception exception = new Exception();
        InternalLogger.debug(exception, "Oops!");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.DEBUG, exception, "Oops!"));
    }

    /**
     * Verifies that no debug log entries wil be issued, if the debug debug level is disabled.
     */
    @CaptureLogEntries(level = Level.INFO)
    @Test
    void debugDisabled() {
        InternalLogger.debug(null, "Hello World!");
        InternalLogger.debug(null, "Hello {}!", "Alice");

        assertThat(log.consume()).isEmpty();
    }

    /**
     * Verifies that an info log entry with a plain text message can be issued.
     */
    @CaptureLogEntries(level = Level.INFO)
    @Test
    void infoMessage() {
        InternalLogger.info(null, "Hello World!");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.INFO, null, "Hello World!"));
    }

    /**
     * Verifies that an info log entry with a placeholder message and arguments can be issued.
     */
    @CaptureLogEntries(level = Level.INFO)
    @Test
    void infoMessageWithArguments() {
        InternalLogger.info(null, "Hello {}!", "Alice");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.INFO, null, "Hello Alice!"));
    }

    /**
     * Verifies that an info log entry with an exception and a custom text message can be issued.
     */
    @CaptureLogEntries(level = Level.INFO)
    @Test
    void infoExceptionAndMessage() {
        Exception exception = new Exception();
        InternalLogger.info(exception, "Oops!");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.INFO, exception, "Oops!"));
    }

    /**
     * Verifies that no info log entries wil be issued, if the info severity level is disabled.
     */
    @CaptureLogEntries(level = Level.WARN)
    @Test
    void infoDisabled() {
        InternalLogger.info(null, "Hello World!");
        InternalLogger.info(null, "Hello {}!", "Alice");

        assertThat(log.consume()).isEmpty();
    }

    /**
     * Verifies that a warn log entry with a plain text message can be issued.
     */
    @CaptureLogEntries(level = Level.WARN)
    @Test
    void warnMessage() {
        InternalLogger.warn(null, "Hello World!");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.WARN, null, "Hello World!"));
    }

    /**
     * Verifies that a warn log entry with a placeholder message and arguments can be issued.
     */
    @CaptureLogEntries(level = Level.WARN)
    @Test
    void warnMessageWithArguments() {
        InternalLogger.warn(null, "Hello {}!", "Alice");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.WARN, null, "Hello Alice!"));
    }

    /**
     * Verifies that a warn log entry with an exception and a custom text message can be issued.
     */
    @CaptureLogEntries(level = Level.WARN)
    @Test
    void warnExceptionAndMessage() {
        Exception exception = new Exception();
        InternalLogger.warn(exception, "Oops!");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.WARN, exception, "Oops!"));
    }

    /**
     * Verifies that no warn log entries wil be issued, if the warn severity level is disabled.
     */
    @CaptureLogEntries(level = Level.ERROR)
    @Test
    void warnDisabled() {
        InternalLogger.warn(null, "Hello World!");
        InternalLogger.warn(null, "Hello {}!", "Alice");

        assertThat(log.consume()).isEmpty();
    }

    /**
     * Verifies that an error log entry with a plain text message can be issued.
     */
    @CaptureLogEntries(level = Level.ERROR)
    @Test
    void errorMessage() {
        InternalLogger.error(null, "Hello World!");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.ERROR, null, "Hello World!"));
    }

    /**
     * Verifies that an error log entry with a placeholder message and arguments can be issued.
     */
    @CaptureLogEntries(level = Level.ERROR)
    @Test
    void errorMessageWithArguments() {
        InternalLogger.error(null, "Hello {}!", "Alice");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.ERROR, null, "Hello Alice!"));
    }

    /**
     * Verifies that an error log entry with an exception and a custom text message can be issued.
     */
    @CaptureLogEntries(level = Level.ERROR)
    @Test
    void errorExceptionAndMessage() {
        Exception exception = new Exception();
        InternalLogger.error(exception, "Oops!");
        assertThat(log.consume()).containsExactly(createLogEntry(Level.ERROR, exception, "Oops!"));
    }

    /**
     * Verifies that no error log entries wil be issued, if the error severity level is disabled.
     */
    @CaptureLogEntries(level = Level.OFF)
    @Test
    void errorDisabled() {
        InternalLogger.error(null, "Hello World!");
        InternalLogger.error(null, "Hello {}!", "Alice");

        assertThat(log.consume()).isEmpty();
    }

    /**
     * Verifies that log entries can be issued belated when the internal logger will be initialized.
     */
    @CaptureLogEntries(level = Level.INFO, autostart = false)
    @Test
    void delayedIssuing() {
        InternalLogger.info(null, "Hello World!");
        assertThat(log.consume()).isEmpty();

        framework.startUp();
        assertThat(log.consume()).containsExactly(
            new LogEntry(InternalLogger.class.getName(), "tinylog", Level.INFO, null, "Hello World!")
        );
    }

    /**
     * Verifies that log entries will be discarded, if the internal logger is initialized with a less severe level.
     */
    @CaptureLogEntries(level = Level.WARN, autostart = false)
    @Test
    void delayedDiscarding() {
        InternalLogger.info(null, "Hello World!");
        assertThat(log.consume()).isEmpty();

        framework.startUp();
        assertThat(log.consume()).isEmpty();
    }

    /**
     * Creates a new log entry.
     *
     * @param level Severity level
     * @param exception Exception or any other kind of throwable
     * @param message Text message
     * @return Created log entry
     */
    private static LogEntry createLogEntry(Level level, Throwable exception, String message) {
        return new LogEntry(InternalLoggerTest.class.getName(), "tinylog", level, exception, message);
    }

}
