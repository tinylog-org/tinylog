package org.tinylog.impl.format;

import org.tinylog.core.Framework;

/**
 * Builder for creating {@link Placeholder Placeholders}.
 *
 * <p>
 *     New placeholder builders can be provided as {@link java.util.ServiceLoader service} in {@code META-INF/services}.
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
	 * @param framework The actual logging framework instance
	 * @param value Optional configuration value for the created placeholder
	 * @return New instance of the placeholder
	 */
	Placeholder create(Framework framework, String value);

}
