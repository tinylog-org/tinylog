package org.tinylog.core.format.value;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.tinylog.core.internal.LoggingContext;

/**
 * Format for {@link Date}.
 */
public class DateFormat implements ValueFormat {

    /** */
    public DateFormat() {
    }

    @Override
    public boolean isSupported(Object value) {
        return value instanceof Date;
    }

    @Override
    public String format(LoggingContext context, String pattern, Object value) {
        Locale locale = context.getConfiguration().getLocale();
        ZoneId zone = context.getConfiguration().getZone();

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
        dateFormat.setTimeZone(TimeZone.getTimeZone(zone));
        return dateFormat.format(value);
    }

}
