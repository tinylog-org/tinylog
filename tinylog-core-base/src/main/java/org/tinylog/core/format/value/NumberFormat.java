package org.tinylog.core.format.value;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.tinylog.core.Framework;

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
    public String format(Framework framework, String pattern, Object value) {
        Locale locale = framework.getConfiguration().getLocale();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        return new DecimalFormat(pattern, symbols).format(value);
    }

}
