package org.tinylog.core.backend;

import java.time.Clock;
import java.time.Instant;

import org.tinylog.core.Configuration;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Immutable tinylog context for logging backends.
 */
public class TinylogContext {

    private final ClassLoader loader;
    private final Clock clock;
    private final RuntimeFlavor runtime;
    private final Configuration configuration;
    private final InternalLogger logger;

    /**
     * @param loader The class loader to use for loading service implementations
     * @param clock The clock to use for receiving the current {@link Instant}
     * @param runtime The runtime flavor to use for non-standardized operations
     * @param configuration The current tinylog configuration
     * @param logger The internal logger instance for issuing internal tinylog log entries
     */
    public TinylogContext(
        ClassLoader loader,
        Clock clock,
        RuntimeFlavor runtime,
        Configuration configuration,
        InternalLogger logger
    ) {
        this.loader = loader;
        this.clock = clock;
        this.runtime = runtime;
        this.configuration = configuration;
        this.logger = logger;
    }

    /**
     * Provides the class loader to use for loading service implementations.
     *
     * @return The class loader for loading service implementations
     */
    public ClassLoader getLoader() {
        return loader;
    }

    /**
     * Provides the clock to use for receiving the current {@link Instant}.
     *
     * @return The clock to use for receiving the current instant
     */
    public Clock getClock() {
        return clock;
    }

    /**
     * Provides the {@link RuntimeFlavor} for the current virtual machine.
     *
     * @return The runtime flavor instance for the current virtual machine
     */
    public RuntimeFlavor getRuntime() {
        return runtime;
    }

    /**
     * Provides the current tinylog configuration.
     *
     * @return The current tinylog configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Provides the internal logger instance for issuing internal tinylog log entries.
     *
     * @return The internal logger for internal tinylog log entries
     */
    public InternalLogger getLogger() {
        return logger;
    }

    /**
     * Creates a new context for another configuration.
     *
     * <p>
     *     All field from the current context are copied, but the configuration is replaced.
     * </p>
     *
     * @param configuration The new configuration for the new context
     * @return The newly created context
     */
    public TinylogContext withConfiguration(Configuration configuration) {
        return new TinylogContext(
            loader,
            clock,
            runtime,
            configuration,
            logger
        );
    }

}
