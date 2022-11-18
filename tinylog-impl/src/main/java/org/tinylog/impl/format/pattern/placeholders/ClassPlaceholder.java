package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving the fully-qualified class name for a log entry.
 */
public class ClassPlaceholder implements Placeholder {

    /** */
    public ClassPlaceholder() {
    }

    @Override
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.CLASS);
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        return entry.getClassName();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String className = entry.getClassName();
        builder.append(className == null ? "<class unknown>" : className);
    }

}
