package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementation for printing the log message without potential exception of a log entry.
 */
public class MessageOnlyPlaceholder implements Placeholder {

	/** */
	public MessageOnlyPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.MESSAGE);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String message = entry.getMessage();

		if (message != null) {
			builder.append(message);
		}
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		statement.setString(index, entry.getMessage());
	}

}
