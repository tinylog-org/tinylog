package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link ClassPlaceholder}.
 */
public class ClassPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public ClassPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "class";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for class placeholder: \"{}\"",
                value
            );
        }

        return new ClassPlaceholder();
    }

}
