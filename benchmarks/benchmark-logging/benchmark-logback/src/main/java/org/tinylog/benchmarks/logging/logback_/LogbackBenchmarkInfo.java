package org.tinylog.benchmarks.logging.logback_;

import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

import ch.qos.logback.classic.Logger;

/**
 * Benchmark meta information for Logback.
 */
public class LogbackBenchmarkInfo implements BenchmarkInfo {

    /** */
    public LogbackBenchmarkInfo() {
    }

    @Override
    public String getName() {
        return "Logback";
    }

    @Override
    public Class<?> getLogger() {
        return Logger.class;
    }

}
