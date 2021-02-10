package org.tinylog.impl.format.placeholder;

import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;

/**
 * Placeholder implementation for resolving the source thread name of a log entry.
 */
public class ThreadPlaceholder implements Placeholder {

	/** */
	public ThreadPlaceholder() {
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.THREAD);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		Thread thread = entry.getThread();
		builder.append(thread == null ? "<thread unknown>" : thread.getName());
	}

	@Override
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		Thread thread = entry.getThread();

		if (thread == null) {
			return new SqlRecord<>(Types.VARCHAR, null);
		} else {
			return new SqlRecord<>(Types.VARCHAR, thread.getName());
		}
	}

}
