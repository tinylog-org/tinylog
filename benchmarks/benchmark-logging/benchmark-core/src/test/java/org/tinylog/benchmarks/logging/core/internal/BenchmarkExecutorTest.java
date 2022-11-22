package org.tinylog.benchmarks.logging.core.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BenchmarkExecutorTest {

    @Mock
    private MainMethod mainMethod;

    private Path benchmarkNameFile;

    /**
     * Deletes the target file for the name of the benchmarked logging framework before and after every test.
     */
    @BeforeEach
    @AfterEach
    void deleteBenchmarkNameFile() throws URISyntaxException, IOException {
        benchmarkNameFile = getBenchmarkNameFile();
        Files.deleteIfExists(benchmarkNameFile);
    }

    /**
     * Verifies that the passed main method will be invoked and the benchmark name will be written, if no exception is
     * thrown.
     */
    @Test
    void executeWithoutException() throws URISyntaxException, IOException {
        new BenchmarkExecutor(mainMethod).execute("1", "2", "3");

        assertThat(benchmarkNameFile).hasContent("foo");
        verify(mainMethod).execute(new String[] {"1", "2", "3"});
    }

    /**
     * Verifies that the passed main method will be invoked and the benchmark name will be written, even if an exception
     * is thrown.
     */
    @Test
    void executeWithException() throws IOException {
        doThrow(IOException.class).when(mainMethod).execute(any());

        assertThatCode(() -> new BenchmarkExecutor(mainMethod).execute("1", "2", "3"))
            .isInstanceOf(IOException.class);

        assertThat(benchmarkNameFile).hasContent("foo");
    }

    /**
     * Resolves the full path of the file that should contain the name of the benchmarked logging framework.
     *
     * @return The path to the file
     */
    private static Path getBenchmarkNameFile() throws URISyntaxException {
        return new ProjectLocation(BenchmarkExecutorTest.class).resolve(ProjectLocation.BENCHMARK_NAME_FILE);
    }

    /**
     * Dummy service implementation for the JUnit tests of this file.
     */
    public static class BenchmarkExecutorTestInfo implements BenchmarkInfo {

        @Override
        public String getName() {
            return "foo";
        }

        @Override
        public Class<?> getLogger() {
            return null;
        }

    }

}
