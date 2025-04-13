package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link ThreadPlaceholder}.
 */
public class ThreadPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public ThreadPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "thread";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for thread placeholder: \"{}\"",
                value
            );
        }

        return new ThreadPlaceholder();
    }

}
