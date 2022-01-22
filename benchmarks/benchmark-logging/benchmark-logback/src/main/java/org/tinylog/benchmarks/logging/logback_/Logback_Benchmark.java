package org.tinylog.benchmarks.logging.logback_;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.tinylog.benchmarks.logging.core.AbstractLoggingBenchmark;

/**
 * Benchmark for issuing log entries with Logback.
 */
public class Logback_Benchmark extends AbstractLoggingBenchmark<Logback_State> {

	/** */
	public Logback_Benchmark() {
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void discard(Logback_State state) {
		state.logger.debug("Hello {}!", MAGIC_NUMBER);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void output(Logback_State state) {
		state.logger.info("Hello {}!", MAGIC_NUMBER);
	}

}
