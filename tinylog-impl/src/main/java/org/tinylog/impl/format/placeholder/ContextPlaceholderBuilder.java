package org.tinylog.impl.format.placeholder;

import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link ContextPlaceholder}.
 */
public class ContextPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public ContextPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "context";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Thread context key is not defined for context placeholder");
        } else {
            return new ContextPlaceholder(value);
        }
    }

}
