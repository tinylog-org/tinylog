package org.tinylog.core.runtime;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import org.tinylog.core.internal.InternalLogger;

/**
 * Builder for creating an instance of a {@link RuntimeFlavor}.
 *
 * <p>
 *     New runtime builders can be provided as {@link java.util.ServiceLoader service} via {@code META-INF/services}.
 * </p>
 */
public interface RuntimeBuilder {

    /**
     * Tests whether this runtime flavor supports the actual virtual machine.
     *
     * @return {@code true} if supported, otherwise {@code false}
     */
    boolean isSupported();

    /**
     * Gets the priority of this runtime flavor. tinylog tries all available runtime builders in order to find a
     * supported runtime flavor. The runtime flavor with the highest priority comes first and the one with the lowest
     * priority comes last.
     *
     * @return The priority of this runtime flavor
     */
    int getPriority();

    /**
     * Creates a new instance of the runtime flavor.
     *
     * @param logger The internal logger instance for issuing internal tinylog log entries
     * @return New instance of the runtime flavor
     */
    RuntimeFlavor create(InternalLogger logger);

    /**
     * Loads and creates a supported runtime builder.
     *
     * @param loader The class loader to use for loading the service implementations
     * @return A supported runtime builder instance
     * @throws IllegalStateException If there is no supported runtime builder for the current virtual machine
     */
    static RuntimeBuilder load(ClassLoader loader) {
        Iterable<RuntimeBuilder> iterable = ServiceLoader.load(RuntimeBuilder.class, loader);
        return StreamSupport.stream(iterable.spliterator(), false)
            .filter(RuntimeBuilder::isSupported)
            .max(Comparator.comparingInt(RuntimeBuilder::getPriority))
            .orElseThrow(() -> new IllegalStateException("No supported runtime available"));
    }

}
