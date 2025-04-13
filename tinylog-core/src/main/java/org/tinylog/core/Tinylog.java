package org.tinylog.core;

/**
 * Global access to the tinylog framework.
 */
public final class Tinylog {

    private static final Framework framework = new Framework();

    /** */
    private Tinylog() {
    }

    /**
     * Creates a {@link ConfigurationBuilder} for changing the current configuration.
     *
     * @param inherit {@code true} for initializing the {@link ConfigurationBuilder} with the current configuration,
     *                {@code false} for creating an empty {@link ConfigurationBuilder}
     * @return A new configuration builder instance
     */
    public static ConfigurationBuilder getConfigurationBuilder(boolean inherit) {
        return framework.getConfigurationBuilder(inherit);
    }

    /**
     * Gets the global tinylog framework instance.
     *
     * @return The global tinylog framework instance
     */
    public static Framework getFramework() {
        return framework;
    }

}
