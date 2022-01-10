package org.tinylog.impl.format.pattern.placeholders;

import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;

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
	public void render(StringBuilder builder, LogEntry entry) {
		Level level = entry.getSeverityLevel();
		if (level == null) {
			builder.append("?");
		} else {
			builder.append(level.ordinal());
		}
	}

	@Override
	public SqlRecord<? extends Number> resolve(LogEntry entry) {
		Level level = entry.getSeverityLevel();

		if (level == null) {
			return new SqlRecord<>(Types.INTEGER, null);
		} else {
			return new SqlRecord<>(Types.INTEGER, level.ordinal());
		}
	}

}
