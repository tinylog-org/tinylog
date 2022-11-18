package org.tinylog.impl.format.pattern.placeholders;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;

/**
 * Bundle of multiple child placeholders.
 *
 * <p>
 *     This bundle placeholder combines the render result of multiple child placeholders. All child placeholders are
 *     rendered in the order in which they have been passed.
 * </p>
 */
public class BundlePlaceholder implements Placeholder {

    private final List<Placeholder> placeholders;

    /**
     * @param placeholders Child placeholders
     */
    public BundlePlaceholder(List<Placeholder> placeholders) {
        this.placeholders = new ArrayList<>(placeholders);
    }

    @Override
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        Set<LogEntryValue> requiredValues = EnumSet.noneOf(LogEntryValue.class);
        placeholders.forEach(placeholder -> requiredValues.addAll(placeholder.getRequiredLogEntryValues()));
        return requiredValues;
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        StringBuilder builder = new StringBuilder();
        render(builder, entry);
        return builder.toString();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        placeholders.forEach(placeholder -> placeholder.render(builder, entry));
    }

}
