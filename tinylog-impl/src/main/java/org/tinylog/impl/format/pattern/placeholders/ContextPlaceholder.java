package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving thread context values for a log entry.
 */
public class ContextPlaceholder implements Placeholder {

    private final String key;

    /**
     * Creates a ContextPlaceholder without a thread context key.
     */
    public ContextPlaceholder() {
        this(null);
    }

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
        return key == null ? contextKeysAndValuesOrDefault(entry, null) : entry.getContext().get(key);
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        if (key != null) {
            String value = entry.getContext().getOrDefault(key, "");
            builder.append(value);
        } else {
            builder.append(contextKeysAndValuesOrDefault(entry, ""));
        }
    }

    /**
     * Creates a string representation of all keys and values in thread context.
     *
     * @param entry the log entry whose thread context is to be represented as a string
     * @param defaultValue the value to return if the thread context has no keys or values
     * @return a string representation of all keys and values in entry's thread context
     */
    private static String contextKeysAndValuesOrDefault(LogEntry entry, String defaultValue) {
        Set<Map.Entry<String, String>> contextEntries = entry.getContext().entrySet();
        if (contextEntries.isEmpty()) {
            return defaultValue;
        }

        return contextEntries.stream()
            .sorted(Map.Entry.comparingByKey())
            .map(me -> new StringBuilder().append(me.getKey()).append("=").append(me.getValue()))
            .collect(Collectors.joining(", "));
    }

}
