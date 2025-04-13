package org.tinylog.core.variable;

import java.util.ServiceLoader;

import org.tinylog.core.internal.InternalLogger;

/**
 * Service interface for resolving prefixed variables in configuration files.
 */
public interface VariableResolver {

    /**
     * Gets the human-readable resolver name.
     *
     * @return The human-readable resolver name
     */
    String getName();

    /**
     * Gets the prefix character to identify this variable resolver. The prefix character is the character that comes
     * directly before the opening curly bracket.
     *
     * @return The prefix for this variable resolver (must contain at least one character)
     */
    String getPrefix();

    /**
     * Resolves a variable by its name.
     *
     * @param name The name of the variable to resolve
     * @param logger The internal logger instance for issuing internal tinylog log entries
     * @return The value of the variable if existing, or {@code null} if the variable could not be found
     */
    String resolve(String name, InternalLogger logger);

    /**
     * Loads and creates all available variable resolvers.
     *
     * @param loader The class loader to use for loading the service implementations
     * @return An iterable with all variable resolvers
     */
    static Iterable<VariableResolver> load(ClassLoader loader) {
        return ServiceLoader.load(VariableResolver.class, loader);
    }

}
