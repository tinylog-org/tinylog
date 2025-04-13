package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the line number of a log entry in the source file.
 */
public class LinePlaceholder implements Placeholder {

    /** */
    public LinePlaceholder() {
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITH_FULL_LOCATION_INFO;
    }

    @Override
    public ValueType getType() {
        return ValueType.INTEGER;
    }

    @Override
    public Integer getValue(LogEntry entry) {
        int lineNumber = entry.getLineNumber();
        return lineNumber < 0 ? null : lineNumber;
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        int lineNumber = entry.getLineNumber();
        if (lineNumber < 0) {
            builder.append("?");
        } else {
            builder.append(lineNumber);
        }
    }

}
