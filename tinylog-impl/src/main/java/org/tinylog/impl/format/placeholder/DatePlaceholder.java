package org.tinylog.impl.format.placeholder;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the date and time of issue for a log entry.
 */
public class DatePlaceholder implements Placeholder {

    private static final long MILLISECONDS_PER_SECOND = 1_000;

    private final DateTimeFormatter formatter;
    private final boolean forceFormatting;

    /**
     * @param formatter The formatter to use for formatting the date and time of issue
     * @param forceFormatting The date and time of issue will be returned as formatted string by the value getter, if
     *                        {@code true}, otherwise it will be returned as {@link Timestamp SQL timestamp}
     */
    public DatePlaceholder(DateTimeFormatter formatter, boolean forceFormatting) {
        this.formatter = formatter;
        this.forceFormatting = forceFormatting;
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITHOUT_LOCATION_INFO;
    }

    @Override
    public ValueType getType() {
        return forceFormatting ? ValueType.STRING : ValueType.TIMESTAMP;
    }

    @Override
    public Object getValue(LogEntry entry) {
        Instant instant = entry.getTimestamp();

        if (forceFormatting) {
            return formatter.format(instant);
        } else {
            Timestamp timestamp = new Timestamp(instant.getEpochSecond() * MILLISECONDS_PER_SECOND);
            timestamp.setNanos(instant.getNano());
            return timestamp;
        }
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        Instant instant = entry.getTimestamp();
        formatter.formatTo(instant, builder);
    }

}
