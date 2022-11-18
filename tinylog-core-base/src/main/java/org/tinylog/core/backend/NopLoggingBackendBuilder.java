package org.tinylog.core.backend;

import org.tinylog.core.Framework;

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
    public LoggingBackend create(Framework framework) {
        return PROVIDER;
    }

}
