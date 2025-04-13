package org.tinylog.core.backend;

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
    public LoggingBackend create(TinylogContext context) {
        return new InternalLoggingBackend(context.getConfiguration());
    }

}
