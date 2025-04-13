package org.tinylog.test.util;

import org.tinylog.core.Task;
import org.tinylog.core.TaskExecutor;
import org.tinylog.core.backend.LoggingBackend;

/**
 * Task executor implementation that executes tasks directly and synchronously.
 */
public class SynchronousTaskExecutor implements TaskExecutor {

    private final LoggingBackend backend;

    /**
     * @param backend The logging backend to use for tasks
     */
    public SynchronousTaskExecutor(LoggingBackend backend) {
        this.backend = backend;
    }

    @Override
    public boolean enqueue(Task task) {
        synchronized (backend) {
            task.run(backend, true);
        }

        return true;
    }

}
