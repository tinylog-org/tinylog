package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.impl.LogEntry;

/**
 * Placeholder implementation for resolving one thread context value for a log entry.
 */
public class SingleValueContextPlaceholder extends AbstractContextPlaceholder {

    private final String key;

    /**
     * @param key The key of the thread context value to output
     */
    public SingleValueContextPlaceholder(String key) {
        this.key = key;
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
