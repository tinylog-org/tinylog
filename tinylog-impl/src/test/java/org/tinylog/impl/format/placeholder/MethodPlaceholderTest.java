package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class MethodPlaceholderTest {

    /**
     * Verifies that the method placeholder enables output and requires the full location information.
     */
    @Test
    void provideOutputDetails() {
        MethodPlaceholder placeholder = new MethodPlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
    }

    /**
     * Verifies that the source method name of a log entry will be resolved, if set.
     */
    @Test
    void resolveWithSourceMethodName() {
        LogEntry logEntry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", null, -1)
            .create();

        MethodPlaceholder placeholder = new MethodPlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("foo");
    }

    /**
     * Verifies that {@code null} will be resolved, if the source method name is not set.
     */
    @Test
    void resolveWithoutSourceMethodName() {
        LogEntry logEntry = new LogEntryBuilder().create();
        MethodPlaceholder placeholder = new MethodPlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the source method name of a log entry will be output, if set.
     */
    @Test
    void renderWithSourceMethodName() {
        LogEntry logEntry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", null, -1)
            .create();

        FormatOutputRenderer renderer = new FormatOutputRenderer(new MethodPlaceholder());
        assertThat(renderer.render(logEntry)).isEqualTo("foo");
    }

    /**
     * Verifies that {@code <method unknown>} will be output, if the source method name is not set.
     */
    @Test
    void renderWithoutSourceMethodName() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new MethodPlaceholder());
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("<method unknown>");
    }

}
