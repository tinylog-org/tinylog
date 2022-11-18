package org.tinylog.impl.format;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;

/**
 * Builder for creating an instance of an {@link OutputFormat}.
 *
 * <p>
 *     New output format builders can be provided as {@link java.util.ServiceLoader service} in
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
     * @param framework The actual logging framework instance
     * @param configuration The configuration properties of the writer for which the output format has to be created
     * @return New output format instance
     */
    OutputFormat create(Framework framework, Configuration configuration);

}
