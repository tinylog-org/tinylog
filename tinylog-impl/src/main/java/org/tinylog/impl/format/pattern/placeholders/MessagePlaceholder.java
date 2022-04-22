package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

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
	public ValueType getType() {
		return ValueType.STRING;
	}

	@Override
	public String getValue(LogEntry entry) {
		String message = entry.getMessage();
		Throwable exception = entry.getException();

		if (message == null && exception == null) {
			return null;
		} else {
			StringBuilder builder = new StringBuilder();
			render(builder, entry);
			return builder.toString();
		}
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

}
