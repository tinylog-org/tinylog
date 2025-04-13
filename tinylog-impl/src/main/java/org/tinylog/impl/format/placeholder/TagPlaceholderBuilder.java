package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

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
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for tag placeholder: \"{}\"",
                value
            );
        }

        return new TagPlaceholder();
    }

}
