package org.tinylog.impl.format.pattern.placeholders;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class DatePlaceholderTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneOffset.UTC);

    /**
     * Verifies that the log entry value {@link LogEntryValue#TIMESTAMP} is defined as required by the date placeholder.
     */
    @Test
    void requiredLogEntryValues() {
        DatePlaceholder placeholder = new DatePlaceholder(DateTimeFormatter.ISO_INSTANT, false);
        assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.TIMESTAMP);
    }

    /**
     * Verifies that the date and time of a log entry will be resolved as a {@link Timestamp}, if the date and time of
     * issue is set.
     */
    @Test
    void resolveUnformattedWithTimestamp() {
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        DatePlaceholder placeholder = new DatePlaceholder(formatter, false);
        assertThat(placeholder.getType()).isEqualTo(ValueType.TIMESTAMP);
        assertThat(placeholder.getValue(logEntry)).isEqualTo(new Timestamp(0));
    }

    /**
     * Verifies that {@code null} will be resolved, if the date and time of issue is not
     * set.
     */
    @Test
    void resolveUnformattedWithoutTimestamp() {
        LogEntry logEntry = new LogEntryBuilder().create();
        DatePlaceholder placeholder = new DatePlaceholder(formatter, false);
        assertThat(placeholder.getType()).isEqualTo(ValueType.TIMESTAMP);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the date and time of a log entry will be resolved as formatted string, if the date and time of
     * issue is set.
     */
    @Test
    void resolveFormattedWithTimestamp() {
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        DatePlaceholder placeholder = new DatePlaceholder(formatter, true);
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isEqualTo("1970-01-01T00:00:00Z");
    }

    /**
     * Verifies that {@code null} will be resolved, if the date and time of issue is not set.
     */
    @Test
    void resolveFormattedWithoutTimestamp() {
        LogEntry logEntry = new LogEntryBuilder().create();
        DatePlaceholder placeholder = new DatePlaceholder(formatter, true);
        assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(placeholder.getValue(logEntry)).isNull();
    }

    /**
     * Verifies that the formatted date and time of a log entry will be output, if the timestamp is set.
     */
    @Test
    void renderWithTimestamp() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new DatePlaceholder(formatter, false));
        LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
        assertThat(renderer.render(logEntry)).isEqualTo("1970-01-01T00:00:00Z");
    }

    /**
     * Verifies that {@code <timestamp unknown>} will be output, if the timestamp is not set.
     */
    @Test
    void renderWithoutTimestamp() {
        FormatOutputRenderer renderer = new FormatOutputRenderer(new DatePlaceholder(formatter, false));
        LogEntry logEntry = new LogEntryBuilder().create();
        assertThat(renderer.render(logEntry)).isEqualTo("<timestamp unknown>");
    }

}
