package org.tinylog.impl.format.placeholder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementation for resolving the source method name for a log entry.
 */
public class MethodPlaceholder implements Placeholder {

	/** */
	public MethodPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.METHOD);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String methodName = entry.getMethodName();
		builder.append(methodName == null ? "<method unknown>" : methodName);
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		statement.setString(index, entry.getMethodName());
	}

}
