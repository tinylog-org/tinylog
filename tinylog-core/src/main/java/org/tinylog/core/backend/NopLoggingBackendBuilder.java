package org.tinylog.core.backend;

import org.tinylog.core.internal.LoggingContext;

/**
 * Builder for {@link NopLoggingBackend}.
 */
public class NopLoggingBackendBuilder implements LoggingBackendBuilder {

    private static final NopLoggingBackend PROVIDER = new NopLoggingBackend();

    /** */
    public NopLoggingBackendBuilder() {
    }

    @Override
    public String getName() {
        return "nop";
    }

    @Override
    public LoggingBackend create(LoggingContext context) {
        return PROVIDER;
    }

}
