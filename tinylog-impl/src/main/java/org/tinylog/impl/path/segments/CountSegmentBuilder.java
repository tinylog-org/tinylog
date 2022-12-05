package org.tinylog.impl.path.segments;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link CountSegment}.
 */
public class CountSegmentBuilder implements PathSegmentBuilder {

    /** */
    public CountSegmentBuilder() {
    }

    @Override
    public String getName() {
        return "count";
    }

    @Override
    public PathSegment create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for count path segment: \"{}\"",
                value
            );
        }

        return new CountSegment();
    }

}
