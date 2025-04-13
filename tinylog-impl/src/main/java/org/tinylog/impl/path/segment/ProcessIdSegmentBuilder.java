package org.tinylog.impl.path.segment;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of {@link StaticPathSegment} with the process ID of the current process.
 */
public class ProcessIdSegmentBuilder implements PathSegmentBuilder {

    /** */
    public ProcessIdSegmentBuilder() {
    }

    @Override
    public String getName() {
        return "process-id";
    }

    @Override
    public PathSegment create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for process ID path segment: \"{}\"",
                value
            );
        }

        long processId = context.getRuntime().getProcessId();
        return new StaticPathSegment(Long.toString(processId));
    }

}
