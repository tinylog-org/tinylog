package org.tinylog.benchmarks.logging.tinylog3;

import org.tinylog.Logger;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

/**
 * Benchmark meta information for tinylog 3.
 */
public class Tinylog3BenchmarkInfo implements BenchmarkInfo {

    /** */
    public Tinylog3BenchmarkInfo() {
    }

    @Override
    public String getName() {
        return "tinylog";
    }

    @Override
    public Class<?> getLogger() {
        return Logger.class;
    }

}
