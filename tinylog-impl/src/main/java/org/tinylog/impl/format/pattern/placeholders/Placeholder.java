package org.tinylog.impl.format.pattern.placeholders;

import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementations resolve the real values for placeholders in format patterns.
 */
public interface Placeholder extends OutputFormat {

    /**
     * Returns a set with all required log entry properties used by this placeholder.
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
     * Gets the type for the stored value.
     *
     * @return The type of the stored value
     */
    ValueType getType();

    /**
     * Gets the nullable placeholder value from the passed log entry.
     *
     * <p>
     *     The type of the returned value must match with the {@link ValueType} of {@link #getType()}.
     * </p>
     *
     * @param entry The log entry for extracting the value from
     * @return The extracted value
     */
    Object getValue(LogEntry entry);

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
