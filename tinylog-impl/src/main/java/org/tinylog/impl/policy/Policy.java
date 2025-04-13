package org.tinylog.impl.policy;

import java.nio.file.Path;

import org.tinylog.impl.writer.file.FileWriter;

/**
 * Policy interface for triggering rollover events for the {@link FileWriter}.
 */
public interface Policy {

    /**
     * Checks if an already existing log file can be continued before opening it.
     *
     * @param file The log file to check
     * @return {@code true} if the passed log file can be continued, {@code false} if a new log file has to be started
     * @throws Exception If failed to access the passed log file
     */
    boolean canContinueFile(Path file) throws Exception;

    /**
     * Initializes this policy for the passed log file.
     *
     * <p>
     *     This method is called after opening a log file and before the first call of {@link #canAcceptDataRecord(int)}
     *     for the current log file.
     * </p>
     *
     * @param file The current log file
     * @throws Exception If failed to initialize this policy for the passed log file
     */
    void init(Path file) throws Exception;


    /**
     * Checks if a new data record should still be written to the current log file.
     *
     * @param bytes The size of the new data record in bytes
     * @return {@code true} if the new data record should be written to the current log file, {@code false} if a new log
     *         file should to be started
     */
    boolean canAcceptDataRecord(int bytes);

}
