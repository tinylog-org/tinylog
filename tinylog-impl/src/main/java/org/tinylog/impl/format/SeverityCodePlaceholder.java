package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementation for resolving the numeric {@link Level severity level} code of a log entry.
 */
public class SeverityCodePlaceholder implements Placeholder {

	/** */
	public SeverityCodePlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.LEVEL);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Level level = entry.getSeverityLevel();
		if (level == null) {
			builder.append("?");
		} else {
			builder.append(level.ordinal());
		}
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		Level level = entry.getSeverityLevel();
		if (level == null) {
			statement.setNull(index, Types.INTEGER);
		} else {
			statement.setInt(index, level.ordinal());
		}
	}

}
