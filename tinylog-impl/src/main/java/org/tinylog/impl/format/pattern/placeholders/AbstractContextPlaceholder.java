package org.tinylog.impl.format.pattern.placeholders;

import java.util.EnumSet;
import java.util.Set;

import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Placeholder implementation for resolving thread context values for a log entry.
 */
public abstract class AbstractContextPlaceholder implements Placeholder {

    @Override
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.CONTEXT);
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }
}
