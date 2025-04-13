package org.tinylog.impl.writer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of a {@link Writer}.
 *
 * <p>
 *     New writer builders can be provided as {@link java.util.ServiceLoader service} via {@code META-INF/services}.
 * </p>
 */
public interface WriterBuilder {

    /**
     * Gets the name of the writer, which can be used to address the writer in a configuration.
     *
     * <p>
     *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
     *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
     *     allowed.
     * </p>
     *
     * @return The name of the writer
     */
    String getName();

    /**
     * Creates a new instance of the writer.
     *
     * @param context The tinylog context to use for creating a new writer
     * @return New instance of the writer
     * @throws Exception If failed to create a new writer for the passed configuration
     */
    Writer create(TinylogContext context) throws Exception;

    /**
     * Loads and creates all available writer builders.
     *
     * @param loader The class loader to use for loading the service implementations
     * @return A map with the names and writer builder instances
     */
    static Map<String, WriterBuilder> load(ClassLoader loader) {
        Map<String, WriterBuilder> builders = new HashMap<>();

        ServiceLoader
            .load(WriterBuilder.class, loader)
            .forEach(builder -> builders.put(builder.getName().toLowerCase(Locale.ENGLISH), builder));

        return builders;
    }

}
