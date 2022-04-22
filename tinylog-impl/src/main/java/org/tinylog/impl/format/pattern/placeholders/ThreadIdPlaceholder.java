package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving the source thread ID of a log entry.
 */
public class ThreadIdPlaceholder implements Placeholder {

	/** */
	public ThreadIdPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.THREAD);
	}

	@Override
	public ValueType getType() {
		return ValueType.LONG;
	}

	@Override
	public Long getValue(LogEntry entry) {
		Thread thread = entry.getThread();
		return thread == null ? null : thread.getId();
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Thread thread = entry.getThread();
		builder.append(thread == null ? "?" : thread.getId());
	}

}
