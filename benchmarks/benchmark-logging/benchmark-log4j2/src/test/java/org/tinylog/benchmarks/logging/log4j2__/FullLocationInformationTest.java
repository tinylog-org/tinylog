package org.tinylog.benchmarks.logging.log4j2__;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.tinylog.benchmarks.logging.core.LocationInfo;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class FullLocationInformationTest {

    private static final String NEW_LINE = System.lineSeparator();

    private static Log4j2__Benchmark benchmark;

    /**
     * Initializes the benchmark including the logging framework.
     *
     * @throws IOException Failed to configure the logging framework
     */
    @BeforeAll
    static void init() throws IOException {
        benchmark = new Log4j2__Benchmark(LocationInfo.FULL);
        benchmark.configure();
    }

    /**
     * Shuts the benchmark including the logging framework gracefully down.
     *
     * @throws InterruptedException Failed to wait for the graceful shutdown
     */
    @AfterAll
    static void dispose() throws InterruptedException {
        benchmark.shutdown();
    }

    /**
     * Verifies that the debug log entry will be not output.
     */
    @Test
    @Order(1)
    void discard() throws InterruptedException {
        benchmark.discard();
        Thread.sleep(10);

        String logFile = benchmark.getLogFile();
        assertThat(logFile).isNotNull();

        Path path = Paths.get(logFile);
        assertThat(path).isEmptyFile();
    }

    /**
     * Verifies that the into log entry will be output correctly.
     */
    @Test
    @Order(2)
    void output() throws InterruptedException {
        benchmark.output();
        Thread.sleep(10);

        String logFile = benchmark.getLogFile();
        assertThat(logFile).isNotNull();

        Path path = Paths.get(logFile);
        assertThat(path)
            .content(StandardCharsets.UTF_8)
            .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} - main - org\\.tinylog\\.benchmarks\\.logging\\."
                + "log4j2__\\.Log4j2__Benchmark\\.output\\(\\) - INFO: Hello 42!" + NEW_LINE);
    }

}
