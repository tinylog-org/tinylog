package org.tinylog.impl.format.placeholders;

import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;

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
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		return new SqlRecord<>(Types.LONGVARCHAR, entry.getMessage());
	}

}
