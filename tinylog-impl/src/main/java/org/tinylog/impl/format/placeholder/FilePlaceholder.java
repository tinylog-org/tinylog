package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the source file name of a log entry.
 */
public class FilePlaceholder implements Placeholder {

    /** */
    public FilePlaceholder() {
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO;
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        return entry.getFileName();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String fileName = entry.getFileName();
        builder.append(fileName == null ? "<file unknown>" : fileName);
    }

}
