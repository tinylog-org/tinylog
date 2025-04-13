package org.tinylog.core.internal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.backend.LoggingBackend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsynchronousTaskExecutorTest {

    @Mock
    private LoggingBackend backend;

    /**
     * Verifies that a single task can be executed asynchronously.
     */
    @Test
    void executeSingleTask() throws InterruptedException {
        AtomicBoolean state = new AtomicBoolean(false);

        AsynchronousTaskExecutor executor = new AsynchronousTaskExecutor(8);
        executor.enqueue((backend, last) -> state.set(true));
        executor.start(backend);

        try {
            await().untilAsserted(() -> assertThat(state).isTrue());
        } finally {
            executor.stop();
        }
    }

    /**
     * Verifies that task are only enqueued and executed if the current thread is not interrupted.
     */
    @Test
    void dropTaskIfInterrupted() throws InterruptedException {
        AtomicBoolean firstState = new AtomicBoolean(false);
        AtomicBoolean secondState = new AtomicBoolean(false);

        AsynchronousTaskExecutor executor = new AsynchronousTaskExecutor(8);

        Thread.currentThread().interrupt();
        assertThat(executor.enqueue((backend, last) -> firstState.set(true))).isFalse();
        assertThat(executor.enqueue((backend, last) -> secondState.set(true))).isTrue();

        executor.start(backend);

        try {
            await().untilAsserted(() -> {
                assertThat(firstState).isFalse();
                assertThat(secondState).isTrue();
            });
        } finally {
            executor.stop();
        }
    }

    /**
     * Verifies that multiple tasks are executed in order and the last flag is set correctly.
     */
    @Test
    void executeTasksInOrder() throws InterruptedException {
        Map<Integer, Boolean> data = Collections.synchronizedMap(new LinkedHashMap<>());

        AsynchronousTaskExecutor executor = new AsynchronousTaskExecutor(8);
        IntStream.of(0, 1, 2).forEach(index -> executor.enqueue((backend, last) -> data.put(index, last)));

        executor.start(backend);

        try {
            await().untilAsserted(
                () -> assertThat(data).containsExactly(entry(0, false), entry(1, false), entry(2, true))
            );
        } finally {
            executor.stop();
        }
    }

    /**
     * Verifies that more tasks than the configured capacity can be enqueued.
     */
    @Test
    void enqueueMoreTasksThanCapacity() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);

        AsynchronousTaskExecutor executor = new AsynchronousTaskExecutor(8);

        Thread feeder = new Thread(
            () -> IntStream.range(0, 16).forEach(index -> executor.enqueue((backend, last) -> count.incrementAndGet()))
        );

        feeder.start();
        executor.start(backend);

        try {
            await().untilAsserted(() -> assertThat(count).hasValue(16));
        } finally {
            executor.stop();
        }
    }

    /**
     * Verifies that the logging backend is closed after stopping the executor.
     */
    @Test
    void closeLoggingBackend() throws InterruptedException {
        AsynchronousTaskExecutor executor = new AsynchronousTaskExecutor(8);
        executor.start(backend);

        try {
            verify(backend, never()).close();
        } finally {
            executor.stop();
        }

        verify(backend).close();
    }

    /**
     * Verifies that the shutdown hook is correctly registered and deregistered.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void registerAndDeregisterShutdownHook() throws InterruptedException {
        try (MockedStatic<Runtime> mock = mockStatic(Runtime.class)) {
            Runtime runtime = mock(Runtime.class);
            mock.when(Runtime::getRuntime).thenReturn(runtime);

            AsynchronousTaskExecutor executor = new AsynchronousTaskExecutor(8);

            verify(runtime, never()).addShutdownHook(any());
            executor.start(backend);

            try {
                verify(runtime).addShutdownHook(any());
                verify(runtime, never()).removeShutdownHook(any());
            } finally {
                executor.stop();
            }

            verify(runtime).removeShutdownHook(any());
        }
    }

    /**
     * Verifies the shutdown hook can close the logging backend.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void shutdownByShutdownHook() throws InterruptedException {
        try (MockedStatic<Runtime> mock = mockStatic(Runtime.class)) {
            Runtime runtime = mock(Runtime.class);
            mock.when(Runtime::getRuntime).thenReturn(runtime);

            AsynchronousTaskExecutor executor = new AsynchronousTaskExecutor(8);
            executor.start(backend);

            try {
                ArgumentCaptor<Thread> captor = ArgumentCaptor.forClass(Thread.class);
                verify(runtime).addShutdownHook(captor.capture());

                Thread thread = captor.getValue();
                thread.start();
                thread.join();

                verify(backend).close();
            } finally {
                executor.stop();
            }
        }
    }

    /**
     * Verifies that the task executor stops gracefully if the internal daemon thread was interrupted.
     */
    @Test
    void stopWhenInterrupted() {
        AsynchronousTaskExecutor executor = new AsynchronousTaskExecutor(8);
        executor.start(backend);

        try {
            ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
            int count = threadGroup.activeCount();
            Thread[] allThreads = new Thread[count];
            threadGroup.enumerate(allThreads);

            List<Thread> tinylogThreads = Stream.of(allThreads)
                .filter(thread -> thread != null && thread.getName().contains("tinylog"))
                .collect(Collectors.toList());

            assertThat(tinylogThreads).isNotEmpty();

            tinylogThreads.forEach(Thread::interrupt);

            await().untilAsserted(() ->
                assertThat(tinylogThreads).allSatisfy(thread -> assertThat(thread.isAlive()).isFalse())
            );
        } finally {
            verify(backend).close();
        }
    }

}
