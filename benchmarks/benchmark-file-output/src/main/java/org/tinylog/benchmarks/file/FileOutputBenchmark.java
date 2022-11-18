package org.tinylog.benchmarks.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.tinylog.impl.writers.file.LogFile;

/**
 * Benchmark for comparing the performance of different approaches for writing strings into files.
 */
public class FileOutputBenchmark {

    private static final String CONTENT = "Hello World! I'm an example for a short log entry. Goodbye";

    /** */
    public FileOutputBenchmark() {
    }

    /**
     * Gets the text that should be written.
     *
     * @return The text to be written
     */
    public String getContent() {
        return CONTENT;
    }

    /**
     * Uses a {@link LogFile} for writing.
     *
     * @param state The state with the {@link LogFile} instance to use
     * @throws IOException Failed to write into the current file
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void logFile(LogFileState state) throws IOException {
        state.write(CONTENT);
    }

    /**
     * Uses a {@link FileOutputStream} for writing.
     *
     * @param state The state with the {@link FileOutputStream} instance to use
     * @throws IOException Failed to write into the current file
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void outputStream(OutputStreamState state) throws IOException {
        state.write(CONTENT);
    }

    /**
     * Uses a {@link RandomAccessFile} for writing.
     *
     * @param state The state with the {@link RandomAccessFile} instance to use
     * @throws IOException Failed to write into the current file
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void randomAccessFile(RandomAccessFileState state) throws IOException {
        state.write(CONTENT);
    }

    /**
     * Uses a {@link Writer} for writing.
     *
     * @param state The state with the {@link Writer} instance to use
     * @throws IOException Failed to write into the current file
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void writer(WriterState state) throws IOException {
        state.write(CONTENT);
    }

}
