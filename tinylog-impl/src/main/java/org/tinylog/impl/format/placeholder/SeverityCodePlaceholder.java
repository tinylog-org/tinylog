package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the numeric {@link Level severity level} code of a log entry.
 */
public class SeverityCodePlaceholder implements Placeholder {

    /** */
    public SeverityCodePlaceholder() {
    }

    @Override
    public OutputDetails getOutputDetails() {
        return OutputDetails.ENABLED_WITHOUT_LOCATION_INFO;
    }

    @Override
    public ValueType getType() {
        return ValueType.INTEGER;
    }

    @Override
    public Integer getValue(LogEntry entry) {
        Level level = entry.getSeverityLevel();
        return level.ordinal();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        Level level = entry.getSeverityLevel();
        builder.append(level.ordinal());
    }

}
