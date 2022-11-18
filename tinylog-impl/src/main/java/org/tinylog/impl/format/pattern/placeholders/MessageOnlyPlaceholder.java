package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for printing the log message without potential exception of a log entry.
 */
public class MessageOnlyPlaceholder implements Placeholder {

    /** */
    public MessageOnlyPlaceholder() {
    }

    @Override
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.MESSAGE);
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        return entry.getMessage();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String message = entry.getMessage();

        if (message != null) {
            builder.append(message);
        }
    }

}
