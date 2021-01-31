package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementation for resolving the fully-qualified class name for a log entry.
 */
public class ClassPlaceholder implements Placeholder {

	/** */
	public ClassPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.CLASS);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String className = entry.getClassName();
		builder.append(className == null ? "<unknown>" : className);
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		statement.setString(index, entry.getClassName());
	}

}
