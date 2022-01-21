package org.tinylog.benchmarks.logging.tinylog2;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.tinylog.Logger;
import org.tinylog.benchmarks.logging.core.AbstractLoggingBenchmark;

/**
 * Benchmark for issuing log entries with tinylog 2.
 */
public class Tinylog2Benchmark extends AbstractLoggingBenchmark<Tinylog2State> {

	/** */
	public Tinylog2Benchmark() {
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void discard(Tinylog2State state) {
		Logger.debug("Hello {}!", MAGIC_NUMBER);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void output(Tinylog2State state) {
		Logger.info("Hello {}!", MAGIC_NUMBER);
	}

}
