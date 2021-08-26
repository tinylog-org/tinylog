package org.tinylog.impl.writers;

import org.tinylog.impl.LogEntry;

/**
 * Writer interface for outputting log entries asynchronously.
 *
 * <p>
 *     tinylog executes async writers in a separate writing thread. Hence, the thread, which issues a log entry, is not
 *     blocked and tinylog ensures that async writers are always called by the same writing thread. Therefore, async
 *     writer implementations do not have to take care about thread safety.
 * </p>
 *
 * <p>
 *     Alternately, the base interface {@link Writer} can be used for outputting log entries synchronously.
 * </p>
 */
public interface AsyncWriter extends Writer {

	/**
	 * Flushes the output after passing all currently available log entries to {@link Writer#log(LogEntry)}.
	 *
	 * <p>
	 *     The writing thread calls this method after passing a complete batch of log entries. Thrown exceptions are
	 *     handled by tinylog and have no effect on other writers.
	 * </p>
	 *
	 * @throws Exception Any exception can be thrown, if the flush fails
	 */
	void flush() throws Exception;

}
