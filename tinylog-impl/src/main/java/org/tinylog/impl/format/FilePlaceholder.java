package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementation for resolving the source file name of a log entry.
 */
public class FilePlaceholder implements Placeholder {

	/** */
	public FilePlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.FILE);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String fileName = entry.getFileName();
		builder.append(fileName == null ? "<file unknown>" : fileName);
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		statement.setString(index, entry.getFileName());
	}

}
