package org.tinylog.impl.format.placeholder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementation for resolving the line number of a log entry in the source file.
 */
public class LinePlaceholder implements Placeholder {

	/** */
	public LinePlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.LINE);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		int lineNumber = entry.getLineNumber();
		if (lineNumber < 0) {
			builder.append("?");
		} else {
			builder.append(lineNumber);
		}
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		int lineNumber = entry.getLineNumber();
		if (lineNumber < 0) {
			statement.setNull(index, Types.INTEGER);
		} else {
			statement.setInt(index, lineNumber);
		}
	}

}
