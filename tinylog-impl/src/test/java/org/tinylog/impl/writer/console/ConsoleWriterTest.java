package org.tinylog.impl.writer.console;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdErr;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.impl.format.placeholder.ClassPlaceholder;
import org.tinylog.impl.format.placeholder.MessageOnlyPlaceholder;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class ConsoleWriterTest {

    @Inject
    private Configuration configuration;

    /**
     * Verifies that the console writer returns the output details of the passed placeholder.
     */
    @Test
    void provideOutputDetails() {
        try (ConsoleWriter writer = new ConsoleWriter(new ClassPlaceholder(), Level.WARN)) {
            assertThat(writer.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
        }
    }

    /**
     * Verifies that all log entries are output to the correct stream according to the defined severity level threshold.
     *
     * @param out The captured output of the standard output stream
     * @param err The captured output of the standard error stream
     */
    @Test
    @StdIo
    void outputLogEntry(StdOut out, StdErr err) throws Exception {
        try (ConsoleWriter writer = new ConsoleWriter(new MessageOnlyPlaceholder(configuration), Level.WARN)) {
            writer.log(new LogEntryBuilder().severityLevel(Level.TRACE).message("Hello Trace!").create());
            writer.log(new LogEntryBuilder().severityLevel(Level.DEBUG).message("Hello Debug!").create());
            writer.log(new LogEntryBuilder().severityLevel(Level.INFO).message("Hello Info!").create());
            writer.log(new LogEntryBuilder().severityLevel(Level.WARN).message("Hello Warn!").create());
            writer.log(new LogEntryBuilder().severityLevel(Level.ERROR).message("Hello Error!").create());

            assertThat(out.capturedString()).isEqualTo("Hello Trace!Hello Debug!Hello Info!");
            assertThat(err.capturedString()).isEqualTo("Hello Warn!Hello Error!");
        }
    }

    /**
     * Verifies that flushing the console writer won't throw any exception.
     */
    @Test
    void flushWriter() {
        try (ConsoleWriter writer = new ConsoleWriter(new MessageOnlyPlaceholder(configuration), Level.WARN)) {
            writer.flush();
        }
    }

}
