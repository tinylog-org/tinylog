package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving the source method name for a log entry.
 */
public class MethodPlaceholder implements Placeholder {

    /** */
    public MethodPlaceholder() {
    }

    @Override
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.METHOD);
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        return entry.getMethodName();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String methodName = entry.getMethodName();
        builder.append(methodName == null ? "<method unknown>" : methodName);
    }

}
