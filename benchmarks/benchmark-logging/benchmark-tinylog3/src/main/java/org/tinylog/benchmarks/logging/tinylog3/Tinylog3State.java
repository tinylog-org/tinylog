package org.tinylog.benchmarks.logging.tinylog3;

import java.io.IOException;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.tinylog.benchmarks.logging.core.AbstractLoggingState;
import org.tinylog.benchmarks.logging.core.LocationInfo;
import org.tinylog.core.Configuration;
import org.tinylog.core.Tinylog;

/**
 * State for initializing tinylog 3.
 */
@State(Scope.Thread)
public class Tinylog3State extends AbstractLoggingState {

	@Param
	private LocationInfo locationInfo;

	/** */
	public Tinylog3State() {
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

		Configuration configuration = Tinylog.getConfiguration();
		configuration.set("level", "INFO");
		configuration.set("writer.type", "file");
		configuration.set("writer.file", createLogFile("tinylog3"));
		configuration.set("writer.pattern", formatPattern.toString());
	}

}
