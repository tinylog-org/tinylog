package org.tinylog.benchmarks.logging.noop;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

/**
 * No-operation benchmark as reference value for logging benchmarks.
 */
public class NoOperationBenchmark {

    /** */
    public NoOperationBenchmark() {
    }

    /**
     * Does nothing as a no-operation benchmark method.
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void emptyMethod() {
    }

}
