package org.tinylog.benchmarks.logging.tinylog3;

import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.tinylog.Logger;
import org.tinylog.benchmarks.logging.core.AbstractLoggingBenchmark;
import org.tinylog.benchmarks.logging.core.LocationInfo;
import org.tinylog.core.Configuration;
import org.tinylog.core.Tinylog;

/**
 * Benchmark for issuing log entries with tinylog 2.
 */
@State(Scope.Thread)
public class Tinylog3Benchmark extends AbstractLoggingBenchmark {

	@Param
	private LocationInfo locationInfo;

	/** */
	public Tinylog3Benchmark() {
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

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void discard() {
		Logger.debug("Hello {}!", MAGIC_NUMBER);
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@Override
	public void output() {
		Logger.info("Hello {}!", MAGIC_NUMBER);
	}

}
