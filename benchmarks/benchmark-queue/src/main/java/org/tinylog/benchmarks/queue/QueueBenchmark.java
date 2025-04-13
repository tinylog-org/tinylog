package org.tinylog.benchmarks.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.tinylog.core.backend.NopLoggingBackend;
import org.tinylog.core.internal.AsynchronousTaskExecutor;

/**
 * Benchmark for thread-safe waiting queues.
 */
@State(Scope.Thread)
public class QueueBenchmark {

    @Param("1024")
    private int size;

    @Param("4")
    private int threads;

    @Param("1000000")
    private int iterations;

    /** */
    public QueueBenchmark() {
    }

    /**
     * @param size The size of the waiting queue
     * @param threads The number of threads to feed the waiting queue simultaneously
     * @param iterations The number of iterations per thread
     */
    public QueueBenchmark(int size, int threads, int iterations) {
        this.size = size;
        this.threads = threads;
        this.iterations = iterations;
    }

    /**
     * Enqueues and executes task by using {@link AsynchronousTaskExecutor}.
     *
     * @throws InterruptedException If interrupted while waiting
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void asynchronousTaskExecutor() throws InterruptedException {
        List<Thread> feederThreads = new ArrayList<>(threads);

        AsynchronousTaskExecutor taskExecutor = new AsynchronousTaskExecutor(size);
        taskExecutor.start(new NopLoggingBackend());

        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; ++i) {
            Thread thread = new FeederThread("feeder-thread-" + i, iterations, taskExecutor, latch);
            thread.start();
            feederThreads.add(thread);
        }

        latch.await();

        taskExecutor.stop();

        for (Thread thread : feederThreads) {
            thread.join();
        }
    }

}
