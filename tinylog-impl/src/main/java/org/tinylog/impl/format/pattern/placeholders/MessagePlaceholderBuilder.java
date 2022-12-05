package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link MessagePlaceholder}.
 */
public class MessagePlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public MessagePlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "message";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for message placeholder: \"{}\"",
                value
            );
        }

        return new MessagePlaceholder();
    }

}
