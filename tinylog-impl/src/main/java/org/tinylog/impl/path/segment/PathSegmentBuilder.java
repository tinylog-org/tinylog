package org.tinylog.impl.path.segment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of a {@link PathSegment}.
 *
 * <p>
 *     New path segment builders can be provided as {@link java.util.ServiceLoader service} via
 *     {@code META-INF/services}.
 * </p>
 */
public interface PathSegmentBuilder {

    /**
     * Gets the name of the path segment, which can be used to address the policy in a configuration.
     *
     * <p>
     *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
     *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
     *     allowed.
     * </p>
     *
     * @return The name of the path segment
     */
    String getName();

    /**
     * Creates a new instance of the path segment.
     *
     * @param context The tinylog context to use for creating a new path segment
     * @param value An optional configuration value for the path segment
     * @return New path segment instance
     * @throws Exception If failed to create a new path segment for the passed configuration value
     */
    PathSegment create(TinylogContext context, String value) throws Exception;

    /**
     * Loads and creates all available path segment builders.
     *
     * <p>
     *     All path segment builders are mapped to their lower case names.
     * </p>
     *
     * @param loader The class loader to use for loading the service implementations
     * @return A map with the names and path segment builder instances
     */
    static Map<String, PathSegmentBuilder> load(ClassLoader loader) {
        Map<String, PathSegmentBuilder> builders = new HashMap<>();

        ServiceLoader
            .load(PathSegmentBuilder.class, loader)
            .forEach(builder -> builders.put(builder.getName().toLowerCase(Locale.ENGLISH), builder));

        return builders;
    }

}
