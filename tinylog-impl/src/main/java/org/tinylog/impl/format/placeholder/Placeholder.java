package org.tinylog.impl.format.placeholder;

import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;

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
	 * Resolves the {@link SqlRecord} for a passed log entry.
	 *
	 * @param entry The log entry to resolve
	 * @return The resolved typed SQL value to insert or update into an SQL table
	 */
	SqlRecord<?> resolve(LogEntry entry);

}
