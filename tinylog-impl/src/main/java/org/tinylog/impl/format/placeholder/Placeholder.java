package org.tinylog.impl.format.placeholder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Placeholder implementations resolve the real values for placeholders in format patterns.
 */
public interface Placeholder {

	/**
	 * Returns a set with all required log entry properties used by this placeholder.
	 *
	 * <p>
	 *     For performance optimization, tinylog may not set properties of {@link LogEntry LogEntries} that a
	 *     placeholder does not define as required.
	 * </p>
	 *
	 * <p>
	 *     tinylog calls this method only once during the initialization phase and assumes that the set of required log
	 *     entry properties will never change afterwards.
	 * </p>
	 *
	 * @return The set of all required log entry properties
	 */
	Set<LogEntryValue> getRequiredLogEntryValues();

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
