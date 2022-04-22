package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.SqlType;

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
		return new SqlRecord<>(SqlType.STRING, entry.getMethodName());
	}

}
