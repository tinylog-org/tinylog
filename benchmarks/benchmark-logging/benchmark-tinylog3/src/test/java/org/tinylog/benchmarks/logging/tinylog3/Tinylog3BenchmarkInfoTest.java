package org.tinylog.benchmarks.logging.tinylog3;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

import static org.assertj.core.api.Assertions.assertThat;

class Tinylog3BenchmarkInfoTest {

    /**
     * Verifies that the provided logger is part of tinylog.
     */
    @Test
    void logger() {
        BenchmarkInfo benchmarkInfo = new Tinylog3BenchmarkInfo();
        assertThat(benchmarkInfo.getLogger()).hasPackage("org.tinylog");
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
