package org.tinylog.impl.format.pattern.styles;

import org.tinylog.core.Framework;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;

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
     * @param framework The actual logging framework instance
     * @param placeholder The actual placeholder to style
     * @param value Optional configuration value for the style
     * @return New instance of the styled placeholder
     */
    Placeholder create(Framework framework, Placeholder placeholder, String value);

}
