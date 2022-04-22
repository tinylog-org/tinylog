package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.SqlType;

/**
 * Placeholder implementation for printing the log message and exception of a log entry.
 */
public class MessagePlaceholder implements Placeholder {

	private final Placeholder messageOnlyPlaceholder;
	private final Placeholder exceptionPlaceholder;

	/** */
	public MessagePlaceholder() {
		messageOnlyPlaceholder = new MessageOnlyPlaceholder();
		exceptionPlaceholder = new ExceptionPlaceholder();
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String message = entry.getMessage();
		Throwable exception = entry.getException();

		if (message != null) {
			messageOnlyPlaceholder.render(builder, entry);
		}

		if (message != null && exception != null) {
			builder.append(": ");
		}

		if (exception != null) {
			exceptionPlaceholder.render(builder, entry);
		}
	}

	@Override
	public SqlRecord<? extends CharSequence> resolve(LogEntry entry) {
		String message = entry.getMessage();
		Throwable exception = entry.getException();

		if (message == null && exception == null) {
			return new SqlRecord<>(SqlType.STRING, null);
		} else {
			StringBuilder builder = new StringBuilder();
			render(builder, entry);
			return new SqlRecord<>(SqlType.STRING, builder);
		}
	}

}
