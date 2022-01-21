package org.tinylog.benchmarks.logging.core;

/**
 * Abstract logging benchmark for all logging benchmarks.
 *
 * @param <T> The state for initializing the actual logging framework
 */
public abstract class AbstractLoggingBenchmark<T extends AbstractLoggingState> {

	/**
	 * The magic number to log as argument.
	 */
	protected static final int MAGIC_NUMBER = 42;

	/** */
	public AbstractLoggingBenchmark() {
	}

	/**
	 * Issues debug log entries, which will be discarded and not written into the log file.
	 *
	 * @param state The state for initializing the logging framework
	 */
	public abstract void discard(T state);

	/**
	 * Issues into log entries, which will be written into the log file.
	 *
	 * @param state The state for initializing the logging framework
	 */
	public abstract void output(T state);

}
