package org.tinylog.benchmarks.logging.core.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ServiceLoader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BenchmarkNameTest {

    @Mock
    private ServiceLoader<BenchmarkInfo> serviceLoader;

    /**
     * Deletes the target file for the name of the benchmarked logging framework before and after every test.
     */
    @BeforeEach
    @AfterEach
    void deleteBenchmarkNameFile() throws Exception {
        Path path = getBenchmarkNameFile();
        Files.deleteIfExists(path);
    }

    /**
     * Verifies that the name including the provided version can be output.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void storeBenchmarkInfoWithVersion() throws URISyntaxException, IOException {
        BenchmarkInfo benchmarkInfo = mock(BenchmarkInfo.class);
        when(benchmarkInfo.getName()).thenReturn("SLF4J");
        when(benchmarkInfo.getLogger()).thenReturn((Class) org.slf4j.Logger.class);
        when(serviceLoader.findFirst()).thenReturn(Optional.of(benchmarkInfo));

        new BenchmarkName(serviceLoader).store();

        assertThat(getBenchmarkNameFile()).content().matches("SLF4J 2(\\.\\d+)*(-\\w+)?");
    }

    /**
     * Verifies that the name without version will be output, if there are no version information available.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void storeBenchmarkInfoWithoutVersion() throws URISyntaxException, IOException {
        BenchmarkInfo benchmarkInfo = mock(BenchmarkInfo.class);
        when(benchmarkInfo.getName()).thenReturn("java.util.logging");
        when(benchmarkInfo.getLogger()).thenReturn((Class) java.util.logging.Logger.class);
        when(serviceLoader.findFirst()).thenReturn(Optional.of(benchmarkInfo));

        new BenchmarkName(serviceLoader).store();

        assertThat(getBenchmarkNameFile()).hasContent("java.util.logging");
    }

    /**
     * Verifies that the name without version will be output, if the service implementation does not provide a logger
     * class.
     */
    @Test
    void storeBenchmarkInfoWithoutLogger() throws URISyntaxException, IOException {
        BenchmarkInfo benchmarkInfo = mock(BenchmarkInfo.class);
        when(benchmarkInfo.getName()).thenReturn("foo");
        when(benchmarkInfo.getLogger()).thenReturn(null);
        when(serviceLoader.findFirst()).thenReturn(Optional.of(benchmarkInfo));

        new BenchmarkName(serviceLoader).store();

        assertThat(getBenchmarkNameFile()).hasContent("foo");
    }

    /**
     * Verifies that no file will be created, if there is no service implementation in the class path.
     */
    @Test
    void skipWithoutBenchmarkInfo() throws URISyntaxException, IOException {
        when(serviceLoader.findFirst()).thenReturn(Optional.empty());

        new BenchmarkName(serviceLoader).store();

        assertThat(getBenchmarkNameFile()).doesNotExist();
    }

    /**
     * Resolves the full path of the file that should contain the name of the benchmarked logging framework.
     *
     * @return The path to the file
     */
    private static Path getBenchmarkNameFile() throws URISyntaxException {
        return new ProjectLocation(BenchmarkNameTest.class).resolve(ProjectLocation.BENCHMARK_NAME_FILE);
    }

}
