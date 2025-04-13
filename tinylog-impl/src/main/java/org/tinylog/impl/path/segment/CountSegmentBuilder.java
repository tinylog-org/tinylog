package org.tinylog.impl.path.segment;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

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
    public PathSegment create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for count path segment: \"{}\"",
                value
            );
        }

        return new CountSegment();
    }

}
