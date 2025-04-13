package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class FilePlaceholderTest {

    /**
     * Verifies that the file placeholder enables output and requires the full location information.
     */
    @Test
    void provideOutputDetails() {
        FilePlaceholder placeholder = new FilePlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO);
    }

    /**
     * Verifies that the source file name of a log entry will be resolved, if set.
     */
    @Test
    void resolveWithSourceFileName() {
        LogEntry logEntry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", "MyClass.java", 42)
            .create();

        FilePlaceholder placeholder = new FilePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("MyClass.java");
    }

    /**
     * Verifies that {@code null} will be resolved, if the source file name is not set.
     */
    @Test
    void resolveWithoutSourceFileName() {
        LogEntry logEntry = new LogEntryBuilder().create();
        FilePlaceholder placeholder = new FilePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the source file name of a log entry will be output, if set.
     */
    @Test
    void renderWithSourceFileName() {
        LogEntry logEntry = new LogEntryBuilder()
            .stackTraceElement("MyClass", "foo", "MyClass.java", 42)
            .create();

        FormatOutputRenderer renderer = new FormatOutputRenderer(new FilePlaceholder());
        assertThat(renderer.render(logEntry)).isEqualTo("MyClass.java");
    }

    /**
     * Verifies that {@code <file unknown>} will be output, if the source file name is not set.
     */
    @Test
    void renderWithoutSourceFileName() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new FilePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("<file unknown>");
    }

}
