package org.tinylog.benchmarks.queue;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.tinylog.core.TaskExecutor;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.NopLoggingBackend;
import org.tinylog.test.util.SynchronousTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FeederThreadTest {

    /**
     * Verifies that the feeder thread enqueues as many tasks as the passed count. However, the count-down latch must be
     * counted down only once.
     */
    @Test
    void execution() throws InterruptedException {
        LoggingBackend backend = new NopLoggingBackend();
        TaskExecutor executor = spy(new SynchronousTaskExecutor(backend));
        CountDownLatch latch = new CountDownLatch(10);

        FeederThread thread = new FeederThread("feeder-test", 10, executor, latch);
        thread.start();
        thread.join();

        verify(executor, times(10)).enqueue(any());
        assertThat(latch.getCount()).isEqualTo(9);
    }

}
