package org.tinylog.impl.format;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of an {@link OutputFormat}.
 *
 * <p>
 *     New output format builders can be provided as {@link java.util.ServiceLoader service} via
 *     {@code META-INF/services}.
 * </p>
 */
public interface OutputFormatBuilder {

    /**
     * Gets the name of the output format, which can be used to address the output format in a configuration.
     *
     * <p>
     *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
     *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
     *     allowed.
     * </p>
     *
     * @return The name of the output format
     */
    String getName();

    /**
     * Creates a new instance of the output format.
     *
     * @param context The tinylog context to use for creating a new output format
     * @return New output format instance
     */
    OutputFormat create(TinylogContext context);

    /**
     * Loads and creates all available output format builders.
     *
     * @param loader The class loader to use for loading the service implementations
     * @return A map with the names and output format builder instances
     */
    static Map<String, OutputFormatBuilder> load(ClassLoader loader) {
        Map<String, OutputFormatBuilder> builders = new HashMap<>();

        ServiceLoader
            .load(OutputFormatBuilder.class, loader)
            .forEach(builder -> builders.put(builder.getName().toLowerCase(Locale.ENGLISH), builder));

        return builders;
    }

}
