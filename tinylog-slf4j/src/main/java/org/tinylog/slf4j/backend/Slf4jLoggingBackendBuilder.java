package org.tinylog.slf4j.backend;

import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for {@link Slf4jLoggingBackend}.
 */
public class Slf4jLoggingBackendBuilder implements LoggingBackendBuilder {

    /** */
    public Slf4jLoggingBackendBuilder() {
    }

    @Override
    public String getName() {
        return "slf4f";
    }

    @Override
    public LoggingBackend create(TinylogContext context) {
        return new Slf4jLoggingBackend(context);
    }

}
