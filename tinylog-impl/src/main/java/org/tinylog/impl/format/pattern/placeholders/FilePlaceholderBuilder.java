package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link FilePlaceholder}.
 */
public class FilePlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public FilePlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(null, "Unexpected configuration value for file placeholder: \"{}\"", value);
        }

        return new FilePlaceholder();
    }

}
