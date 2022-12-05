package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

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
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for package placeholder: \"{}\"",
                value
            );
        }

        return new PackagePlaceholder();
    }

}
