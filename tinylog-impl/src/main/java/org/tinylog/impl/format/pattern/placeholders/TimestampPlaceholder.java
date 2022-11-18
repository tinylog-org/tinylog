package org.tinylog.impl.format.pattern.placeholders;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.ToLongFunction;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving the Unix timestamp of issue for a log entry.
 */
public class TimestampPlaceholder implements Placeholder {

    private final ToLongFunction<Instant> timestampMapper;

    /**
     * @param timestampMapper The mapping function for converting an instant into a long (e.g.
     *                        {@link Instant#toEpochMilli()} and {@link Instant#getEpochSecond()})
     */
    public TimestampPlaceholder(ToLongFunction<Instant> timestampMapper) {
        this.timestampMapper = timestampMapper;
    }

    @Override
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.TIMESTAMP);
    }

    @Override
    public ValueType getType() {
        return ValueType.LONG;
    }

    @Override
    public Long getValue(LogEntry entry) {
        Instant timestamp = entry.getTimestamp();
        return timestamp == null ? null : timestampMapper.applyAsLong(timestamp);
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        Instant instant = entry.getTimestamp();
        builder.append(instant == null ? "<timestamp unknown>" : timestampMapper.applyAsLong(instant));
    }

}
