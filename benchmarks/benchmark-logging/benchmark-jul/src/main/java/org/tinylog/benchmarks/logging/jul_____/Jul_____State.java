package org.tinylog.benchmarks.logging.jul_____;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.tinylog.benchmarks.logging.core.AbstractLoggingState;
import org.tinylog.benchmarks.logging.core.LocationInfo;

/**
 * State for initializing java.util.logging.
 */
@State(Scope.Thread)
public class Jul_____State extends AbstractLoggingState {

	/**
	 * Configured logger for {@link Jul_____Benchmark}.
	 */
	final Logger logger = Logger.getLogger(Jul_____Benchmark.class.getName());

	@Param
	private LocationInfo locationInfo;

	/** */
	public Jul_____State() {
	}

	@Setup(Level.Trial)
	@Override
	public void configure() throws IOException {
		Handler handler = new FileHandler(createLogFile("jul"), false);
		handler.setFormatter(new SimpleFormatter(locationInfo));

		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		logger.setLevel(java.util.logging.Level.INFO);
	}

}
