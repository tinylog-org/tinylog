package org.tinylog.core.format.value;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.tinylog.core.Framework;

/**
 * Format for date and time classes from {@link java.time}.
 */
public class JavaTimeFormat implements ValueFormat {

    /** */
    public JavaTimeFormat() {
    }

    @Override
    public boolean isSupported(Object value) {
        return value instanceof TemporalAccessor;
    }

    @Override
    public String format(Framework framework, String pattern, Object value) {
        TemporalAccessor accessor = (TemporalAccessor) value;
        if (accessor instanceof Instant) {
            ZoneId zone = framework.getConfiguration().getZone();
            accessor = ZonedDateTime.ofInstant((Instant) accessor, zone);
        }

        Locale locale = framework.getConfiguration().getLocale();
        return DateTimeFormatter.ofPattern(pattern, locale).format(accessor);
    }

}
