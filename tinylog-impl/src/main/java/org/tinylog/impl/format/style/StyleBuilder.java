package org.tinylog.impl.format.style;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.placeholder.Placeholder;

/**
 * Builder for creating a style wrapper for a {@link Placeholder}.
 *
 * <p>
 *     New style builders can be provided as {@link java.util.ServiceLoader service} via {@code META-INF/services}.
 * </p>
 *
 * <p>
 *     Typically, new style wrappers extend {@link AbstractStylePlaceholder}, which simplifies the implementation of
 *     style wrappers and already contains much common functionality. However, a style builder can create any kind of
 *     placeholder that implements the interface {@link Placeholder}.
 * </p>
 *
 * @see AbstractStylePlaceholder
 */
public interface StyleBuilder {

    /**
     * Gets the name of the style, which can be used for reformatting the output of placeholders in format patterns.
     *
     * <p>
     *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
     *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
     *     allowed.
     * </p>
     *
     * @return The name of the style
     */
    String getName();

    /**
     * Creates a new style wrapper of another placeholder.
     *
     * @param context The tinylog context to use for creating a new placeholder
     * @param placeholder The actual placeholder to style
     * @param value An optional configuration value for the style
     * @return New instance of the styled placeholder
     */
    Placeholder create(TinylogContext context, Placeholder placeholder, String value);

    /**
     * Loads and creates all available style builders.
     *
     * <p>
     *     All style builders are mapped to their lower case names.
     * </p>
     *
     * @param loader The class loader to use for loading the service implementations
     * @return A map with the names and style builder instances
     */
    static Map<String, StyleBuilder> load(ClassLoader loader) {
        Map<String, StyleBuilder> builders = new HashMap<>();

        ServiceLoader
            .load(StyleBuilder.class, loader)
            .forEach(builder -> builders.put(builder.getName().toLowerCase(Locale.ENGLISH), builder));

        return builders;
    }

}
