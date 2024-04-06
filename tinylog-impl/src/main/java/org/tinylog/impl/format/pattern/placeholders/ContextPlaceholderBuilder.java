package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.LoggingContext;

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
    public Placeholder create(LoggingContext context, String value) {
        return new ContextPlaceholder(value);
    }
}
