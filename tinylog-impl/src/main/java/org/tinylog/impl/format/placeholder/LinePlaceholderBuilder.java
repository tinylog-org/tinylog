package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link LinePlaceholder}.
 */
public class LinePlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public LinePlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "line";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for line placeholder: \"{}\"",
                value
            );
        }

        return new LinePlaceholder();
    }

}
