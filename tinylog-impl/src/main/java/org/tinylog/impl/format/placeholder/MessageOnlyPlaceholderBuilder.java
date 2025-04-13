package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

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
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for message only placeholder: \"{}\"",
                value
            );
        }

        return new MessageOnlyPlaceholder(context.getConfiguration());
    }

}
