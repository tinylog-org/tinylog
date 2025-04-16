package org.tinylog.impl.format.placeholder;

import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link SingleContextPlaceholder} or {@link MultiContextPlaceholder}.
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
            return new MultiContextPlaceholder();
        } else {
            return new SingleContextPlaceholder(value);
        }
    }

}
