package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementation for resolving the date and time of issue for a log entry.
 */
public class DatePlaceholder implements Placeholder {

	private final DateTimeFormatter formatter;

	/**
	 * @param formatter The formatter to use for formatting the date and time of issue
	 */
	public DatePlaceholder(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.TIMESTAMP);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Instant instant = entry.getTimestamp();
		if (instant == null) {
			builder.append("<unknown>");
		} else {
			formatter.formatTo(instant, builder);
		}
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		Instant instant = entry.getTimestamp();
		statement.setTimestamp(index, instant == null ? null : Timestamp.from(instant));
	}

}
