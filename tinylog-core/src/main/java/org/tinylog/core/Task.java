package org.tinylog.core;

import org.tinylog.core.backend.LoggingBackend;

/**
 * Tasks to be enqueued in a {@link TaskExecutor} have to implement this functional interface.
 */
@FunctionalInterface
public interface Task {

    /**
     * Executes the task.
     *
     * <p>
     *     Exceptions should be handled by the task itself.
     * </p>
     *
     * @param backend The currently active logging backend
     * @param last {@code true} if this is the last task that is currently enqueued, {@code false} if there are still
     *             other tasks waiting in the queue
     */
    void run(LoggingBackend backend, boolean last);

}
