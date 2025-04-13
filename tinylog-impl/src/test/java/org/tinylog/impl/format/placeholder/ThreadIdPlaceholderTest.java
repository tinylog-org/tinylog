package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadIdPlaceholderTest {

    /**
     * Verifies that the thread ID placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        ThreadIdPlaceholder placeholder = new ThreadIdPlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that the source thread ID of a log entry is resolved.
     */
    @Test
    void resolveValue() {
        Thread thread = new Thread(() -> { });
        LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
        ThreadIdPlaceholder placeholder = new ThreadIdPlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.LONG);
        assertThat(placeholder.getValue(logEntry)).isEqualTo(thread.getId());
    }

    /**
     * Verifies that the source thread ID of a log entry is output.
     */
    @Test
    void renderString() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new ThreadIdPlaceholder());
        Thread thread = new Thread(() -> { });
        LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
        assertThat(renderer.render(logEntry)).isEqualTo(Long.toString(thread.getId()));
    }

}
