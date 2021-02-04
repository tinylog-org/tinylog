package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementation for resolving the {@link Level severity level} of a log entry.
 */
public class LevelPlaceholder implements Placeholder {

	/** */
	public LevelPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.LEVEL);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Level level = entry.getSeverityLevel();
		builder.append(level == null ? "<level unknown>" : level);
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		Level level = entry.getSeverityLevel();
		statement.setString(index, level == null ? null : level.toString());
	}

}
