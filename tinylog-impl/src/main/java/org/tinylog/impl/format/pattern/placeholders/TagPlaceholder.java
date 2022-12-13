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

    private final String defaultReturnValue;
    private final String defaultRenderValue;

    /**
     * @param defaultReturnValue The default value to return by value getter, if there is no assigned tag for a
     *                             passed log entry
     * @param defaultRenderValue The default value to append to string builders, if there is no assigned tag for a
     *                           passed log entry
     */
    public TagPlaceholder(String defaultReturnValue, String defaultRenderValue) {
        this.defaultReturnValue = defaultReturnValue;
        this.defaultRenderValue = defaultRenderValue;
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
        String tag = entry.getTag();
        return tag == null ? defaultReturnValue : tag;
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        String tag = entry.getTag();
        builder.append(tag == null ? defaultRenderValue : tag);
    }

}