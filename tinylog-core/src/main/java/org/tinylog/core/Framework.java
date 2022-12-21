package org.tinylog.core;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

import org.tinylog.core.backend.BundleLoggingBackend;
import org.tinylog.core.backend.InternalLoggingBackend;
import org.tinylog.core.backend.InternalLoggingBackendBuilder;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.backend.NopLoggingBackendBuilder;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.internal.LoggingContext;
import org.tinylog.core.internal.SafeServiceLoader;
import org.tinylog.core.loader.ConfigurationLoader;
import org.tinylog.core.runtime.RuntimeBuilder;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Storage for {@link Configuration}, registered {@link Hook} instances, and {@link LoggingBackend}.
 */
public class Framework {

    private static final String FROZEN_MESSAGE =
        "The configuration has already been applied and cannot be modified anymore";

    private final Object mutex = new Object();

    private final RuntimeFlavor runtime;
    private final Collection<Hook> hooks;

    private volatile Configuration configuration;
    private volatile LoggingBackend loggingBackend;

    private boolean frozen;
    private boolean running;

    /**
     * Loads the configuration from default properties file and hooks from service files.
     *
     * @param loadConfiguration {@code true} to load the configuration from found properties file, {@code false} to
     *                          keep the configuration empty
     * @param loadHooks {@code true} to load all hooks that are registered as services, {@code false} to do not load
     *                  any hooks
     */
    public Framework(boolean loadConfiguration, boolean loadHooks) {
        this.runtime = createRuntime();
        this.configuration = loadConfiguration ? loadConfiguration() : new Configuration(Collections.emptyMap());
        this.hooks = loadHooks ? loadHooks() : new ArrayList<>();
    }

