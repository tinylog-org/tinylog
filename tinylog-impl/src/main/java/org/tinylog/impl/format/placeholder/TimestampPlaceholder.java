package org.tinylog.impl.format.placeholder;

import java.time.Instant;
import java.util.function.ToLongFunction;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

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
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITHOUT_LOCATION_INFO;
    }

    @Override
    public ValueType getType() {
        return ValueType.LONG;
    }

    @Override
    public Long getValue(LogEntry entry) {
        Instant timestamp = entry.getTimestamp();
        return timestampMapper.applyAsLong(timestamp);
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        Instant instant = entry.getTimestamp();
        builder.append(timestampMapper.applyAsLong(instant));
    }

}
