package org.tinylog.impl.path.segments;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

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
    public PathSegment create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for process ID path segment: \"{}\"",
                value
            );
        }

        long processId = context.getFramework().getRuntime().getProcessId();
        return new StaticPathSegment(Long.toString(processId));
    }

}
