package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class LinePlaceholderTest {

    /**
     * Verifies that the line placeholder enables output and requires the full location information.
     */
    @Test
    void provideOutputDetails() {
        LinePlaceholder placeholder = new LinePlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
    }

    /**
     * Verifies that the line number of the source file will be resolved, if set.
     */
    @Test
    void resolveWithSourceLineName() {
        LogEntry logEntry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", "MyClass.java", 100)
            .create();

        LinePlaceholder placeholder = new LinePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.INTEGER);
        assertThat(placeholder.getValue(logEntry)).isEqualTo(100);
    }

    /**
     * Verifies that {@code null} will be resolved, if the line number of the source file is not set.
     */
    @Test
    void resolveWithoutSourceLineName() {
        LogEntry logEntry = new LogEntryBuilder().create();
        LinePlaceholder placeholder = new LinePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.INTEGER);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the line number of the source file will be output, if set.
     */
    @Test
    void renderWithSourceLineName() {
        LogEntry logEntry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", "MyClass.java", 100)
            .create();

        FormatOutputRenderer renderer = new FormatOutputRenderer(new LinePlaceholder());
        assertThat(renderer.render(logEntry)).isEqualTo("100");
    }

    /**
     * Verifies that "?" will be output, if the line number of the source file is not set.
     */
    @Test
    void renderWithoutSourceLineName() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new LinePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("?");
    }

}
