package org.tinylog.benchmarks.caller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CallerBenchmarkTest {

	private CallerBenchmark benchmark;

	/**
	 * Initializes the {@link CallerBenchmark} instance.
	 */
	@BeforeEach
	void init() {
		benchmark = new CallerBenchmark();
		benchmark.init();
	}

	/**
	 * Verifies that {@link CallerBenchmark#throwable()} returns the expected fully-qualified caller class.
	 */
	@Test
	void throwable() {
		assertThat(benchmark.throwable()).isEqualTo(CallerBenchmarkTest.class.getName());
	}

	/**
	 * Verifies that {@link CallerBenchmark#thread()} returns the expected fully-qualified caller class.
	 */
	@Test
	void thread() {
		assertThat(benchmark.thread()).isEqualTo(CallerBenchmarkTest.class.getName());
	}

	/**
	 * Verifies that {@link CallerBenchmark#stackFrameStream()} returns the expected fully-qualified caller class.
	 */
	@Test
	void stackFrameStream() {
		assertThat(benchmark.stackFrameStream()).isEqualTo(CallerBenchmarkTest.class.getName());
	}

	/**
	 * Verifies that {@link CallerBenchmark#callerClass()} returns the expected fully-qualified caller class.
	 */
	@Test
	void callerClass() {
		assertThat(benchmark.callerClass()).isEqualTo(CallerBenchmarkTest.class.getName());
	}

	/**
	 * Verifies that {@link CallerBenchmark#callerSupplier()} returns the expected fully-qualified caller class.
	 */
	@Test
	void callerSupplier() {
		assertThat(benchmark.callerSupplier()).isEqualTo(CallerBenchmarkTest.class.getName());
	}

	/**
	 * Verifies that {@link CallerBenchmark#securityManager()} returns the expected fully-qualified caller class.
	 */
	@Test
	void securityManager() {
		assertThat(benchmark.securityManager()).isEqualTo(CallerBenchmarkTest.class.getName());
	}

}
