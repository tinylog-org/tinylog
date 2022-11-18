package org.tinylog.impl.writers;

import java.util.Set;

import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;

/**
 * Writer interface for outputting log entries synchronously.
 *
 * <p>
 *     By default, tinylog calls the {@link Writer#log(LogEntry)} synchronously. This means that the thread, which
 *     issues a log entry, is blocked until {@link Writer#log(LogEntry)} terminates and that the writer can be called
 *     by multiple threads in parallel. Therefore, a writer must be ensure thread safety.
 * </p>
 *
 * <p>
 *     Alternately, the interface {@link AsyncWriter} can be used for outputting log entries asynchronously.
 * </p>
 */
public interface Writer extends AutoCloseable {

    /**
     * Returns a set with all required log entry properties used by this writer.
     *
     * <p>
     *     For performance optimization, tinylog may not set properties of {@link LogEntry} instances that a writer
     *     does not define as required.
     * </p>
     *
     * <p>
     *     tinylog calls this method only once during the initialization phase and assumes that the set of required log
     *     entry properties will never change afterwards.
     * </p>
     *
     * @return The set of all required log entry properties
     */
    Set<LogEntryValue> getRequiredLogEntryValues();

    /**
     * Outputs a log entry.
     *
     * <p>
     *     Thrown exceptions are handled by tinylog and do not affect the output of other writers or log entries.
     * </p>
     *
     * @param entry The log entry to output
     * @throws Exception Any exception can be thrown, if the output fails
     */
    void log(LogEntry entry) throws Exception;

    /**
     * Closes the writer when tinylog is shutting down. All allocated resources should be released in this method.
     *
     * <p>
     *     Thrown exceptions are handled by tinylog and have no effect on other writers nor on the graceful shutdown of
     *     tinylog itself.
     * </p>
     *
     * @throws Exception Any exception can be thrown, if closing of resources fails
     */
    @Override
    void close() throws Exception;

}
