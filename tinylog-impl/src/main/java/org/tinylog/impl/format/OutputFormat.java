package org.tinylog.impl.format;

import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Formatter for outputting log entries.
 */
public interface OutputFormat {

    /**
     * Returns a set with all required log entry properties used by this output format.
     *
     * <p>
     *     For performance optimization, tinylog may not set properties of {@link LogEntry} instances that are not
     *     define as required.
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
     * Renders this output format for a passed log entry.
     *
     * <p>
     *     The resolved value for this output format is appended to the passed {@link StringBuilder}.
     * </p>
     *
     * @param builder The string builder for the rendered format pattern
     * @param entry The log entry to render
     */
    void render(StringBuilder builder, LogEntry entry);

}
