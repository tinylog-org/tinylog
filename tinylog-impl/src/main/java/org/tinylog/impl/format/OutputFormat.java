package org.tinylog.impl.format;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Formatter for outputting log entries.
 */
public interface OutputFormat {

    /**
     * Returns the output details for this output format.
     *
     * <p>
     *     tinylog calls this method only once during the initialization phase and assumes that the output details will
     *     never change afterwards.
     * </p>
     *
     * @return The output detail for this output format
     */
    OutputDetails getOutputDetails();

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
