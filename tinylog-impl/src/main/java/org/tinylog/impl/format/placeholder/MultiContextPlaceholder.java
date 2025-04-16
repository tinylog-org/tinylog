package org.tinylog.impl.format.placeholder;

import java.util.Iterator;
import java.util.Map;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Placeholder implementation for resolving all thread context keys and values for a log entry.
 */
public class MultiContextPlaceholder implements Placeholder {

    /** */
    public MultiContextPlaceholder() {
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
        if (entry.getContext().isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        render(builder, entry);
        return builder.toString();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        Iterator<Map.Entry<String, String>> iterator = entry.getContext()
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .iterator();

        if (iterator.hasNext()) {
            while (true) {
                Map.Entry<String, String> contextEntry = iterator.next();
                builder.append(contextEntry.getKey());
                builder.append('=');
                builder.append(contextEntry.getValue());

                if (iterator.hasNext()) {
                    builder.append(", ");
                } else {
                    break;
                }
            }
        }
    }

}
