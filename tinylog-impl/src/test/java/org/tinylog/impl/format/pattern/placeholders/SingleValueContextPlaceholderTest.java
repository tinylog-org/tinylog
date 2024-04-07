package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class SingleValueContextPlaceholderTest {

    /**
     * Verifies that the log entry value {@link LogEntryValue#CONTEXT} is defined as required by the context
     * placeholder.
     */
    @Test
    void requiredLogEntryValues() {
        SingleValueContextPlaceholder placeholder = new SingleValueContextPlaceholder("foo");
        assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CONTEXT);
    }

    /**
     * Verifies that a thread context value of a log entry will be resolved, if present.
     */
    @Test
    void resolveWithContextValue() {
        SingleValueContextPlaceholder placeholder = new SingleValueContextPlaceholder("foo");
        LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("bar");
    }

    /**
     * Verifies that {@code null} will be resolved, if a thread context value is not present.
     */
    @Test
    void resolveWithoutContextValue() {
        SingleValueContextPlaceholder placeholder = new SingleValueContextPlaceholder("foo");
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that a thread context value of a log entry will be output, if present.
     */
    @Test
    void renderWithContextValue() {
        SingleValueContextPlaceholder placeholder = new SingleValueContextPlaceholder("foo");
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").create();
        assertThat(renderer.render(logEntry)).isEqualTo("bar");
    }

    /**
     * Verifies that an empty string will be output, if a thread context value is not present.
     */
    @Test
    void renderWithoutContextValue() {
        SingleValueContextPlaceholder placeholder = new SingleValueContextPlaceholder("foo");
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEmpty();
    }
}
