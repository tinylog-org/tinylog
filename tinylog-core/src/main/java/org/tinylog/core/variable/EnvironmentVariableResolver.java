package org.tinylog.core.variable;

/**
 * Resolver for environment variables.
 */
public class EnvironmentVariableResolver implements VariableResolver {

    /** */
    public EnvironmentVariableResolver() {
    }

    @Override
    public String getName() {
        return "environment variable";
    }

    @Override
    public String getPrefix() {
        return "$";
    }

    @Override
    public String resolve(String name) {
        return System.getenv(name);
    }

}
