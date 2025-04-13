package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class SeverityCodePlaceholderTest {

    /**
     * Verifies that the severity code placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        SeverityCodePlaceholder placeholder = new SeverityCodePlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that the numeric severity level code of a log entry is resolved.
     */
    @Test
    void resolveValue() {
        LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
        SeverityCodePlaceholder placeholder = new SeverityCodePlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.INTEGER);
        assertThat(placeholder.getValue(logEntry)).isEqualTo(3);
    }

    /**
     * Verifies that the numeric severity level code of a log entry is output.
     */
    @Test
    void renderString() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new SeverityCodePlaceholder());
        LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
        assertThat(renderer.render(logEntry)).isEqualTo("3");
    }

}
