package org.tinylog.impl.format.pattern.styles;

import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Styled placeholder wrapper for replacing empty outputs with a given default value.
 */
public class DefaultValueStyle extends AbstractStylePlaceholder {

    private final String value;

    /**
     * @param placeholder The actual placeholder whose output is to be handled
     * @param value The default value for empty outputs
     */
    public DefaultValueStyle(Placeholder placeholder, String value) {
        super(placeholder);
        this.value = value;
    }

    @Override
    protected void apply(StringBuilder builder, int start) {
        if (builder.length() == start) {
            builder.append(value);
        }
    }

}
