package org.tinylog.impl.format.pattern.placeholders;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving the date and time of issue for a log entry.
 */
public class DatePlaceholder implements Placeholder {

    private static final long MILLIS_PER_SECOND = 1_000;

    private final DateTimeFormatter formatter;
    private final boolean formatAlways;

    /**
     * @param formatter The formatter to use for formatting the date and time of issue
     * @param formatAlways The date and time of issue will be returned as formatted string by the value getter, if
     *                     {@code true}, otherwise it will be returned as {@link Timestamp SQL timestamp}
     */
    public DatePlaceholder(DateTimeFormatter formatter, boolean formatAlways) {
        this.formatter = formatter;
        this.formatAlways = formatAlways;
    }

    @Override
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.TIMESTAMP);
    }

    @Override
    public ValueType getType() {
        return formatAlways ? ValueType.STRING : ValueType.TIMESTAMP;
    }

    @Override
    public Object getValue(LogEntry entry) {
        Instant instant = entry.getTimestamp();

        if (instant == null) {
            return null;
        } else if (formatAlways) {
            return formatter.format(instant);
        } else {
            Timestamp timestamp = new Timestamp(instant.getEpochSecond() * MILLIS_PER_SECOND);
            timestamp.setNanos(instant.getNano());
            return timestamp;
        }
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        Instant instant = entry.getTimestamp();
        if (instant == null) {
            builder.append("<timestamp unknown>");
        } else {
            formatter.formatTo(instant, builder);
        }
    }

}
