package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link ClassNamePlaceholder}.
 */
public class ClassNamePlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public ClassNamePlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "class-name";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for class name placeholder: \"{}\"",
                value
            );
        }

        return new ClassNamePlaceholder();
    }

}
