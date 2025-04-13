package org.tinylog.impl.format.placeholder;

import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;

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
    public Placeholder create(TinylogContext context, String value) {
        if (value != null) {
            context.getLogger().log(
                Level.WARN,
                "Unexpected configuration value for process ID placeholder: \"{}\"",
                value
            );
        }

        long processId = context.getRuntime().getProcessId();
        return new ProcessIdPlaceholder(processId);
    }

}
