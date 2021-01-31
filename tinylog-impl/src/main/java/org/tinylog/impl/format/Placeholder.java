package org.tinylog.impl.format;

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

}
