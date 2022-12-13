package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link ThreadPlaceholder}.
 */
public class ThreadPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public ThreadPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "thread";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for thread placeholder: \"{}\"",
                value
            );
        }

        return new ThreadPlaceholder();
    }

}