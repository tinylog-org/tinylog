package org.tinylog.benchmarks.logging.tinylog3;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.tinylog.Logger;

public class Tinylog3Benchmark {

	private static final int MAGIC_NUMBER = 42;

	public Tinylog3Benchmark() {
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void discard(Tinylog3State state) {
		Logger.debug("Hello {}!", MAGIC_NUMBER);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void output(Tinylog3State state) {
		Logger.info("Hello {}!", MAGIC_NUMBER);
	}

}
