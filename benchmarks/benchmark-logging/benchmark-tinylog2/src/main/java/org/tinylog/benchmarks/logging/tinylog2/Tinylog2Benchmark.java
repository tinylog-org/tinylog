package org.tinylog.benchmarks.logging.tinylog2;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.tinylog.Logger;

public class Tinylog2Benchmark {

	private static final int MAGIC_NUMBER = 42;

	public Tinylog2Benchmark() {
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void discard(Tinylog2State state) {
		Logger.debug("Hello {}!", MAGIC_NUMBER);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void output(Tinylog2State state) {
		Logger.info("Hello {}!", MAGIC_NUMBER);
	}

}
