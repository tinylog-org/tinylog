package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class MultiContextPlaceholderTest {

    /**
     * Verifies that the context placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        MultiContextPlaceholder placeholder = new MultiContextPlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that {@code null} will be resolved, if there are no thread context values.
     */
    @Test
    void resolveWithoutContextValue() {
        MultiContextPlaceholder placeholder = new MultiContextPlaceholder();
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that a single thread context value can be resolved.
     */
    @Test
    void resolveWithSingleContextValue() {
        MultiContextPlaceholder placeholder = new MultiContextPlaceholder();
        LogEntry logEntry = new LogEntryBuilder().context("foo", "a").create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("foo=a");
    }

    /**
     * Verifies that multiple thread context value can be resolved.
     */
    @Test
    void resolveWithMultipleContextValue() {
        MultiContextPlaceholder placeholder = new MultiContextPlaceholder();
        LogEntry logEntry = new LogEntryBuilder().context("foo", "a").context("bar", "b").create();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("bar=b, foo=a");
    }

    /**
     * Verifies that an empty string will be output, if there are no thread context values.
     */
    @Test
    void renderWithoutContextValue() {
        MultiContextPlaceholder placeholder = new MultiContextPlaceholder();
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEmpty();
    }

    /**
     * Verifies that a single thread context value can be resolved.
     */
    @Test
    void renderWithSingleContextValue() {
        MultiContextPlaceholder placeholder = new MultiContextPlaceholder();
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().context("foo", "a").create();
        assertThat(renderer.render(logEntry)).isEqualTo("foo=a");
    }

    /**
     * Verifies that multiple thread context value can be resolved.
     */
    @Test
    void renderWithMultipleContextValue() {
        MultiContextPlaceholder placeholder = new MultiContextPlaceholder();
        FormatOutputRenderer renderer = new FormatOutputRenderer(placeholder);
        LogEntry logEntry = new LogEntryBuilder().context("foo", "a").context("bar", "b").create();
        assertThat(renderer.render(logEntry)).isEqualTo("bar=b, foo=a");
    }

}
