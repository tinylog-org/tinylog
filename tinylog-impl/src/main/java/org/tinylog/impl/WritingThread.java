package org.tinylog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.writers.Writer;

/**
 * Thread for writing log entries asynchronously.
 */
public class WritingThread extends Thread {

    private final Collection<Writer> writers;
    private final int queueSize;

    private final Object mutex;

    private List<Task> waitingQueue;
    private List<Task> workingQueue;

    private boolean acceptLogEntries;
    private CompletableFuture<Void> waitingFuture;

    /**
     * @param writers All writers
     * @param queueSize The size for the waiting and working queue
     */
    public WritingThread(Collection<Writer> writers, int queueSize) {
        super("tinylog-writing-thread");

        this.writers = new ArrayList<>(writers);
        this.queueSize = queueSize;

        this.mutex = new Object();

        this.waitingQueue = new ArrayList<>(queueSize);
        this.workingQueue = new ArrayList<>(queueSize);

        this.acceptLogEntries = true;
        this.waitingFuture = new CompletableFuture<>();

        setPriority(Thread.MIN_PRIORITY);
        setDaemon(true);
    }

    /**
     * Adds a log entry to the end of the waiting queue.
     *
     * <p>
     *     If the waiting queue is already full, the current thread is blocked until the passed log entry could be
     *     successfully added to the waiting queue.
     * </p>
     *
     * @param writer The writer to use to output the passed log entry (it must be one of the writer that was passed to
     *               the constructor when this writing thread was instantiated)
     * @param logEntry The log entry to output
     */
    public void enqueue(Writer writer, LogEntry logEntry) {
        Task task = new Task(writer, logEntry);
        while (true) {
            CompletableFuture<Void> future = tryToEnqueue(task);
            if (future == null) {
                return;
            } else {
                future.join();
            }
        }
    }

    /**
     * Outputs all enqueued log entries.
     */
    @Override
    public void run() {
        do {
            waitForLogEntries();
            swapQueues();
        } while (writeLogEntries());
    }

    /**
     * Shuts the writing thread down.
     *
     * <p>
     *     This method does not block the current thread. However, the current thread can call {@link Thread#join()} to
     *     wait for the shutdown.
     * </p>
     */
    public void shutDown() {
        enqueue(null, null);
    }

    /**
     * Tries to add the passed task to the end of the waiting queue.
     *
     * @param task The task to enqueue
     * @return {@code null} if the task was successfully enqueued, otherwise a completable future that can be used
     *         to wait for free place in the waiting queue
     */
    private CompletableFuture<Void> tryToEnqueue(Task task) {
        CompletableFuture<Void> future = null;

        synchronized (mutex) {
            if (acceptLogEntries) {
                if (waitingQueue.size() == queueSize) {
                    future = waitingFuture;
                } else {
                    waitingQueue.add(task);

                    if (task.logEntry == null) {
                        acceptLogEntries = false;
                    }

                    if (waitingQueue.size() == 1) {
                        interrupt();
                    }
                }
            }
        }

        if (future == null || task.logEntry == null || !Objects.equals(InternalLogger.TAG, task.logEntry.getTag())) {
            return future;
        } else {
            return null;
        }
    }

    /**
     * Blocks the thread until new log entries are enqueued.
     */
    private void waitForLogEntries() {
        if (!interrupted()) {
            try {
                sleep(Long.MAX_VALUE);
            } catch (InterruptedException ex) {
                // Ignore
            }
        }
    }

    /**
     * Swaps the waiting and working queues.
     */
    private void swapQueues() {
        CompletableFuture<Void> completedFuture = null;

        synchronized (mutex) {
            List<Task> tempQueue = workingQueue;
            tempQueue.clear();

            workingQueue = waitingQueue;
            waitingQueue = tempQueue;

            if (workingQueue.size() >= queueSize) {
                completedFuture = waitingFuture;
                waitingFuture = new CompletableFuture<>();
            }
        }

        if (completedFuture != null) {
            completedFuture.complete(null);
        }
    }

    /**
     * Writes all log entries from the working queue and flushes all writers afterwards.
     *
     * @return {@code false} if the writhing thread should shut down, {@code true} if the writhing thread should wait
     *         for the next log entries
     */
    private boolean writeLogEntries() {
        for (Task task : workingQueue) {
            if (task.logEntry == null) {
                return false;
            }

            try {
                task.writer.log(task.logEntry);
            } catch (Exception ex) {
                if (!Objects.equals(InternalLogger.TAG, task.logEntry.getTag())) {
                    InternalLogger.error(ex, "Failed to write log entry");
                }
            }
        }

        for (Writer writer : writers) {
            try {
                writer.flush();
            } catch (Exception ex) {
                InternalLogger.error(ex, "Failed to flush writer");
            }
        }

        return true;
    }

    /**
     * Writing task with writer and log entry.
     */
    private static final class Task {

        private final Writer writer;
        private final LogEntry logEntry;

        /**
         * @param writer The writer to use to output the passed log entry
         * @param logEntry The log entry to output
         */
        Task(Writer writer, LogEntry logEntry) {
            this.writer = writer;
            this.logEntry = logEntry;
        }

    }

}
