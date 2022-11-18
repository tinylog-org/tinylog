package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class FilePlaceholderTest {

    /**
     * Verifies that the log entry value {@link LogEntryValue#FILE} is defined as required by the file placeholder.
     */
    @Test
    void requiredLogEntryValues() {
        FilePlaceholder placeholder = new FilePlaceholder();
        assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.FILE);
    }

    /**
     * Verifies that the source file name of a log entry will be resolved, if set.
     */
    @Test
    void resolveWithSourceFileName() {
        LogEntry logEntry = new LogEntryBuilder().fileName("foo.java").create();
        FilePlaceholder placeholder = new FilePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("foo.java");
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
        FormatOutputRenderer renderer = new FormatOutputRenderer(new FilePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().fileName("foo.java").create();
        assertThat(renderer.render(logEntry)).isEqualTo("foo.java");
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
