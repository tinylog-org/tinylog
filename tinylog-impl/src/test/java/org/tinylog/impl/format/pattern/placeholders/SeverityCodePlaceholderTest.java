package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class SeverityCodePlaceholderTest {

    /**
     * Verifies that the log entry value {@link LogEntryValue#LEVEL} is defined as required by the severity code
     * placeholder.
     */
    @Test
    void requiredLogEntryValues() {
        SeverityCodePlaceholder placeholder = new SeverityCodePlaceholder();
        assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.LEVEL);
    }

    /**
     * Verifies that the numeric severity level code of a log entry will be resolved,  if the severity level is set.
     */
    @Test
    void resolveWithSeverityLevel() {
        LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
        SeverityCodePlaceholder placeholder = new SeverityCodePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.INTEGER);
        assertThat(placeholder.getValue(logEntry)).isEqualTo(3);
    }

    /**
     * Verifies that {@code null} will be resolved, if the severity level is not set.
     */
    @Test
    void resolveWithoutSeverityLevel() {
        LogEntry logEntry = new LogEntryBuilder().create();
        SeverityCodePlaceholder placeholder = new SeverityCodePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.INTEGER);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the numeric severity level code of a log entry will be output, if the severity level set.
     */
    @Test
    void renderWithSeverityLevel() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new SeverityCodePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
        assertThat(renderer.render(logEntry)).isEqualTo("3");
    }

    /**
     * Verifies that "?" will be output, if the severity level is not set.
     */
    @Test
    void renderWithoutSeverityLevel() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new SeverityCodePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("?");
    }

}
