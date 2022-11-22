package org.tinylog.benchmarks.logging.core;

/**
 * Meta information for logging benchmarks.
 *
 * <p>
 *     This service interface must be implemented by every logging benchmark project.
 * </p>
 */
public interface BenchmarkInfo {

    /**
     * Gets the human-readable name of the benchmarked logging framework.
     *
     * @return The human-readable name of the logging framework
     */
    String getName();

    /**
     * Gets the logger class of the benchmarked logging framework.
     *
     * @return The logger class of the logging framework
     */
    Class<?> getLogger();

}
