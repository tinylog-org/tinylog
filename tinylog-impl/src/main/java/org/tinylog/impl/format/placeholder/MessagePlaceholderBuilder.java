package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

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
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for message placeholder: \"{}\"",
                value
            );
        }

        return new MessagePlaceholder(context.getConfiguration());
    }

}
