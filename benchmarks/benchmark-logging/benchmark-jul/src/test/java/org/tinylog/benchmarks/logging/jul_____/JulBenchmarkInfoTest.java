package org.tinylog.benchmarks.logging.jul_____;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

import static org.assertj.core.api.Assertions.assertThat;

class JulBenchmarkInfoTest {

    /**
     * Verifies that the provided logger is part of java.util.logging and does not provide any implementation version.
     */
    @Test
    void logger() {
        BenchmarkInfo benchmarkInfo = new JulBenchmarkInfo();
        assertThat(benchmarkInfo.getLogger())
            .hasPackage("java.util.logging")
            .satisfies(logger -> {
                String version = logger.getPackage().getImplementationVersion();
                assertThat(version).isNull();
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
            .isEqualTo("java.util.logging");
    }

}
