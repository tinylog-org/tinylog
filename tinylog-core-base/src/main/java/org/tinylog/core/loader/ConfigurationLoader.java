package org.tinylog.core.loader;

import java.util.Map;

import org.tinylog.core.Framework;

/**
 * Service interface for loading configurations.
 *
 * <p>
 *     New configuration loaders can be provided as {@link java.util.ServiceLoader service} via
 *     {@code META-INF/services}.
 * </p>
 */
public interface ConfigurationLoader {

    /**
     * Gets the priority of this configuration loader. tinylog tries all available configuration loaders in order to
     * find a configuration loader that can provide a configuration for tinylog. The configuration loader with the
     * highest priority comes first and the one with the lowest priority comes last.
     *
     * @return The priority of this configuration loader
     */
    int getPriority();

    /**
     * Tries to load the configuration for tinylog.
     *
     * @param framework The actual framework instance
     * @return The loaded configuration as map if any configuration could be found and successfully loaded, or
     *         {@code null} if there is no suitable configuration
     */
    Map<String, String> load(Framework framework);

}
