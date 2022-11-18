package org.tinylog.core.format.value;

import java.util.Locale;

/**
 * Builder for creating an instance of {@link NumberFormat}.
 */
public class NumberFormatBuilder implements ValueFormatBuilder {

    /** */
    public NumberFormatBuilder() {
    }

    @Override
    public NumberFormat create(Locale locale) {
        return new NumberFormat(locale);
    }

}
