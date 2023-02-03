package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving the assigned tag of a log entry.
 */
public class TagPlaceholder implements Placeholder {

    /** */
    public TagPlaceholder() {
    }

    @Override
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.TAG);
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
