package org.tinylog.impl.writer;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Writer interface for outputting log entries.
 *
 * <p>
 *     tinylog guarantees that writers are always called by the same thread. Therefore, writer implementations do not
 *     have to take care about thread safety.
 * </p>
 */
public interface Writer extends AutoCloseable {

    /**
     * Returns the output details for this writer.
     *
     * <p>
     *     tinylog calls this method only once during the initialization phase and assumes that the output details will
     *     never change afterwards.
     * </p>
     *
     * @return The output detail for this writer
     */
    OutputDetails getOutputDetails();

    /**
     * Outputs a log entry.
     *
     * <p>
     *     Thrown exceptions are handled by tinylog and do not affect the output of other writers or log entries.
     * </p>
     *
     * @param entry The log entry to output
     * @throws Exception If the output fails, any exception can be thrown
     */
    void log(LogEntry entry) throws Exception;

    /**
     * Flushes the output after passing all currently available log entries.
     *
     * <p>
     *     This method is called after completing the output of a batch of log entries. Thrown exceptions are handled by
     *     tinylog and have no side effects on other writers.
     * </p>
     *
     * @throws Exception If the flush fails, any exception can be thrown
     */
    void flush() throws Exception;

    /**
     * Closes the writer when shutting down. All allocated resources should be released in this method.
     *
     * <p>
     *     Thrown exceptions are handled by tinylog and have no effect on other writers nor on the graceful shutdown of
     *     tinylog itself.
     * </p>
     *
     * @throws Exception If closing of resources fails, any exception can be thrown
     */
    @Override
    void close() throws Exception;

}
