package org.tinylog.impl.format.style;

import org.tinylog.core.Framework;

/**
 * Builder for creating {@link Style Styles}.
 *
 * <p>
 *     New style builders can be provided as {@link java.util.ServiceLoader service} in {@code META-INF/services}.
 * </p>
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
	 * Creates a new instance of the style.
	 *
	 * @param framework The actual logging framework instance
	 * @param value Optional configuration value for the created style
	 * @return New instance of the style
	 */
	Style create(Framework framework, String value);

}
