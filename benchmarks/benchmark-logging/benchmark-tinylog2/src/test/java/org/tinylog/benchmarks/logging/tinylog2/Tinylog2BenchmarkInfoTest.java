package org.tinylog.benchmarks.logging.tinylog2;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

import static org.assertj.core.api.Assertions.assertThat;

class Tinylog2BenchmarkInfoTest {

    /**
     * Verifies that the provided logger is part of tinylog and provides a valid implementation version.
     */
    @Test
    void logger() {
        BenchmarkInfo benchmarkInfo = new Tinylog2BenchmarkInfo();
        assertThat(benchmarkInfo.getLogger())
            .hasPackage("org.tinylog")
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
            .isEqualTo("tinylog");
    }

}
