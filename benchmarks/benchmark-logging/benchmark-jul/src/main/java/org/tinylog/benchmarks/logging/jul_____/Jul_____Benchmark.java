package org.tinylog.benchmarks.logging.jul_____;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.tinylog.benchmarks.logging.core.AbstractLoggingBenchmark;
import org.tinylog.benchmarks.logging.core.LocationInfo;

/**
 * Benchmark for issuing log entries with java.util.logging.
 */
@State(Scope.Thread)
public class Jul_____Benchmark extends AbstractLoggingBenchmark {

	@Param
	private LocationInfo locationInfo;

	private Handler handler;
	private Logger logger;

	/** */
	public Jul_____Benchmark() {
	}

	@Setup(Level.Trial)
	@Override
	public void configure() throws IOException {
		handler = new FileHandler(createLogFile("jul"), false);
		handler.setFormatter(new SimpleFormatter(locationInfo));

		logger = Logger.getLogger(Jul_____Benchmark.class.getName());
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		logger.setLevel(java.util.logging.Level.INFO);
	}

	@TearDown(Level.Trial)
	@Override
	public void shutdown() {
		handler.close();
		logger.removeHandler(handler);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void discard() {
		logger.fine("Hello " + MAGIC_NUMBER + "!");
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void output() {
		logger.info("Hello " + MAGIC_NUMBER + "!");
	}

}
