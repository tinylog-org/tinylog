package org.tinylog.benchmarks.logging.core.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ServiceLoader;

import org.openjdk.jmh.Main;
import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

/**
 * Executor for executing JMH benchmarks along with writing the benchmark name into a file.
 *
 * @see ProjectLocation#BENCHMARK_NAME_FILE
 */
public class BenchmarkExecutor {

    private final MainMethod main;

    /**
     * @param main The main method of JMH
     * @see Main#main
     */
    public BenchmarkExecutor(MainMethod main) {
        this.main = main;
    }

    /**
     * Executes JMH benchmarks along with writing the benchmark name into a file.
     *
     * @param arguments The arguments for JMH
     * @throws URISyntaxException Failed to convert the location URL of the logger class into a valid URI
     * @throws IOException Failed to execute command line or to write the benchmark nane into the target file
     */
    public void execute(String... arguments) throws URISyntaxException, IOException {
        ServiceLoader<BenchmarkInfo> serviceLoader = ServiceLoader.load(BenchmarkInfo.class);
        new BenchmarkName(serviceLoader).store();
        main.execute(arguments);
    }

}
