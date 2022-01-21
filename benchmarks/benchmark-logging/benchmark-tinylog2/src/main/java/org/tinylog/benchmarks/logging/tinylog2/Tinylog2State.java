package org.tinylog.benchmarks.logging.tinylog2;

import java.io.IOException;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.tinylog.benchmarks.logging.core.AbstractLoggingState;
import org.tinylog.benchmarks.logging.core.LocationInfo;
import org.tinylog.configuration.Configuration;

/**
 * State for initializing tinylog 2.
 */
@State(Scope.Thread)
public class Tinylog2State extends AbstractLoggingState {

	@Param
	private LocationInfo locationInfo;

	/** */
	public Tinylog2State() {
	}

	@Setup(Level.Trial)
	@Override
	public void configure() throws IOException {
		StringBuilder formatPattern = new StringBuilder();
		formatPattern.append("{date:yyyy-MM-dd HH:mm:ss} - {thread}");
		if (locationInfo == LocationInfo.FULL) {
			formatPattern.append(" - {class}.{method}() - ");
		} else if (locationInfo == LocationInfo.CLASS_OR_CATEGORY_ONLY) {
			formatPattern.append(" - {class} - ");
		} else {
			formatPattern.append(" - ");
		}
		formatPattern.append("{level}: {message}");

		Configuration.set("level", "INFO");
		Configuration.set("writer", "file");
		Configuration.set("writer.file", createLogFile("tinylog2"));
		Configuration.set("writer.format", formatPattern.toString());
	}

}
