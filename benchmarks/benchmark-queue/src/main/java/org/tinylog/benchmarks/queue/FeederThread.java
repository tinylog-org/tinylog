package org.tinylog.benchmarks.queue;

import java.util.concurrent.CountDownLatch;

import org.tinylog.core.TaskExecutor;

/**
 * Thread for enqueuing a given number of tasks ot a  {@link TaskExecutor}.
 */
class FeederThread extends Thread {

    private final int count;
    private final TaskExecutor taskExecutor;
    private final CountDownLatch latch;

    /**
     * @param name The name of the thread
     * @param count The number of tasks to enqueue in the passed task executor
     * @param taskExecutor The task executor implementation to feed with tasks
     * @param latch This latch will be counted down by 1 when finished
     */
    FeederThread(String name, int count, TaskExecutor taskExecutor, CountDownLatch latch) {
        super(name);

        this.count = count;
        this.taskExecutor = taskExecutor;
        this.latch = latch;
    }

    @Override
    public void run() {
        for (int i = 0; i < count; ++i) {
            if (i == count - 1) {
                taskExecutor.enqueue((backend, last) -> latch.countDown());
            } else {
                taskExecutor.enqueue((backend, last) -> { /* Ignore */ });
            }
        }
    }

}
