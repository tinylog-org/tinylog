package org.tinylog.impl.policies;

import org.tinylog.core.Framework;

/**
 * Builder for creating an instance of a {@link Policy}.
 *
 * <p>
 *     New policy builders can be provided as {@link java.util.ServiceLoader service} in {@code META-INF/services}.
 * </p>
 */
public interface PolicyBuilder {

	/**
	 * Gets the name of the policy, which can be used to address the policy in a configuration.
	 *
	 * <p>
	 *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
	 *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
	 *     allowed.
	 * </p>
	 *
	 * @return The name of the policy
	 */
	String getName();

	/**
	 * Creates a new instance of the policy.
	 *
	 * @param framework The actual logging framework instance
	 * @param value Optional configuration value for the created policy
	 * @return New instance of the policy
	 * @throws Exception Failed to create a new policy for the passed configuration value
	 */
	Policy create(Framework framework, String value) throws Exception;

}
