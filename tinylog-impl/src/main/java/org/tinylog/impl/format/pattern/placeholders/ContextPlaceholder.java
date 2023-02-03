package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

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
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.CONTEXT);
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
