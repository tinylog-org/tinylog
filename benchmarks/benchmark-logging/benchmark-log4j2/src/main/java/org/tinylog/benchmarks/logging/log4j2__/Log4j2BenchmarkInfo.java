package org.tinylog.benchmarks.logging.log4j2__;

import org.apache.logging.log4j.Logger;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

/**
 * Benchmark meta information for Apache Log4j 2.
 */
public class Log4j2BenchmarkInfo implements BenchmarkInfo {

    /** */
    public Log4j2BenchmarkInfo() {
    }

    @Override
    public String getName() {
        return "Log4j";
    }

    @Override
    public Class<?> getLogger() {
        return Logger.class;
    }

}
