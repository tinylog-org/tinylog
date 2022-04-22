package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.SqlType;

/**
 * Placeholder implementation for resolving the {@link Level severity level} of a log entry.
 */
public class LevelPlaceholder implements Placeholder {

	/** */
	public LevelPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.LEVEL);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Level level = entry.getSeverityLevel();
		builder.append(level == null ? "<level unknown>" : level.toString());
	}

	@Override
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		Level level = entry.getSeverityLevel();
		return new SqlRecord<>(SqlType.STRING, level == null ? null : level.toString());
	}

}
