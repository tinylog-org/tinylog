package org.tinylog.impl.format.pattern.placeholders;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tinylog.impl.LogEntry;

/**
 * Placeholder implementation for resolving all thread context keys and values for a log entry.
 */
public class MultiValueContextPlaceholder extends AbstractContextPlaceholder {

    /**
     * Constructs a MultiValueContextPlaceholder.
     */
    public MultiValueContextPlaceholder() {
    }

    @Override
    public String getValue(LogEntry entry) {
        return contextKeysAndValues(entry);
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        builder.append(contextKeysAndValues(entry));
    }

    /**
     * Creates a string representation of all keys and values in thread context.
     *
     * @param logEntry The log entry whose thread context is to be represented as a string
     * @return A string representation of all keys and values in entry's thread context
     */
    private static String contextKeysAndValues(LogEntry logEntry) {
        Set<Map.Entry<String, String>> contextEntries = logEntry.getContext().entrySet();
        if (contextEntries.isEmpty()) {
            return "";
        }

        return contextEntries.stream()
            .sorted(Map.Entry.comparingByKey())
            .map(mapEntry -> new StringBuilder(mapEntry.getKey()).append("=").append(mapEntry.getValue()))
            .collect(Collectors.joining(", "));
    }

}

