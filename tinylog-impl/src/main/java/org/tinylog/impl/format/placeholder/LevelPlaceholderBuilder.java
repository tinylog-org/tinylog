package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link LevelPlaceholder}.
 */
public class LevelPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public LevelPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "level";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for level placeholder: \"{}\"",
                value
            );
        }

        return new LevelPlaceholder();
    }

}
