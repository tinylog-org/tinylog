package org.tinylog.impl.format.placeholder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of a {@link Placeholder}.
 *
 * <p>
 *     New placeholder builders can be provided as {@link java.util.ServiceLoader service} via
 *     {@code META-INF/services}.
 * </p>
 */
public interface PlaceholderBuilder {

    /**
     * Gets the name of the placeholder, which can be used as placeholders in format patterns for log entry output.
     *
     * <p>
     *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
     *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
     *     allowed.
     * </p>
     *
     * @return The name of the placeholder
     */
    String getName();

    /**
     * Creates a new instance of the placeholder.
     *
     * @param context The tinylog context to use for creating a new placeholder
     * @param value An optional configuration value for the placeholder
     * @return New placeholder instance
     */
    Placeholder create(TinylogContext context, String value);

    /**
     * Loads and creates all available placeholder builders.
     *
     * <p>
     *     All placeholder builders are mapped to their lower case names.
     * </p>
     *
     * @param loader The class loader to use for loading the service implementations
     * @return A map with the names and placeholder builder instances
     */
    static Map<String, PlaceholderBuilder> load(ClassLoader loader) {
        Map<String, PlaceholderBuilder> builders = new HashMap<>();

        ServiceLoader
            .load(PlaceholderBuilder.class, loader)
            .forEach(builder -> builders.put(builder.getName().toLowerCase(Locale.ENGLISH), builder));

        return builders;
    }

}
