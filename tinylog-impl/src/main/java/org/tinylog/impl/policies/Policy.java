package org.tinylog.impl.policies;

import java.nio.file.Path;

import org.tinylog.impl.writers.file.FileWriter;

/**
 * Policy interface for triggering rollover events for {@link FileWriter}.
 */
public interface Policy {

	/**
	 * Checks if an already existing log file can be continued before opening it.
	 *
	 * @param file The log file to check
	 * @return {@code true} if the passed log file can be continued, {@code false} if a new log file has to be started
	 * @throws Exception Failed to check the passed log file
	 */
	boolean canContinueFile(Path file) throws Exception;

	/**
	 * Initializes this policy for the current log file.
	 *
	 * <p>
	 *     This method is called after opening a log file and before the first call of {@link #canAcceptLogEntry(int)}
	 *     for the current log file.
	 * </p>
	 *
	 * @param file The current log file
	 * @throws Exception Failed to initialize this policy for the passed log file
	 */
	void init(Path file) throws Exception;


	/**
	 * Checks if the number of bytes of the next log entry can be still written to the current log file.
	 *
	 * @param bytes The size in bytes of the next log entry to be written
	 * @return {@code true} if the next log entry can be written to the current log file, {@code false} if a new log
	 *         file has to be started
	 */
	boolean canAcceptLogEntry(int bytes);

}
