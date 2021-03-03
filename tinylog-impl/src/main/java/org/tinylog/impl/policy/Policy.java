package org.tinylog.impl.policy;

import java.nio.file.Path;

import org.tinylog.impl.writer.FileWriter;

/**
 * Policy interface for triggering rollover events for {@link FileWriter}.
 */
public interface Policy {

	/**
	 * Checks if an already existing log file can be continued before opening it.
	 *
	 * @param path The log file to check
	 * @return {@code true} if the passed log file can be continued, {@code false} if a new log file has to be started
	 * @throws Exception Failed to check the passed log file
	 */
	boolean canContinueFile(Path path) throws Exception;

	/**
	 * Initializes this policy for the current log file.
	 *
	 * <p>
	 *     This method is called before any calls of {@link #canContinueFile(Path)}.
	 * </p>
	 *
	 * @param path The current log file
	 * @throws Exception Failed to initialize this policy for the passed log file
	 */
	void init(Path path) throws Exception;


	/**
	 * Checks if the number of bytes of the next log entry can be still written to the current log file.
	 *
	 * @param bytes The size in bytes of the next log entry to be written
	 * @return {@code true} if the next log entry can be written to the current log file, {@code false} if a new log
	 *         file has to be started
	 */
	boolean canAcceptLogEntry(int bytes);

}
