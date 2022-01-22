package org.tinylog.benchmarks.logging.log4j2__;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.tinylog.benchmarks.logging.core.AbstractLoggingBenchmark;

/**
 * Benchmark for issuing log entries with Logback.
 */
public class Log4j2__Benchmark extends AbstractLoggingBenchmark<Log4j2__State> {

	/** */
	public Log4j2__Benchmark() {
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void discard(Log4j2__State state) {
		state.logger.debug("Hello {}!", MAGIC_NUMBER);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void output(Log4j2__State state) {
		state.logger.info("Hello {}!", MAGIC_NUMBER);
	}

}
