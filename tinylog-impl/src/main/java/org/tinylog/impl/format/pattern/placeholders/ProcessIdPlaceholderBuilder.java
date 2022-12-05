package org.tinylog.impl.format.pattern.placeholders;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for creating an instance of {@link ProcessIdPlaceholder}.
 */
public class ProcessIdPlaceholderBuilder implements PlaceholderBuilder {

    /** */
    public ProcessIdPlaceholderBuilder() {
    }

    @Override
    public String getName() {
        return "process-id";
    }

    @Override
    public Placeholder create(LoggingContext context, String value) {
        if (value != null) {
            InternalLogger.warn(
                null,
                "Unexpected configuration value for process ID placeholder: \"{}\"",
                value
            );
        }

        long processId = context.getFramework().getRuntime().getProcessId();
        return new ProcessIdPlaceholder(processId);
    }

}
