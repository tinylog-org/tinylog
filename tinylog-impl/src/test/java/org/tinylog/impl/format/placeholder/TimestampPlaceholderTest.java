package org.tinylog.impl.format.placeholder;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class TimestampPlaceholderTest {

    /**
     * Verifies that the timestamp placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        TimestampPlaceholder placeholder = new TimestampPlaceholder(Instant::toEpochMilli);
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that the timestamp of issue of a log entry is resolved.
     */
    @Test
    void resolveValue() {
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1000)).create();
        TimestampPlaceholder placeholder = new TimestampPlaceholder(Instant::toEpochMilli);
        assertThat(placeholder.getType()).isEqualTo(ValueType.LONG);
        assertThat(placeholder.getValue(logEntry)).isEqualTo(1000L);
    }

    /**
     * Verifies that the timestamp of issue of a log entry will be output, if present.
     */
    @Test
    void renderString() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new TimestampPlaceholder(Instant::toEpochMilli));
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1000)).create();
        assertThat(renderer.render(logEntry)).isEqualTo("1000");
    }

}
