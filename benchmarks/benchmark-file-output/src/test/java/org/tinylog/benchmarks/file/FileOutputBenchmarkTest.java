package org.tinylog.benchmarks.file;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class FileOutputBenchmarkTest {

    private final FileOutputBenchmark benchmark = new FileOutputBenchmark();

    /**
     * Verifies that {@link FileOutputBenchmark#logFile(LogFileState)} writes {@link FileOutputBenchmark#getContent()}
     * correctly into the current temporary file.
     *
     * @param bufferSize The buffer size in bytes
     */
    @ParameterizedTest
    @ValueSource(ints = {1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072})
    void logFile(int bufferSize) throws IOException {
        LogFileState state = new LogFileState(bufferSize);
        state.init();
        benchmark.logFile(state);
        state.close();

        assertThat(state.getPath()).hasContent(benchmark.getContent());
        state.dispose();
    }

    /**
     * Verifies that {@link FileOutputBenchmark#outputStream(OutputStreamState)} writes
     * {@link FileOutputBenchmark#getContent()} correctly into the current temporary file.
     *
     * @param bufferSize The buffer size in bytes
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072})
    void outputStream(int bufferSize) throws IOException {
        OutputStreamState state = new OutputStreamState(bufferSize);
        state.init();
        benchmark.outputStream(state);
        state.close();

        assertThat(state.getPath()).hasContent(benchmark.getContent());
        state.dispose();
    }

    /**
     * Verifies that {@link FileOutputBenchmark#randomAccessFile(RandomAccessFileState)} writes
     * {@link FileOutputBenchmark#getContent()} correctly into the current temporary file.
     */
    @Test
    void randomAccessFile() throws IOException {
        RandomAccessFileState state = new RandomAccessFileState();
        state.init();
        benchmark.randomAccessFile(state);
        state.close();

        assertThat(state.getPath()).hasContent(benchmark.getContent());
        state.dispose();
    }

    /**
     * Verifies that {@link FileOutputBenchmark#writer(WriterState)} writes {@link FileOutputBenchmark#getContent()}
     * correctly into the current temporary file.
     *
     * @param bufferSize The buffer size in bytes
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072})
    void writer(int bufferSize) throws IOException {
        WriterState state = new WriterState(bufferSize);
        state.init();
        benchmark.writer(state);
        state.close();

        assertThat(state.getPath()).hasContent(benchmark.getContent());
        state.dispose();
    }

}
