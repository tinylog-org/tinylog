package org.tinylog.benchmarks.queue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QueueBenchmarkTest {

    /**
     * Verifies that the benchmark terminates without any remaining active threads.
     */
    @Test
    void asynchronousTaskExecutor() throws InterruptedException {
        List<Thread> threadsBefore = getThreads();

        QueueBenchmark benchmark = new QueueBenchmark(64, 2, 100);
        benchmark.asynchronousTaskExecutor();

        List<Thread> threadsAfterwards = getThreads();

        assertThat(threadsAfterwards).containsExactlyInAnyOrderElementsOf(threadsBefore);
    }

    /**
     * Receive all threads of the current thread group.
     *
     * @return All threads
     */
    private List<Thread> getThreads() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[64];
        int count = group.enumerate(threads);
        return Stream.of(threads).limit(count).collect(Collectors.toList());
    }

}
