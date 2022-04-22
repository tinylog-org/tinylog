package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving the process ID of the current process.
 */
public class ProcessIdPlaceholder implements Placeholder {

	private final long processId;

	/**
	 * @param processId The process ID to output
	 */
	public ProcessIdPlaceholder(long processId) {
		this.processId = processId;
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.noneOf(LogEntryValue.class);
	}

	@Override
	public ValueType getType() {
		return ValueType.LONG;
	}

	@Override
	public Long getValue(LogEntry entry) {
		return processId;
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		builder.append(processId);
	}

}
