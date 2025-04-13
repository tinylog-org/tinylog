package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link ThreadIdPlaceholder}.
 */
public class ThreadIdPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public ThreadIdPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "thread-id";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for thread ID placeholder: \"{}\"",
                value
            );
        }

        return new ThreadIdPlaceholder();
    }

}
