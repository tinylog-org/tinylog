package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Configuration;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.junit.log.Tinylog;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@Tinylog
class MessageOnlyPlaceholderTest {

    @Inject
    private Configuration configuration;

    /**
     * Verifies that the message only placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        MessageOnlyPlaceholder placeholder = new MessageOnlyPlaceholder(configuration);
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that the log message of a log entry will be resolved, if set.
     */
    @Test
    void resolveWithMessage() {
        LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
        MessageOnlyPlaceholder placeholder = new MessageOnlyPlaceholder(configuration);
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("Hello World!");
    }

    /**
     * Verifies that {@code null} will be resolved, if the log message is not set.
     */
    @Test
    void resolveWithoutMessage() {
        LogEntry logEntry = new LogEntryBuilder().create();
        MessageOnlyPlaceholder placeholder = new MessageOnlyPlaceholder(configuration);
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the log message will be output, if set.
     */
    @Test
    void renderWithMessage() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new MessageOnlyPlaceholder(configuration));
        LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
        assertThat(renderer.render(logEntry)).isEqualTo("Hello World!");
    }

    /**
     * Verifies that nothing will be output, if the log message is not set.
     */
    @Test
    void renderWithoutMessage() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new MessageOnlyPlaceholder(configuration));
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("");
    }

}
