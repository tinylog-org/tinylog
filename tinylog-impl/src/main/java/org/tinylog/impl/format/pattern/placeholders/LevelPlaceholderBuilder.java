package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating an instance of {@link LevelPlaceholder}.
 */
public class LevelPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public LevelPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "level";
    }

    @Override
    public Placeholder create(Framework framework, String value) {
        if (value != null) {
            InternalLogger.warn(null, "Unexpected configuration value for level placeholder: \"{}\"", value);
        }

        return new LevelPlaceholder();
    }

}
