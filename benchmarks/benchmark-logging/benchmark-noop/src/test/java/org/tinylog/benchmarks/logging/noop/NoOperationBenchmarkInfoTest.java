package org.tinylog.benchmarks.logging.noop;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

import static org.assertj.core.api.Assertions.assertThat;

class NoOperationBenchmarkInfoTest {

    /**
     * Verifies that there is no provided logger.
     */
    @Test
    void logger() {
        BenchmarkInfo benchmarkInfo = new NoOperationBenchmarkInfo();
        assertThat(benchmarkInfo.getLogger()).isNull();
    }

    /**
     * Verifies that the benchmark info implementation is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(BenchmarkInfo.class))
            .singleElement()
            .extracting(BenchmarkInfo::getName)
            .isEqualTo("Empty Method");
    }

}
