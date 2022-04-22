package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

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
	public ValueType getType() {
		return ValueType.STRING;
	}

	@Override
	public String getValue(LogEntry entry) {
		return entry.getFileName();
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String fileName = entry.getFileName();
		builder.append(fileName == null ? "<file unknown>" : fileName);
	}

}
