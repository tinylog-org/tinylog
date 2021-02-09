package org.tinylog.impl.format.placeholder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

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
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		String message = entry.getMessage();
		Throwable exception = entry.getException();

		if (message == null && exception == null) {
			statement.setString(index, null);
		} else {
			StringBuilder builder = new StringBuilder();
			render(builder, entry);
			statement.setString(index, builder.toString());
		}
	}

}
