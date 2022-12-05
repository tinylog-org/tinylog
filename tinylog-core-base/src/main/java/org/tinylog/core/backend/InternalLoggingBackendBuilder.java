package org.tinylog.core.backend;

import org.tinylog.core.Framework;

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
    public LoggingBackend create(Framework framework) {
        return new InternalLoggingBackend(framework);
    }

}
