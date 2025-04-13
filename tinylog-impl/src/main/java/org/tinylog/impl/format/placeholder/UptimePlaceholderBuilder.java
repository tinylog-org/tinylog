package org.tinylog.impl.format.placeholder;

import org.tinylog.core.backend.TinylogContext;

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
    public Placeholder create(TinylogContext context, String value) {
        if (value == null) {
            return new UptimePlaceholder(context.getRuntime(), DEFAULT_PATTERN, false);
        } else {
            return new UptimePlaceholder(context.getRuntime(), value, true);
        }
    }

}
