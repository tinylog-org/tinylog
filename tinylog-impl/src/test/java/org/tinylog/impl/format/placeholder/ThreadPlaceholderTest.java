package org.tinylog.impl.format.placeholder;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadPlaceholderTest {

    /**
     * Verifies that the thread placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        ThreadPlaceholder placeholder = new ThreadPlaceholder();
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that the source thread name of a log entry is resolved.
     */
    @Test
    void resolveValue() {
        Thread thread = new Thread(() -> { }, "foo");
        LogEntry logEntry = new LogEntryBuilder().thread(thread).create();

        ThreadPlaceholder placeholder = new ThreadPlaceholder();
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("foo");
    }

    /**
     * Verifies that the source thread name of a log entry is output.
     */
    @Test
    void renderString() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new ThreadPlaceholder());
        Thread thread = new Thread(() -> { }, "foo");
        LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
        assertThat(renderer.render(logEntry)).isEqualTo("foo");
    }

}
