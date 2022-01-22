package org.tinylog.benchmarks.logging.jul_____;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.tinylog.benchmarks.logging.core.AbstractLoggingBenchmark;

/**
 * Benchmark for issuing log entries with java.util.logging.
 */
public class Jul_____Benchmark extends AbstractLoggingBenchmark<Jul_____State> {

	/** */
	public Jul_____Benchmark() {
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void discard(Jul_____State state) {
		state.logger.fine("Hello " + MAGIC_NUMBER + "!");
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void output(Jul_____State state) {
		state.logger.info("Hello " + MAGIC_NUMBER + "!");
	}

}
