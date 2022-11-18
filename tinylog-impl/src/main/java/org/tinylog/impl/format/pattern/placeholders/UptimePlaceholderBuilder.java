package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.Framework;

/**
 * Builder for creating a {@link UptimePlaceholder}.
 */
public class UptimePlaceholderBuilder implements PlaceholderBuilder {

    private static final String DEFAULT_PATTERN = "HH:mm:ss";

    /** */
    public UptimePlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "uptime";
    }

    @Override
    public Placeholder create(Framework framework, String value) {
        if (value == null) {
            return new UptimePlaceholder(DEFAULT_PATTERN, false);
        } else {
            return new UptimePlaceholder(value, true);
        }
    }

}
