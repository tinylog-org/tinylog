/*
 * Copyright 2018 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.benchmarks.logging.jul;

import java.util.logging.Logger;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

/**
 * Benchmark for java.util.logging aka JUL.
 */
public class JulBenchmark {

	private static final int MAGIC_NUMBER = 42;

	/** */
	public JulBenchmark() {
	}

	/**
	 * Benchmarks issuing log entries that will be discarded.
	 *
	 * @param lifeCycle
	 *            Life cycle with logger instance
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void discard(final LifeCycle lifeCycle) {
		lifeCycle.getLogger().log(java.util.logging.Level.CONFIG, "Hello {0}!", MAGIC_NUMBER);
	}

	/**
	 * Benchmarks issuing log entries that will be output.
	 *
	 * @param lifeCycle
	 *            Life cycle with logger instance
	 */
	@Benchmark
	@BenchmarkMode(Mode.SingleShotTime)
	public void output(final LifeCycle lifeCycle) {
		Logger logger = lifeCycle.getLogger();
		for (int i = 0; i < 1_000_000; ++i) {
			logger.log(java.util.logging.Level.INFO, "Hello {0}!", MAGIC_NUMBER);
		}
	}

}
