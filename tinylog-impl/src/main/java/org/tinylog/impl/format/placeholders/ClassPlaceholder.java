package org.tinylog.impl.format.placeholders;

import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;

/**
 * Placeholder implementation for resolving the fully-qualified class name for a log entry.
 */
public class ClassPlaceholder implements Placeholder {

	/** */
	public ClassPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.CLASS);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String className = entry.getClassName();
		builder.append(className == null ? "<class unknown>" : className);
	}

	@Override
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		return new SqlRecord<>(Types.VARCHAR, entry.getClassName());
	}

}
