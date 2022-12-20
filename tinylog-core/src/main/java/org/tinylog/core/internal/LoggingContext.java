package org.tinylog.core.internal;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;

/**
 * The tinylog context for logging APIs and backends.
 */
public class LoggingContext {

    private final Framework framework;
    private final Configuration configuration;

    /**
     * @param framework The underlying framework instance
     * @param configuration The tinylog configuration to store
     */
    public LoggingContext(Framework framework, Configuration configuration) {
        this.framework = framework;
        this.configuration = configuration;
    }

    /**
     * Gets the actual framework instance.
     *
     * @return The actual framework instance
     */
    public Framework getFramework() {
        return framework;
    }

    /**
     * Gets the stored configuration.
     *
     * @return The stored configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

}
