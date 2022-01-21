package org.tinylog.benchmarks.logging.core;

import java.io.File;
import java.io.IOException;

/**
 * Abstract logging state for initializing the actual logging framework.
 */
public abstract class AbstractLoggingState {

	/**
	 * Applies the configuration for the logging framework before executing any benchmarks.
	 *
	 * @throws Exception Failed to configure the logging framework
	 */
	public abstract void configure() throws Exception;

	/**
	 * Creates a new temporary log file.
	 *
	 * @param name The logging framework name that should be part of the final log file name
	 * @return The absolute path to the created log file
	 * @throws IOException Failed to crate a temporary file
	 */
	protected static String createLogFile(String name) throws IOException {
		File file = File.createTempFile("benchmark_" + name + "_", ".log");
		file.deleteOnExit();
		return file.getAbsolutePath();
	}

}
