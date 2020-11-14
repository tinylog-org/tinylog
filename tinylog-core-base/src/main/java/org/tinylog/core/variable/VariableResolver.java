package org.tinylog.core.variable;

/**
 * Service interface for variable resolvers in configuration files.
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
	 * @return The value of the variable if existing, or {@code null} if the variable could not be found
	 */
	String resolve(String name);

}
