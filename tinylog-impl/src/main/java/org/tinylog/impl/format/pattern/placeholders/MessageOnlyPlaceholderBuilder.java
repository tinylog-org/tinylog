package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link MessageOnlyPlaceholder}.
 */
public class MessageOnlyPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public MessageOnlyPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "message-only";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for message only placeholder: \"{}\"",
                value
            );
        }

        return new MessageOnlyPlaceholder();
    }

}
