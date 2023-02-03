package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link TagPlaceholder}.
 */
public class TagPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public TagPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "tag";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for tag placeholder: \"{}\"",
                value
            );
        }

        return new TagPlaceholder();
    }

}
