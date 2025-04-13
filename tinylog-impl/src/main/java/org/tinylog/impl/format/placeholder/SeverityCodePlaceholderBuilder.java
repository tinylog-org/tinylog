package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link SeverityCodePlaceholder}.
 */
public class SeverityCodePlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public SeverityCodePlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "severity-code";
    }

    @Override
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for severity code placeholder: \"{}\"",
                value
            );
        }

        return new SeverityCodePlaceholder();
    }

}
