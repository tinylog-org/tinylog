package org.tinylog.benchmarks.logging.log4j2__;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

import static org.assertj.core.api.Assertions.assertThat;

class Log4j2BenchmarkInfoTest {

    /**
     * Verifies that the provided logger is part of Apache Log4j 2 and provides a valid implementation version.
     */
    @Test
    void logger() {
        BenchmarkInfo benchmarkInfo = new Log4j2BenchmarkInfo();
        assertThat(benchmarkInfo.getLogger())
            .hasPackage("org.apache.logging.log4j")
            .satisfies(logger -> {
                String version = logger.getPackage().getImplementationVersion();
                assertThat(version).matches("2(\\.\\d+)+(-.+)?");
            });
    }

    /**
     * Verifies that the benchmark info implementation is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(BenchmarkInfo.class))
            .singleElement()
            .extracting(BenchmarkInfo::getName)
            .isEqualTo("Log4j");
    }

}
