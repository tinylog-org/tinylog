package org.tinylog.impl.policy;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

import org.tinylog.core.backend.TinylogContext;

/**
 * Builder for creating an instance of a {@link Policy}.
 *
 * <p>
 *     New policy builders can be provided as {@link java.util.ServiceLoader service} via {@code META-INF/services}.
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
     * @param context The tinylog context to use for creating a new policy
     * @param value An optional configuration value for the policy
     * @return New instance of the policy
     * @throws Exception If failed to create a new policy for the passed configuration value
     */
    Policy create(TinylogContext context, String value) throws Exception;

    /**
     * Loads and creates all available policy builders.
     *
     * @param loader The class loader to use for loading the service implementations
     * @return A map with the names and policy builder instances
     */
    static Map<String, PolicyBuilder> load(ClassLoader loader) {
        Map<String, PolicyBuilder> builders = new HashMap<>();

        ServiceLoader
            .load(PolicyBuilder.class, loader)
            .forEach(builder -> builders.put(builder.getName().toLowerCase(Locale.ENGLISH), builder));

        return builders;
    }

}
