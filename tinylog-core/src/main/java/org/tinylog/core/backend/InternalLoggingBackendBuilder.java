package org.tinylog.core.backend;

import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for {@link InternalLoggingBackend}.
 */
public class InternalLoggingBackendBuilder implements LoggingBackendBuilder {

    /** */
    public InternalLoggingBackendBuilder() {
    }

    @Override
    public String getName() {
        return "internal";
    }

    @Override
    public LoggingBackend create(LoggingContext context) {
        return new InternalLoggingBackend(context);
    }

}
