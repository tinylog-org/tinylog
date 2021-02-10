package org.tinylog.impl.format.placeholder;

import java.sql.Types;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.ToLongFunction;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;

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
	public void render(StringBuilder builder, LogEntry entry) {
		Instant instant = entry.getTimestamp();
		builder.append(instant == null ? "<timestamp unknown>" : timestampMapper.applyAsLong(instant));
	}

	@Override
	public SqlRecord<? extends Number> resolve(LogEntry entry) {
		Instant timestamp = entry.getTimestamp();

		if (timestamp == null) {
			return new SqlRecord<>(Types.BIGINT, null);
		} else {
			return new SqlRecord<>(Types.BIGINT, timestampMapper.applyAsLong(timestamp));
		}
	}

}
