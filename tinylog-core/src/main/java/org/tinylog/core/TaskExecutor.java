package org.tinylog.core;

/**
 * Executor for executing tasks.
 *
 * @see Task
 */
public interface TaskExecutor {

    /**
     * Adds a new task that should be executed by the task executor.
     *
     * @param task A new task that should be executed
     * @return {@code true} if the task was successfully enqueued, {@code false} if the task could not be enqueued
     */
    boolean enqueue(Task task);

}