    /**
     * Gets the class loader for loading resources and services from the classpath.
     *
     * @return A valid and existing class loader instance
     */
    public ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader == null ? Framework.class.getClassLoader() : classLoader;
    }

    /**
     * Gets the clock for getting the current date, time, and zone.
     *
     * @return A working clock
     */
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }

    /**
     * Provides the appropriate {@link RuntimeFlavor} for the actual virtual machine.
     *
     * @return The appropriate runtime instance
     */
    public RuntimeFlavor getRuntime() {
        return runtime;
    }

    /**
     * Creates a {@link ConfigurationBuilder} for changing the current configuration.
     *
     * @param inherit {@code true} for initializing the {@link ConfigurationBuilder} with the current configuration,
     *                {@code false} for creating an empty {@link ConfigurationBuilder}
     * @return A new configuration builder instance
     */
    public ConfigurationBuilder getConfigurationBuilder(boolean inherit) {
        if (inherit) {
            return new ConfigurationBuilder(this, configuration.getAllValues());
        } else {
            return new ConfigurationBuilder(this, Collections.emptyMap());
        }
    }

    /**
     * Gets the logging backend from the stored configuration.
     *
     * @return The logging backend implementation
     */
    public LoggingBackend getLoggingBackend() {
        if (loggingBackend == null) {
            synchronized (mutex) {
                if (loggingBackend == null) {
                    loadLoggingBackend();
                }
            }
        }

        return loggingBackend;
    }

    /**
     * Registers a new {@link Hook}.
     *
     * @param hook Hook to register
     */
    public void registerHook(Hook hook) {
        synchronized (mutex) {
            hooks.add(hook);

            if (running) {
                InternalLogger.debug(null, "Start hook {}", hook.getClass().getName());
                SafeServiceLoader.execute(hook, "start", Hook::startUp);
            }
        }
    }

    /**
     * Removes a registered {@link Hook}.
     *
     * @param hook Hook to unregister
     */
    public void removeHook(Hook hook) {
        synchronized (mutex) {
            hooks.remove(hook);
        }
    }

    /**
     * Starts the framework and calls the start-up method on all registered hooks, if the framework is not yet started.
     */
    public void startUp() {
        synchronized (mutex) {
            if (!running) {
                running = true;

                for (Hook hook : hooks) {
                    InternalLogger.debug(null, "Start hook {}", hook.getClass().getName());
                    SafeServiceLoader.execute(hook, "start", Hook::startUp);
                }

                loadLoggingBackend();

                if (!"false".equalsIgnoreCase(configuration.getValue("auto-shutdown"))) {
                    Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown, "tinylog-shutdown-thread"));
                }

                InternalLogger.debug(null, "Logging framework is up");
            }
        }
    }

    /**
     * Stops the framework and calls the shutdown method on all registered hooks, if the framework is not yet shut
     * down.
     */
    public void shutDown() {
        synchronized (mutex) {
            if (running) {
                running = false;

                InternalLogger.debug(null, "Logging framework is shutting down");

                for (Hook hook : hooks) {
                    InternalLogger.debug(null, "Shut down hook {}", hook.getClass().getName());
                    SafeServiceLoader.execute(hook, "shut down", Hook::shutDown);
                }

                loggingBackend = null;
                InternalLogger.reset();
            }
        }
    }

    /**
     * Replaces the current configuration with the new passed configuration.
     *
     * <p>
     *     The configuration can be replaced as needed before issuing any log entries. As soon as the first log entry is
     *     issued, the configuration becomes frozen and can no longer be replaced anymore.
     * </p>
     *
     * @param configuration The new configuration
     * @throws UnsupportedOperationException The current configuration has already been applied and cannot be replaced
     */
    void setConfiguration(Configuration configuration) {
        synchronized (mutex) {
            if (frozen) {
                throw new UnsupportedOperationException(FROZEN_MESSAGE);
            } else {
                this.configuration = configuration;
            }
        }
    }

    /**
     * Creates a new {@link LoggingBackend}.
     *
     * @param context The current logging context
     * @return The newly created logging backend instance
     */
    protected LoggingBackend createLoggingBackend(LoggingContext context) {
        List<String> names = configuration.getList("backends");
        Map<String, LoggingBackendBuilder> builders = new HashMap<>();
        List<LoggingBackend> backends = new ArrayList<>();

        SafeServiceLoader.load(
            getClassLoader(),
            LoggingBackendBuilder.class,
            "logging backend builders",
            builder -> builders.put(builder.getName().toLowerCase(Locale.ENGLISH), builder)
        );

        for (String name : names) {
            LoggingBackendBuilder builder = builders.get(name.toLowerCase(Locale.ENGLISH));
            if (builder == null) {
                InternalLogger.error(
                    null,
                    "Could not find any logging backend with the name \"{}\" in the classpath",
                    name
                );
            } else {
                SafeServiceLoader.execute(backends, builder, "execute", instance -> instance.create(context));
            }
        }

        if (backends.isEmpty()) {
            for (Map.Entry<String, LoggingBackendBuilder> entry : builders.entrySet()) {
                LoggingBackendBuilder builder = entry.getValue();
                if (!(builder instanceof NopLoggingBackendBuilder)
                        && !(builder instanceof InternalLoggingBackendBuilder)) {
                    SafeServiceLoader.execute(backends, builder, "execute", instance -> instance.create(context));
                }
            }
        }

        if (backends.isEmpty()) {
            InternalLogger.warn(null, "No logging backend could be found in the classpath. Therefore, no log "
                + "entries will be output. Please add tinylog-impl.jar or any other logging backend for outputting log "
                + "entries, or disable logging explicitly by setting \"backends = nop\" in the configuration.");
            return new InternalLoggingBackend(context);
        } else if (backends.size() == 1) {
            return backends.get(0);
        } else {
            return new BundleLoggingBackend(backends);
        }
    }

    /**
     * Finds and creates a supported runtime flavor.
     *
     * @return An instance of a supported runtime flavor
     */
    private RuntimeFlavor createRuntime() {
        return SafeServiceLoader
            .asList(getClassLoader(), RuntimeBuilder.class, "runtime builders")
            .stream()
            .filter(RuntimeBuilder::isSupported)
            .sorted(Comparator.comparingInt(RuntimeBuilder::getPriority).reversed())
            .findAny()
            .orElseThrow(() -> new IllegalStateException("No supported runtime available"))
            .create();
    }

    /**
     * Loads all hooks that are registered as a {@link ServiceLoader service} in {@code META-INF/services}.
     *
     * @return All found hooks
     */
    private Collection<Hook> loadHooks() {
        return SafeServiceLoader.asList(getClassLoader(), Hook.class, "hooks");
    }

    /**
     * Loads the configuration via a registered {@link ConfigurationLoader}.
     *
     * @return The initial configuration
     */
    private Configuration loadConfiguration() {
        List<ConfigurationLoader> loaders = SafeServiceLoader.asList(
            getClassLoader(),
            ConfigurationLoader.class,
            "configuration loaders"
        );

        Map<String, String> properties = loaders.stream()
            .sorted(Comparator.comparingInt(ConfigurationLoader::getPriority).reversed())
            .map(loader -> loader.load(getClassLoader()))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(Collections.emptyMap());

        return new Configuration(properties);
    }

    /**
     * Freezes the stored configuration and creates a new logging backend, if none is assigned yet.
     */
    private void loadLoggingBackend() {
        frozen = true;
        startUp();

        if (loggingBackend == null) {
            LoggingContext context = new LoggingContext(this, configuration);
            loggingBackend = createLoggingBackend(context);
            InternalLogger.init(context);
            InternalLogger.debug(null, "Active logging backend: {}", loggingBackend.getClass().getName());
        }
    }

}
