package org.tinylog.impl.format.placeholder;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving thread context values for a log entry.
 */
public class ContextPlaceholder implements Placeholder {

    private final String key;

    /**
     * @param key The key of the thread context value to output
     */
    public ContextPlaceholder(String key) {
        this.key = key;
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
        return entry.getContext().get(key);
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String value = entry.getContext().getOrDefault(key, "");
        builder.append(value);
    }

}
