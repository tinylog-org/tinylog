package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the source thread name of a log entry.
 */
public class ThreadPlaceholder implements Placeholder {

    /** */
    public ThreadPlaceholder() {
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITHOUT_LOCATION_INFO;
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        Thread thread = entry.getThread();
        return thread.getName();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        Thread thread = entry.getThread();
        builder.append(thread.getName());
    }

}
