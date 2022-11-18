package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

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
    public Placeholder create(Framework framework, String value) {
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
