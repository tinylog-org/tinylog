package org.tinylog.impl.format.placeholders;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;

/**
 * Placeholder implementation for resolving the date and time of issue for a log entry.
 */
public class DatePlaceholder implements Placeholder {

	private static final long MILLIS_PER_SECOND = 1_000;

	private final DateTimeFormatter formatter;
	private final boolean formatForSql;

	/**
	 * @param formatter The formatter to use for formatting the date and time of issue
	 * @param formatForSql The date and time of issue will be applied as formatted string to prepared SQL statements if
	 *                     set to {@code true}, otherwise it will be applied as {@link Timestamp SQL timestamp}
	 */
	public DatePlaceholder(DateTimeFormatter formatter, boolean formatForSql) {
		this.formatter = formatter;
		this.formatForSql = formatForSql;
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.TIMESTAMP);
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

	@Override
	public SqlRecord<?> resolve(LogEntry entry) {
		Instant instant = entry.getTimestamp();

		if (formatForSql) {
			return new SqlRecord<>(Types.VARCHAR, instant == null ? null : formatter.format(instant));
		} else if (instant == null) {
			return new SqlRecord<>(Types.TIMESTAMP, null);
		} else {
			Timestamp timestamp = new Timestamp(instant.getEpochSecond() * MILLIS_PER_SECOND);
			timestamp.setNanos(instant.getNano());
			return new SqlRecord<>(Types.TIMESTAMP, timestamp);
		}
	}

}
