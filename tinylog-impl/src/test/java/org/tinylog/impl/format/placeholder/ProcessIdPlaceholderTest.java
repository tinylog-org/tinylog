package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessIdPlaceholderTest {

    /**
     * Verifies that the process ID placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        ProcessIdPlaceholder placeholder = new ProcessIdPlaceholder(42);
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that the passed process ID is resolved.
     */
    @Test
    void resolveValue() {
        LogEntry logEntry = new LogEntryBuilder().create();
        ProcessIdPlaceholder placeholder = new ProcessIdPlaceholder(1000);
        assertThat(placeholder.getType()).isEqualTo(ValueType.LONG);
        assertThat(placeholder.getValue(logEntry)).isEqualTo(1000L);
    }

    /**
     * Verifies that the passed process ID is output.
     */
    @Test
    void renderString() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new ProcessIdPlaceholder(1000));
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("1000");
    }

}
