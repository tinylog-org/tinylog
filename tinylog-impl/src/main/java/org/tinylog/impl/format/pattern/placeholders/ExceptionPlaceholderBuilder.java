package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

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
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for exception placeholder: \"{}\"",
                value
            );
        }

        return new ExceptionPlaceholder();
    }

}
