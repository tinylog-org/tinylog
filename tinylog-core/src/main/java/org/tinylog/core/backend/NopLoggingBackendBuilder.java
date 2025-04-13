package org.tinylog.core.backend;

/**
 * Builder for {@link NopLoggingBackend}.
 */
public class NopLoggingBackendBuilder implements LoggingBackendBuilder {

    /** */
    public NopLoggingBackendBuilder() {
    }

    @Override
    public String getName() {
        return "nop";
    }

    @Override
    public LoggingBackend create(TinylogContext context) {
        return new NopLoggingBackend();
    }

}
