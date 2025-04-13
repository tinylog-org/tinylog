package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link PackagePlaceholder}.
 */
public class PackagePlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public PackagePlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "package";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for package placeholder: \"{}\"",
                value
            );
        }

        return new PackagePlaceholder();
    }

}
