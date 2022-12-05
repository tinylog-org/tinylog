package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

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
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for class name placeholder: \"{}\"",
                value
            );
        }

        return new ClassNamePlaceholder();
    }

}
