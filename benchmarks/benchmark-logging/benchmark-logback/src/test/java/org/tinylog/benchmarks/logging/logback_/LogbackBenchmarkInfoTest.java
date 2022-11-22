package org.tinylog.benchmarks.logging.logback_;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

import static org.assertj.core.api.Assertions.assertThat;

class LogbackBenchmarkInfoTest {

    /**
     * Verifies that the provided logger is part of Logback and provides a valid implementation version.
     */
    @Test
    void logger() {
        BenchmarkInfo benchmarkInfo = new LogbackBenchmarkInfo();
        assertThat(benchmarkInfo.getLogger())
            .hasPackage("ch.qos.logback.classic")
            .satisfies(logger -> {
                String version = logger.getPackage().getImplementationVersion();
                assertThat(version).matches("1(\\.\\d+)+(-.+)?");
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
            .isEqualTo("Logback");
    }

}
