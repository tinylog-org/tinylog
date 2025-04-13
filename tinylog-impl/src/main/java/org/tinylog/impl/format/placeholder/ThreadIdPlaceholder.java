package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the source thread ID of a log entry.
 */
public class ThreadIdPlaceholder implements Placeholder {

    /** */
    public ThreadIdPlaceholder() {
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
        Thread thread = entry.getThread();
        return thread.getId();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        Thread thread = entry.getThread();
        builder.append(thread.getId());
    }

}
