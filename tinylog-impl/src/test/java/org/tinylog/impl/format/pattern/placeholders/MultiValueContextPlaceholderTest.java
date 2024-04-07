package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for MultiValueContextPlaceholder.
 */
public class MultiValueContextPlaceholderTest {

    /**
     * Verifies that the log entry value {@link LogEntryValue#CONTEXT} is defined as required by the context
     * placeholder.
     */
    @Test
    void requiredLogEntryValues() {
        MultiValueContextPlaceholder placeholder = new MultiValueContextPlaceholder();
        assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CONTEXT);
    }

    /**
     * Verifies that all thread context keys and values of a log entry will be resolved.
     */
    @Test
    void resolveWithContextValues() {
        MultiValueContextPlaceholder placeholder = new MultiValueContextPlaceholder();
        LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").context("baz", "quk").create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("baz=quk, foo=bar");
    }

    /**
     * Verifies that empty string will be resolved, if a thread context value is not present.
     */
    @Test
    void resolveWithoutContextValues() {
        MultiValueContextPlaceholder placeholder = new MultiValueContextPlaceholder();
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("");
    }

    /**
     * Verifies that all thread context keys and values of a log entry will be output.
     */
    @Test
    void renderWithContextValues() {
        MultiValueContextPlaceholder placeholder = new MultiValueContextPlaceholder();
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").context("baz", "quk").create();
        assertThat(renderer.render(logEntry)).isEqualTo("baz=quk, foo=bar");
    }

    /**
     * Verifies that an empty string will be output, if no thread context values are
     * present.
     */
    @Test
    void renderWithoutContextValues() {
        MultiValueContextPlaceholder placeholder = new MultiValueContextPlaceholder();
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEmpty();
    }
}
