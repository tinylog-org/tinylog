package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.SqlType;

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
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		return new SqlRecord<>(SqlType.STRING, entry.getFileName());
	}

}
