package org.tinylog.core.format.value;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Format for {@link Date}.
 */
public class DateFormat implements ValueFormat {

    private final Locale locale;

    /**
     * @param locale Locale for language or country depending format outputs
     */
    DateFormat(Locale locale) {
        this.locale = locale;
    }

    @Override
    public boolean isSupported(final Object value) {
        return value instanceof Date;
    }

    @Override
    public String format(final String pattern, final Object value) {
        return new SimpleDateFormat(pattern, locale).format(value);
    }

}
