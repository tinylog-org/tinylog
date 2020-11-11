package org.tinylog.core.loader;

/**
 * Builder for creating {@link ConfigurationLoader ConfigurationLoaders}.
 *
 * <p>
 *     New configuration loader builders can be provided as {@link java.util.ServiceLoader service} in
 *     {@code META-INF/services}.
 * </p>
 */
public interface ConfigurationLoaderBuilder {

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
	 * Creates a new instance of the configuration loader.
	 *
	 * @return New instance of the configuration loader
	 */
	ConfigurationLoader create();

}
