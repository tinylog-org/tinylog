package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.tinylog.impl.LogEntry;

/**
 * Placeholder implementations resolve the real values for placeholders in format patterns.
 */
public interface Placeholder {

	/**
	 * Renders this placeholder for a passed log entry.
	 *
	 * <p>
	 *     The resolved value for this placeholder is appended to the passed {@link StringBuilder}.
	 * </p>
	 *
	 * @param builder The string builder for the rendered format pattern
	 * @param entry The log entry to render
	 */
	void render(StringBuilder builder, LogEntry entry);

	/**
	 * Applies the corresponding log entry values for this placeholder to a prepared SQL statement.
	 *
	 * @param statement The target SQL statement
	 * @param index The parameter index to fill in the passed SQL statement
	 * @param entry The log entry to apply
	 * @throws SQLException Failed to set the parameter in the passed SQL statement
	 */
	void apply(PreparedStatement statement, int index, LogEntry entry) throws SQLException;

}
