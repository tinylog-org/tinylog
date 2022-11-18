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
    private final String defaultReturnValue;
    private final String defaultRenderValue;

    /**
     * @param key The key of the thread context value to output
     * @param defaultReturnValue The default value to return by getter, if there is no value stored for the passed key
     * @param defaultRenderValue The default value to append to string builders, if there is no value stored for the
     *                           passed key
     */
    public ContextPlaceholder(String key, String defaultReturnValue, String defaultRenderValue) {
        this.key = key;
        this.defaultReturnValue = defaultReturnValue;
        this.defaultRenderValue = defaultRenderValue;
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
        return entry.getContext().getOrDefault(key, defaultReturnValue);
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String value = entry.getContext().getOrDefault(key, defaultRenderValue);
        builder.append(value);
    }

}
