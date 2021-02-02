package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementation for resolving thread context values for a log entry.
 */
public class ContextPlaceholder implements Placeholder {

	private final String key;
	private final String defaultRenderValue;
	private final String defaultApplyValue;

	/**
	 * @param key The key of the thread context value to output
	 * @param defaultRenderValue The default value to append to string builders, if there is no value stored for the
	 *                           passed key
	 * @param defaultApplyValue The default value to apply to prepared SQL statements, if there is no value stored for
	 *                          the passed key
	 */
	public ContextPlaceholder(String key, String defaultRenderValue, String defaultApplyValue) {
		this.key = key;
		this.defaultRenderValue = defaultRenderValue;
		this.defaultApplyValue = defaultApplyValue;
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.CONTEXT);
	}

	@Override
	public void render(StringBuilder builder, LogEntry entry) {
		String value = entry.getContext().getOrDefault(key, defaultRenderValue);
		builder.append(value);
	}

	@Override
	public void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException {
		String value = entry.getContext().getOrDefault(key, defaultApplyValue);
		statement.setString(index, value);
	}

}
