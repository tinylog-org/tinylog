package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ContextPlaceholderTest {

    /**
     * Verifies that the context placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        ContextPlaceholder placeholder = new ContextPlaceholder("foo");
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that a thread context value of a log entry will be resolved, if present.
     */
    @Test
    void resolveWithContextValue() {
        ContextPlaceholder placeholder = new ContextPlaceholder("foo");
        LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("bar");
    }

    /**
     * Verifies that {@code null} will be resolved, if a thread context value is not present.
     */
    @Test
    void resolveWithoutContextValue() {
        ContextPlaceholder placeholder = new ContextPlaceholder("foo");
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that a thread context value of a log entry will be output, if present.
     */
    @Test
    void renderWithContextValue() {
        ContextPlaceholder placeholder = new ContextPlaceholder("foo");
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").create();
        assertThat(renderer.render(logEntry)).isEqualTo("bar");
    }

    /**
     * Verifies that an empty string will be output, if a thread context value is not present.
     */
    @Test
    void renderWithoutContextValue() {
        ContextPlaceholder placeholder = new ContextPlaceholder("foo");
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEmpty();
    }

}
