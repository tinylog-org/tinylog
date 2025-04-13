package org.tinylog.core.backend;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.RuntimeFlavor;

/**
 * Factory for creating logging backends.
 */
public class LoggingBackendFactory {

    private final ClassLoader loader;
    private final RuntimeFlavor runtime;
    private final InternalLogger logger;

    /**
     * @param loader The class loader to use for loading service implementations
     * @param runtime The runtime flavor to use for non-standardized operations
     * @param logger The internal logger instance for issuing internal tinylog log entries
     */
    public LoggingBackendFactory(ClassLoader loader, RuntimeFlavor runtime, InternalLogger logger) {
        this.loader = loader;
        this.runtime = runtime;
        this.logger = logger;
    }

    /**
     * Creates a new logging backend.
     *
     * @param configuration The configuration to use for logging backend creation
     * @return The created logging backend
     */
    public LoggingBackend create(Configuration configuration) {
        TinylogContext context = new TinylogContext(loader, Clock.systemUTC(), runtime, configuration, logger);
        List<String> names = configuration.getList("backends");
        Map<String, LoggingBackendBuilder> builders = LoggingBackendBuilder.load(loader);
        Map<String, LoggingBackend> backends = new LinkedHashMap<>();

        for (String name : names) {
            String sanitizedName = name.toLowerCase(Locale.ENGLISH);
            LoggingBackendBuilder builder = builders.get(sanitizedName);
            if (builder == null) {
                logger.log(
                    Level.ERROR,
                    "Could not find any logging backend with the name \"{}\" in the classpath",
                    name
                );
            } else {
                createNonExistentBackend(builder, context, backends);
            }
        }

        if (backends.isEmpty()) {
            for (Map.Entry<String, LoggingBackendBuilder> entry : builders.entrySet()) {
                LoggingBackendBuilder builder = entry.getValue();
                if (!(builder instanceof NopLoggingBackendBuilder)
                    && !(builder instanceof InternalLoggingBackendBuilder)) {
                    createNonExistentBackend(builder, context, backends);
                }
            }
        }

        if (backends.isEmpty()) {
            logger.log(Level.WARN, "No logging backend could be found in the classpath. Therefore, no log "
                + "entries will be output. Please add tinylog-impl.jar or any other logging backend for outputting log "
                + "entries, or disable logging explicitly by setting \"backends = nop\" in the configuration.");
            return new InternalLoggingBackend(configuration);
        } else if (backends.size() == 1) {
            return backends.values().stream().findAny().get();
        } else {
            return new BundleLoggingBackend(backends.values());
        }
    }

    /**
     * Creates and adds a new logging backend if it doesn't exist in the target backend map.
     *
     * @param builder The builder to use for creating the logging backend
     * @param context The tinylog context to use for the new logging backend
     * @param backends The target map with logging backends
     */
    private static void createNonExistentBackend(
        LoggingBackendBuilder builder,
        TinylogContext context,
        Map<String, LoggingBackend> backends
    ) {
        try {
            backends.computeIfAbsent(builder.getName(), name -> builder.create(context));
        } catch (Exception ex) {
            context.getLogger().log(Level.ERROR, ex, "Failed to create {}", builder.getClass().getName());
        }
    }

}
