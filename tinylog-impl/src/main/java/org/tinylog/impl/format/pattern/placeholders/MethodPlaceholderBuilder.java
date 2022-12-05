package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link MethodPlaceholder}.
 */
public class MethodPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public MethodPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "method";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for method placeholder: \"{}\"",
                value
            );
        }

        return new MethodPlaceholder();
    }

}
