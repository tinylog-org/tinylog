package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link ExceptionPlaceholder}.
 */
public class ExceptionPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public ExceptionPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "exception";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for exception placeholder: \"{}\"",
                value
            );
        }

        return new ExceptionPlaceholder();
    }

}
