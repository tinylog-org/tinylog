package org.tinylog.impl.format.pattern.placeholders;

import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;

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
	public SqlRecord<? extends Number> resolve(LogEntry entry) {
		int lineNumber = entry.getLineNumber();
		return new SqlRecord<>(Types.INTEGER, lineNumber < 0 ? null : lineNumber);
	}

}
