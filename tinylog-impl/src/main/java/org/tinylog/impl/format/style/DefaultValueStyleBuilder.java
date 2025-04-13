package org.tinylog.impl.format.style;

import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.placeholder.Placeholder;

/**
 * Builder for creating an instance of {@link DefaultValueStyle}.
 */
public class DefaultValueStyleBuilder implements StyleBuilder {

    /** */
    public DefaultValueStyleBuilder() {
    }

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public Placeholder create(TinylogContext context, Placeholder placeholder, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Default value is not defined for default value style");
        } else {
            return new DefaultValueStyle(placeholder, value);
        }
    }

}
