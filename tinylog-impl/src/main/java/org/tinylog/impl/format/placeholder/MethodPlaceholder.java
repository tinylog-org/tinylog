package org.tinylog.impl.format.placeholder;

import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;

/**
 * Placeholder implementation for resolving the source method name for a log entry.
 */
public class MethodPlaceholder implements Placeholder {

	/** */
	public MethodPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.METHOD);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String methodName = entry.getMethodName();
		builder.append(methodName == null ? "<method unknown>" : methodName);
	}

	@Override
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		return new SqlRecord<>(Types.VARCHAR, entry.getMethodName());
	}

}
