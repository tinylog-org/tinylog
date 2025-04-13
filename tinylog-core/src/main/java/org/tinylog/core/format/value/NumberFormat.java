package org.tinylog.core.format.value;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.tinylog.core.Configuration;

/**
 * Format for numbers.
 */
public class NumberFormat implements ValueFormat {

    /** */
    public NumberFormat() {
    }

    @Override
    public boolean isSupported(Object value) {
        return value instanceof Number;
    }

    @Override
    public String format(Configuration configuration, String pattern, Object value) {
        Locale locale = configuration.getLocale();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        return new DecimalFormat(pattern, symbols).format(value);
    }

}
