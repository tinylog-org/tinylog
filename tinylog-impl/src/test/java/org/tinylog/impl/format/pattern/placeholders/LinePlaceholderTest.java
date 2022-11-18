package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class LinePlaceholderTest {

    /**
     * Verifies that the log entry value {@link LogEntryValue#LINE} is defined as required by the line placeholder.
     */
    @Test
    void requiredLogEntryValues() {
        LinePlaceholder placeholder = new LinePlaceholder();
        assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.LINE);
    }

    /**
     * Verifies that the line number of a log entry in the source file will be resolved, if set.
     */
    @Test
    void resolveWithSourceLineName() {
        LogEntry logEntry = new LogEntryBuilder().lineNumber(100).create();
        LinePlaceholder placeholder = new LinePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.INTEGER);
        assertThat(placeholder.getValue(logEntry)).isEqualTo(100);
    }

    /**
     * Verifies that {@code null} will be resolved, if the line number in the source file is not set.
     */
    @Test
    void resolveWithoutSourceLineName() {
        LogEntry logEntry = new LogEntryBuilder().create();
        LinePlaceholder placeholder = new LinePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.INTEGER);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the line number of a log entry in the source file will be output, if set.
     */
    @Test
    void renderWithSourceLineName() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new LinePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().lineNumber(100).create();
        assertThat(renderer.render(logEntry)).isEqualTo("100");
    }

    /**
     * Verifies that "?" will be output, if the line number in the source file is not set.
     */
    @Test
    void renderWithoutSourceLineName() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new LinePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("?");
    }

}
