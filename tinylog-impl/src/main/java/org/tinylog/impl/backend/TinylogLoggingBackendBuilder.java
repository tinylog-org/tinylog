package org.tinylog.impl.backend;

import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for {@link TinylogLoggingBackend}.
 */
public class TinylogLoggingBackendBuilder implements LoggingBackendBuilder {

    /** */
    public TinylogLoggingBackendBuilder() {
    }

    @Override
    public String getName() {
        return "tinylog";
    }

    @Override
    public LoggingBackend create(TinylogContext context) {
        return new TinylogLoggingBackend(context);
    }

}
