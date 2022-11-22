package org.tinylog.benchmarks.logging.core;

import java.io.IOException;
import java.net.URISyntaxException;

import org.openjdk.jmh.Main;
import org.tinylog.benchmarks.logging.core.internal.BenchmarkExecutor;

/**
 * The main application class for executing logging benchmarks.
 */
public final class Application {

    /** */
    private Application() {
    }

    /**
     * Executes JMH benchmarks along with writing the benchmark name into a file.
     *
     * @param args The arguments for JMH
     * @throws URISyntaxException Failed to convert the location URL of the logger class into a valid URI
     * @throws IOException Failed to execute command line or to write the benchmark nane into the target file
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        new BenchmarkExecutor(Main::main).execute(args);
    }

}
