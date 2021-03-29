package org.tinylog.core.loader;

import java.util.Map;

import org.tinylog.core.Framework;

/**
 * Service interface for loading configurations.
 *
 * <p>
 *     New configuration loaders can be provided as {@link java.util.ServiceLoader service} in
 *     {@code META-INF/services}.
 * </p>
 */
public interface ConfigurationLoader {

	/**
	 * Gets the name of the configuration loader, which can be used to address the configuration loader in system
	 * properties.
	 *
	 * <p>
	 *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
	 *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
	 *     allowed.
	 * </p>
	 *
	 * @return The name of the configuration loader
	 */
	String getName();

	/**
	 * Gets the priority of this configuration loader. If no configuration loader has been defined via a system
	 * property, tinylog will try all available configuration loaders in order. The configuration loader with the
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
