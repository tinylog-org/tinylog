package org.tinylog.impl.format.pattern.placeholders;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link DatePlaceholder}.
 */
public class DatePlaceholderBuilder implements PlaceholderBuilder {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** */
    public DatePlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        String pattern = value == null ? DEFAULT_PATTERN : value;
        Locale locale = context.getConfiguration().getLocale();
        ZoneId zone = context.getConfiguration().getZone();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
            return new DatePlaceholder(formatter.withZone(zone), value != null);
        } catch (IllegalArgumentException ex) {
            InternalLogger.error(ex, "Invalid date-time pattern: \"{}\"", pattern);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_PATTERN, locale);
            return new DatePlaceholder(formatter.withZone(zone), false);
        }
    }

}
