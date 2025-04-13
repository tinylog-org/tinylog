package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.impl.format.OutputFormat;

/**
 * Placeholder implementations resolve the real values for placeholders in format patterns.
 */
public interface Placeholder extends OutputFormat {

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
     * @param entry The log entry to extract the value from
     * @return The extracted value
     */
    Object getValue(LogEntry entry);

}
