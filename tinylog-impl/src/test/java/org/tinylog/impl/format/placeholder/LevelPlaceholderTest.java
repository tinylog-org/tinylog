package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class LevelPlaceholderTest {

    /**
     * Verifies that the level placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        LevelPlaceholder placeholder = new LevelPlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that the severity level of a log entry is resolved.
     */
    @Test
    void resolveString() {
        LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
        LevelPlaceholder placeholder = new LevelPlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("INFO");
    }

    /**
     * Verifies that the severity level of a log entry is output.
     */
    @Test
    void renderString() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new LevelPlaceholder());
        LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
        assertThat(renderer.render(logEntry)).isEqualTo("INFO");
    }

}
