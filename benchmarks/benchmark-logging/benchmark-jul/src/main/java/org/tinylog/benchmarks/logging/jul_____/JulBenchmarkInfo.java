package org.tinylog.benchmarks.logging.jul_____;

import java.util.logging.Logger;

import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

/**
 * Benchmark meta information for java.util.logging.
 */
public class JulBenchmarkInfo implements BenchmarkInfo {

    /** */
    public JulBenchmarkInfo() {
    }

    @Override
    public String getName() {
        return "java.util.logging";
    }

    @Override
    public Class<?> getLogger() {
        return Logger.class;
    }

}
