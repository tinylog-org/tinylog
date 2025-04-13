package org.tinylog.impl.format.style;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.impl.format.placeholder.Placeholder;
import org.tinylog.impl.format.placeholder.ValueType;

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
    public OutputDetails getOutputDetails() {
        return placeholder.getOutputDetails();
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
