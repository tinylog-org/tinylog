package org.tinylog.impl.format.placeholder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementation for resolving the source thread ID of a log entry.
 */
public class ThreadIdPlaceholder implements Placeholder {

	/** */
	public ThreadIdPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.THREAD);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Thread thread = entry.getThread();
		builder.append(thread == null ? "?" : thread.getId());
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		Thread thread = entry.getThread();

		if (thread == null) {
			statement.setNull(index, Types.BIGINT);
		} else {
			statement.setLong(index, thread.getId());
		}
	}

}
