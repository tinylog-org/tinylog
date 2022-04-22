package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

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
	public ValueType getType() {
		return ValueType.INTEGER;
	}

	@Override
	public Integer getValue(LogEntry entry) {
		int lineNumber = entry.getLineNumber();
		return lineNumber < 0 ? null : lineNumber;
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

}
