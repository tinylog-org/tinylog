package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the {@link Level severity level} of a log entry.
 */
public class LevelPlaceholder implements Placeholder {

    /** */
    public LevelPlaceholder() {
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
        Level level = entry.getSeverityLevel();
        return level.toString();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        Level level = entry.getSeverityLevel();
        builder.append(level.toString());
    }

}
