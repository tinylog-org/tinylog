package org.tinylog.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import org.tinylog.core.backend.LevelVisibility;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendFactory;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.core.context.ContextStorage;
import org.tinylog.core.internal.AsynchronousTaskExecutor;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.loader.ConfigurationLoader;
import org.tinylog.core.runtime.RuntimeBuilder;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Core tinylog framework for handling the entire life cycle.
 */
public class Framework {

    private static final int TASK_CAPACITY = 1024;

    private final Object mutex;
    private final AsynchronousTaskExecutor executor;
    private final InternalLogger logger;
    private final ClassLoader loader;
    private final RuntimeFlavor runtime;

    private boolean frozen;
    private Configuration configuration;
    private LoggingBackend backend;

    /** */
    public Framework() {
        mutex = new Object();
        executor = new AsynchronousTaskExecutor(TASK_CAPACITY);
        logger = new InternalLogger(executor);
        loader = getClassLoader();
        runtime = RuntimeBuilder.load(loader).create(logger);

        frozen = false;
        configuration = loadConfiguration(loader, logger);
    }

    /**
     * Gets the internal logger for issuing log entries within tinylog.
     *
     * @return The internal logger for tinylog
     */
    public InternalLogger getInternalLogger() {
        return logger;
    }

    /**
     * Gets the class loader for loading resources and services from the classpath.
     *
     * @return A valid and existing class loader instance
     */
    public ClassLoader getClassLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader == null ? Framework.class.getClassLoader() : loader;
    }

    /**
     * Provides the correct {@link RuntimeFlavor} for the current virtual machine.
     *
     * @return The runtime flavor instance for the current virtual machine
     */
    public RuntimeFlavor getRuntime() {
        return runtime;
    }

    /**
     * Retrieves the thread-based context value storage.
     *
     * @return The storage for thread-based context values
     */
    public ContextStorage getContextStorage() {
        if (backend == null) {
            start();
        }

        return backend.getContextStorage();
    }

    /**
     * Retrieves the visibility of all severity levels for a fully-qualified class name. Log entries whose severity
     * levels are set to {@link OutputDetails#DISABLED} do not need to be created since they are never output.
     *
     * @param className The fully-qualified class name for which the visibility of severity levels is requested
     * @return The visibilities of all severity levels
     */
    public LevelVisibility getLevelVisibilityByClass(String className) {
        if (backend == null) {
            start();
        }

        return backend.getLevelVisibilityByClass(className);
    }

    /**
     * Retrieves the visibility of all severity levels for a category tag. Log entries whose severity levels are set to
     * {@link OutputDetails#DISABLED} do not need to be created since they are never output.
     *
     * @param tag The category tag for which the visibility of severity levels is requested
     * @return The visibilities of all severity levels
     */
    public LevelVisibility getLevelVisibilityByTag(String tag) {
        if (backend == null) {
            start();
        }

        return backend.getLevelVisibilityByTag(tag);
    }

    /**
     * Checks if a severity level is enabled for outputting log entries.
     *
     * @param location The location information of the caller
     * @param tag The category tag
     * @param level The severity level to check
     * @return {@code true} if log entries of the passed severity level will be output, {@code false} if not
     */
    public boolean isEnabled(Object location, String tag, Level level) {
        if (backend == null) {
            start();
        }

        return backend.isEnabled(location, tag, level);
    }

    /**
     * Submits a new log entry that should be processed by the active {@link LoggingBackend}.
     *
     * @param entry A newly issued log entry
     */
    public void submit(LogEntry entry) {
        Task task = (backend, last) -> backend.output(entry, last);
        executor.enqueue(task);
    }

    /**
     * Initializes this framework.
     */
    public void start() {
        synchronized (mutex) {
            if (backend == null) {
                frozen = true;
                backend = new LoggingBackendFactory(loader, runtime, logger).create(configuration);
                executor.start(backend);
            }
        }
    }

    /**
     * Shuts this framework down.
     *
     * @throws InterruptedException If the current thread is interrupted while waiting for the successful shutdown
     */
    public void stop() throws InterruptedException {
        synchronized (mutex) {
            executor.stop();
        }
    }

    /**
     * Creates a {@link ConfigurationBuilder} for changing the current configuration.
     *
     * @param inherit {@code true} for initializing the {@link ConfigurationBuilder} with the current configuration,
     *                {@code false} for creating an empty {@link ConfigurationBuilder}
     * @return A new configuration builder instance
     */
    ConfigurationBuilder getConfigurationBuilder(boolean inherit) {
        if (inherit) {
            return new ConfigurationBuilder(this, configuration.getAllValues(), logger);
        } else {
            return new ConfigurationBuilder(this, Collections.emptyMap(), logger);
        }
    }

    /**
     * Applies a new configuration.
     *
     * @param configuration The new configuration
     * @throws UnsupportedOperationException If another configuration has already been applied and cannot be overridden
     *                                       anymore
     */
    void setConfiguration(Configuration configuration) {
        synchronized (mutex) {
            if (frozen) {
                throw new UnsupportedOperationException("Another configuration has already been applied and cannot be"
                    + " overridden anymore");
            } else {
                this.configuration = configuration;
            }
        }
    }

    /**
     * Loads the configuration via a registered {@link ConfigurationLoader}.
     *
     * @param classLoader The class loader to use for loading service implementations and resource files
     * @param logger The internal logger instance for issuing internal tinylog log entries
     * @return The initial configuration
     */
    private static Configuration loadConfiguration(ClassLoader classLoader, InternalLogger logger) {
        Iterable<ConfigurationLoader> loaders = ServiceLoader.load(ConfigurationLoader.class, classLoader);

        Map<String, String> properties = StreamSupport.stream(loaders.spliterator(), false)
            .sorted(Comparator.comparingInt(ConfigurationLoader::getPriority).reversed())
            .map(loader -> loader.load(classLoader, logger))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(Collections.emptyMap());

        return new Configuration(properties, logger);
    }

}
