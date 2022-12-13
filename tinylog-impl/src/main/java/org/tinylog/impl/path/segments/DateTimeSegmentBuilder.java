package org.tinylog.impl.path.segments;

import java.util.Locale;

import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link DateTimeSegment}.
 */
public class DateTimeSegmentBuilder implements PathSegmentBuilder {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd_HH-mm-ss";

    /** */
    public DateTimeSegmentBuilder() {
    }

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public PathSegment create(LoggingContext context, String value) throws Exception {
        String pattern = value == null ? DEFAULT_PATTERN : value;
        Locale locale = context.getConfiguration().getLocale();

        if (pattern.indexOf('[') >= 0) {
            throw new IllegalArgumentException(
                "Date-time pattern \" + pattern + \" contains an unsupported optional section"
            );
        }

        return new DateTimeSegment(pattern, locale);
    }

}