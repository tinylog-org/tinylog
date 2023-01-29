package org.tinylog.core;

/**
 * Hooks are called when tinylog is starting and shutting down.
 *
 * <p>
 *     New hooks can either be programmatically registered on {@link Tinylog} or provided as
 *     {@link java.util.ServiceLoader service} via {@code META-INF/services}.
 * </p>
 */
public interface Hook {

    /**
     * This method is called when tinylog is starting.
     */
    void startUp();

    /**
     * This method is called when tinylog is shutting down.
     */
    void shutDown();

}