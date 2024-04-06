package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ContextPlaceholderTest {

    /**
     * Verifies that the log entry value {@link LogEntryValue#CONTEXT} is defined as required by the context
     * placeholder.
     */
    @Test
    void requiredLogEntryValues() {
        ContextPlaceholder placeholder = new ContextPlaceholder("foo");
        assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CONTEXT);
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
     * Verifies that all thread context keys and values of a log entry will be resolved
     * when no key is specified in the context placeholder.
     */
    @Test
    void resolveWithContextValuesAndNoKeySpecified() {
        ContextPlaceholder placeholder = new ContextPlaceholder();
        LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").context("baz", "quk").create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("baz=quk, foo=bar");
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
     * Verifies that {@code null} will be resolved, if a thread context value is not present,
     * and no key is specified in the context placeholder.
     */
    @Test
    void resolveWithoutContextValueAndKeySpecified() {
        ContextPlaceholder placeholder = new ContextPlaceholder();
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
     * Verifies that all thread context keys and values of a log entry will be output
     * when no key is specified in the context placeholder.
     */
    @Test
    void renderWithContextValueAndNoKeySpecified() {
        ContextPlaceholder placeholder = new ContextPlaceholder();
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").context("baz", "quk").create();
        assertThat(renderer.render(logEntry)).isEqualTo("baz=quk, foo=bar");
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

    /**
     * Verifies that an empty string will be output, if no thread context values are
     * present, and no key is specified in the context placeholder.
     */
    @Test
    void renderWithoutContextValueAndNoKeySpecified() {
        ContextPlaceholder placeholder = new ContextPlaceholder();
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEmpty();
    }
}
