package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving the numeric {@link Level severity level} code of a log entry.
 */
public class SeverityCodePlaceholder implements Placeholder {

	/** */
	public SeverityCodePlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.LEVEL);
	}

	@Override
	public ValueType getType() {
		return ValueType.INTEGER;
	}

	@Override
	public Integer getValue(LogEntry entry) {
		Level level = entry.getSeverityLevel();
		return level == null ? null : level.ordinal();
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Level level = entry.getSeverityLevel();
		if (level == null) {
			builder.append("?");
		} else {
			builder.append(level.ordinal());
		}
	}

}
