package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

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
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(null, "Unexpected configuration value for class placeholder: \"{}\"", value);
        }

        return new ClassPlaceholder();
    }

}
