package org.tinylog.core.variable;

/**
 * Resolver for system properties.
 */
public class SystemPropertyResolver implements VariableResolver {

	/** */
	public SystemPropertyResolver() {
	}

	@Override
	public String getName() {
		return "system property";
	}

	@Override
	public String getPrefix() {
		return "#";
	}

	@Override
	public String resolve(String name) {
		return System.getProperty(name);
	}

}
