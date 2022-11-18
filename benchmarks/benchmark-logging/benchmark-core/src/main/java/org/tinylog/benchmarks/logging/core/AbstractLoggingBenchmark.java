package org.tinylog.benchmarks.logging.core;

import java.io.File;
import java.io.IOException;

/**
 * Abstract logging benchmark for all logging benchmarks.
 */
public abstract class AbstractLoggingBenchmark {

    /**
     * The magic number to log as argument.
     */
    protected static final int MAGIC_NUMBER = 42;

    /** */
    public AbstractLoggingBenchmark() {
    }

    /**
     * Applies the configuration for the logging framework before executing any benchmarks.
     *
     * @throws Exception Failed to configure the logging framework
     */
    public abstract void configure() throws Exception;

    /**
     * Gets the current log file.
     *
     * @return The current log file
     */
    public abstract String getLogFile();

    /**
     * Shuts the logging framework gracefully down.
     *
     * @throws Exception Failed to shut the logging framework gracefully down
     */
    public abstract void shutdown() throws Exception;

    /**
     * Issues debug log entries, which will be discarded and not written into the log file.
     */
    public abstract void discard();

    /**
     * Issues into log entries, which will be written into the log file.
     */
    public abstract void output();

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
