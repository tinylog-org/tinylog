package org.tinylog.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.tinylog.core.Task;
import org.tinylog.core.TaskExecutor;
import org.tinylog.core.backend.LoggingBackend;

/**
 * Task executor implementation that executes tasks asynchronously in a separate thread. Other threads can enqueue
 * tasks in parallel without blocking the execution.
 */
public class AsynchronousTaskExecutor implements TaskExecutor {

    private static final Task POISON = (backend, last) -> { /* Ignore */ };

    private final int capacity;
    private final BlockingQueue<Task> queue;
    private final Thread thread;
    private final Thread hook;

    private LoggingBackend backend;

    /**
     * @param capacity The capacity of the waiting queue
     */
    public AsynchronousTaskExecutor(int capacity) {
        this.capacity = capacity;
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.thread = new Thread(this::run, "tinylog-task-executor");
        this.hook = new Thread(this::shutdown, "tinylog-shutdown-hook");
    }

    @Override
    public boolean enqueue(Task task) {
        try {
            queue.put(task);
            return true;
        } catch (InterruptedException ex) {
            return false;
        }
    }

    /**
     * Starts the execution of enqueued tasks.
     *
     * @param backend The logging backend to use for tasks
     */
    public void start(LoggingBackend backend) {
        this.backend = backend;

        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        thread.start();

        Runtime.getRuntime().addShutdownHook(hook);
    }

    /**
     * Stops the further asynchronous task execution.
     *
     * @throws InterruptedException If the current thread is interrupted while waiting
     */
    public void stop() throws InterruptedException {
        queue.put(POISON);
        thread.join();
        Runtime.getRuntime().removeShutdownHook(hook);
    }

    /**
     * Executes enqueued tasks.
     */
    private void run() {
        List<Task> tasks = new ArrayList<>(capacity);

        while (true) {
            if (queue.drainTo(tasks, capacity) == 0) {
                if (thread.isInterrupted()) {
                    backend.close();
                    return;
                } else {
                    try {
                        tasks.add(queue.take());
                    } catch (InterruptedException ex) {
                        thread.interrupt();
                    }
                }
            }

            Iterator<Task> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                Task task = iterator.next();

                if (task == POISON) {
                    backend.close();
                    return;
                }

                boolean last = !iterator.hasNext();
                task.run(backend, last);
            }

            tasks.clear();
        }
    }

    /**
     * Stops the further asynchronous task execution.
     */
    private void shutdown() {
        try {
            queue.put(POISON);
            thread.join();
        } catch (InterruptedException ex) {
            // Ignore
        }
    }

}
