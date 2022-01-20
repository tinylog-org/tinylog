package org.tinylog.benchmarks.logging.tinylog2;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.tinylog.Logger;

/**
 * Benchmark for issuing log entries with tinylog 2.
 */
public class Tinylog2Benchmark {

	private static final int MAGIC_NUMBER = 42;

	/** */
	public Tinylog2Benchmark() {
	}

	/**
	 * Issues debug log entries, which will be discarded and not written into the log file.
	 *
	 * @param state State for initializing tinylog 2
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void discard(Tinylog2State state) {
		Logger.debug("Hello {}!", MAGIC_NUMBER);
	}

	/**
	 * Issues into log entries, which will be written into the log file.
	 *
	 * @param state State for initializing tinylog 2
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void output(Tinylog2State state) {
		Logger.info("Hello {}!", MAGIC_NUMBER);
	}

}
