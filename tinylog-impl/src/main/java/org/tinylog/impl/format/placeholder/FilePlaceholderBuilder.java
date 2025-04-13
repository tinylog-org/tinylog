package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link FilePlaceholder}.
 */
public class FilePlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public FilePlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for file placeholder: \"{}\"",
                value
            );
        }

        return new FilePlaceholder();
    }

}
