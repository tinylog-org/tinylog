package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link LinePlaceholder}.
 */
public class LinePlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public LinePlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "line";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(null, "Unexpected configuration value for line placeholder: \"{}\"", value);
        }

        return new LinePlaceholder();
    }

}
