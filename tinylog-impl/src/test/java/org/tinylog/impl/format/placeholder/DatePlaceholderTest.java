package org.tinylog.impl.format.placeholder;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class DatePlaceholderTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneOffset.UTC);

    /**
     * Verifies that the date placeholder enables output but does not require any kind of location information.
     */
    @Test
    void provideOutputDetails() {
        DatePlaceholder placeholder = new DatePlaceholder(formatter, false);
        assertThat(placeholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);
    }

    /**
     * Verifies that the date and time of a log entry is resolved as a {@link Timestamp}.
     */
    @Test
    void resolveSqlTimestamp() {
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        DatePlaceholder placeholder = new DatePlaceholder(formatter, false);
        assertThat(placeholder.getType()).isEqualTo(ValueType.TIMESTAMP);
        assertThat(placeholder.getValue(logEntry)).isEqualTo(new Timestamp(0));
    }

    /**
     * Verifies that the date and time of a log entry is resolved as formatted string.
     */
    @Test
    void resolveFormattedString() {
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        DatePlaceholder placeholder = new DatePlaceholder(formatter, true);
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("1970-01-01T00:00:00Z");
    }

    /**
     * Verifies that the formatted date and time of a log entry is output.
     */
    @Test
    void renderString() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new DatePlaceholder(formatter, false));
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(renderer.render(logEntry)).isEqualTo("1970-01-01T00:00:00Z");
    }

}
