package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the process ID of the current process.
 */
public class ProcessIdPlaceholder implements Placeholder {

    private final long processId;

    /**
     * @param processId The process ID to output
     */
    public ProcessIdPlaceholder(long processId) {
        this.processId = processId;
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITHOUT_LOCATION_INFO;
    }

    @Override
    public ValueType getType() {
        return ValueType.LONG;
    }

    @Override
    public Long getValue(LogEntry entry) {
        return processId;
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        builder.append(processId);
    }

}
