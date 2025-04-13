package org.tinylog.core.variable;

import org.tinylog.core.internal.InternalLogger;

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
    public String resolve(String name, InternalLogger logger) {
        return System.getProperty(name);
    }

}
