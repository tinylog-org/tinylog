package org.tinylog.benchmarks.logging.tinylog2;

import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.tinylog.Logger;
import org.tinylog.benchmarks.logging.core.AbstractLoggingBenchmark;
import org.tinylog.benchmarks.logging.core.LocationInfo;
import org.tinylog.configuration.Configuration;
import org.tinylog.provider.ProviderRegistry;

/**
 * Benchmark for issuing log entries with tinylog 2.
 */
@State(Scope.Thread)
public class Tinylog2Benchmark extends AbstractLoggingBenchmark {

	@Param
	private LocationInfo locationInfo;

	/** */
	public Tinylog2Benchmark() {
	}

	/**
	 * @param locationInfo The location information details to log
	 */
	public Tinylog2Benchmark(LocationInfo locationInfo) {
		this.locationInfo = locationInfo;
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

		Configuration.set("level", "INFO");
		Configuration.set("writer", "file");
		Configuration.set("writer.file", createLogFile("tinylog2"));
		Configuration.set("writer.format", formatPattern.toString());
	}

	@TearDown(Level.Trial)
	@Override
	public void shutdown() throws InterruptedException {
		ProviderRegistry.getLoggingProvider().shutdown();
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
