package org.tinylog.impl.format.pattern.placeholders;

import java.time.Instant;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link TimestampPlaceholder}.
 */
public class TimestampPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public TimestampPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "timestamp";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        if ("milliseconds".equals(value)) {
            return new TimestampPlaceholder(Instant::toEpochMilli);
        }

        if (value != null && !value.isEmpty() && !"seconds".equals(value)) {
            InternalLogger.warn(
                null,
                "Configuration value \"{}\" is an unsupported time unit, only \"seconds\" and \"milliseconds\""
                    + "are supported",
                value
            );
        }

        return new TimestampPlaceholder(Instant::getEpochSecond);
    }

}
