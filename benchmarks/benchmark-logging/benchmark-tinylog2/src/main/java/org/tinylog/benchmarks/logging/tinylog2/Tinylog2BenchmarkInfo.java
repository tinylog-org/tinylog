package org.tinylog.benchmarks.logging.tinylog2;

import org.tinylog.Logger;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

/**
 * Benchmark meta information for tinylog 2.
 */
public class Tinylog2BenchmarkInfo implements BenchmarkInfo {

    /** */
    public Tinylog2BenchmarkInfo() {
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
