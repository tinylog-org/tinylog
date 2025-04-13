package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving the assigned tag of a log entry.
 */
public class TagPlaceholder implements Placeholder {

    /** */
    public TagPlaceholder() {
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
        return entry.getTag();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String tag = entry.getTag();
        builder.append(tag == null ? "" : tag);
    }

}
