package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

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
    public Placeholder create(Framework framework, String value) {
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
