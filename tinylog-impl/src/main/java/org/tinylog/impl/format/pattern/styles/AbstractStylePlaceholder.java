package org.tinylog.impl.format.pattern.styles;

import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Style wrapper for a {@link Placeholder}.
 */
public abstract class AbstractStylePlaceholder implements Placeholder {

    private final Placeholder placeholder;

    /**
     * @param placeholder The actual placeholder to style
     */
    public AbstractStylePlaceholder(Placeholder placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        return placeholder.getRequiredLogEntryValues();
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        ValueType originType = placeholder.getType();
        Object originValue = placeholder.getValue(entry);

        if (originValue == null) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();

            if (originType == ValueType.STRING) {
                builder.append(originValue);
            } else {
                placeholder.render(builder, entry);
            }

            apply(builder, 0);

            return builder.toString();
        }
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        int start = builder.length();
        placeholder.render(builder, entry);
        apply(builder, start);
    }

    /**
     * Applies the style to a {@link StringBuilder} that contains the output from the wrapped placeholder at the passed
     * index.
     *
     * @param builder The string builder that contains the output from the wrapped placeholder
     * @param start The index position, where the output from the wrapped placeholder starts
     */
    protected abstract void apply(StringBuilder builder, int start);

}
