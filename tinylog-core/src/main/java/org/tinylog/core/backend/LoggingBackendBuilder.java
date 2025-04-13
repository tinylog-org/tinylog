package org.tinylog.core.backend;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Builder for creating an instance of a {@link LoggingBackend}.
 *
 * <p>
 *     New logging backend builders can be provided as {@link java.util.ServiceLoader service} via
 *     {@code META-INF/services}.
 * </p>
 */
public interface LoggingBackendBuilder {

    /**
     * Gets the name of the logging backend, which can be used to address the logging backend in a configuration.
     *
     * <p>
     *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
     *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
     *     allowed.
     * </p>
     *
     * @return The name of the logging backend
     */
    String getName();

    /**
     * Creates a new instance of the logging backend.
     *
     * @param context The tinylog context to use for creating a new logging backend
     * @return New instance of the logging backend
     */
    LoggingBackend create(TinylogContext context);

    /**
     * Loads and creates all available logging backend builders.
     *
     * <p>
     *     All logging backend builders are mapped to their lower case names.
     * </p>
     *
     * @param loader The class loader to use for loading the service implementations
     * @return A map with the names and logging backend builder instances
     */
    static Map<String, LoggingBackendBuilder> load(ClassLoader loader) {
        Map<String, LoggingBackendBuilder> builders = new LinkedHashMap<>();

        ServiceLoader
            .load(LoggingBackendBuilder.class, loader)
            .forEach(builder -> builders.put(builder.getName().toLowerCase(Locale.ENGLISH), builder));

        return builders;
    }

}
