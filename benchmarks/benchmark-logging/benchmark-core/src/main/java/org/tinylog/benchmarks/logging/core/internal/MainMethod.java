package org.tinylog.benchmarks.logging.core.internal;

import java.io.IOException;

import org.openjdk.jmh.Main;

/**
 * Functional interface for JMH's main method.
 *
 * @see Main#main(String[])
 */
@FunctionalInterface
public interface MainMethod {

    /**
     * Executes the application.
     *
     * @param arguments The passed arguments
     * @throws IOException Failed to execute command line
     */
    void execute(String[] arguments) throws IOException;

}
