package org.tinylog.impl.format.placeholder;

import java.time.Instant;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

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
    public Placeholder create(TinylogContext context, String value) {
        if ("milliseconds".equals(value)) {
            return new TimestampPlaceholder(Instant::toEpochMilli);
        }

        if (value != null && !value.isEmpty() && !"seconds".equals(value)) {
            context.getLogger().log(
                Level.WARN,
                "Configuration value \"{}\" is an unsupported time unit, only \"seconds\" and \"milliseconds\""
                    + "are supported",
                value
            );
        }

        return new TimestampPlaceholder(Instant::getEpochSecond);
    }

}
