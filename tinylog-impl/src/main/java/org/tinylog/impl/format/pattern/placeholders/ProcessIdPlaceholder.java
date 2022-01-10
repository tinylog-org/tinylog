package org.tinylog.impl.format.pattern.placeholders;

import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;

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
	public void render(StringBuilder builder, LogEntry entry) {
		builder.append(processId);
	}

	@Override
	public SqlRecord<? extends Number> resolve(LogEntry entry) {
		return new SqlRecord<>(Types.BIGINT, processId);
	}

}
