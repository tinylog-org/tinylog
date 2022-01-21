package org.tinylog.benchmarks.logging.tinylog2;

import java.io.IOException;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.tinylog.benchmarks.logging.core.AbstractLoggingState;
import org.tinylog.configuration.Configuration;

/**
 * State for initializing tinylog 2.
 */
@State(Scope.Thread)
public class Tinylog2State extends AbstractLoggingState {

	private static final String FORMAT_PATTERN =
		"{date:yyyy-MM-dd HH:mm:ss} - {thread} - {class}.{method}() - {level}: {message}";

	/** */
	public Tinylog2State() {
	}

	@Setup(Level.Trial)
	@Override
	public void configure() throws IOException {
		Configuration.set("level", "INFO");
		Configuration.set("writer", "file");
		Configuration.set("writer.file", createLogFile("tinylog2"));
		Configuration.set("writer.format", FORMAT_PATTERN);
	}

}
