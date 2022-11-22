package org.tinylog.benchmarks.logging.noop;

import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

/**
 * Benchmark meta information for no-operation benchmark.
 */
public class NoOperationBenchmarkInfo implements BenchmarkInfo {

    /** */
    public NoOperationBenchmarkInfo() {
    }

    @Override
    public String getName() {
        return "Empty Method";
    }

    @Override
    public Class<?> getLogger() {
        return null;
    }

}
