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

package org.tinylog.benchmarks.logging;

import java.io.IOException;
import java.nio.file.Files;

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
import org.tinylog.configuration.Configuration;
import org.tinylog.provider.ProviderRegistry;

/**
 * Benchmark for tinylog 2.
 */
public class Tinylog2Benchmark {

	private static final int MAGIC_NUMBER = 42;

	/** */
	public Tinylog2Benchmark() {
	}

	/**
	 * Benchmarks issuing log entries that will be discarded.
	 * 
	 * @param lifeCycle
	 *            Can be ignored
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void discard(final LifeCycle lifeCycle) {
		Logger.debug("Hello {}!", MAGIC_NUMBER);
	}

	/**
	 * Benchmarks issuing log entries that will be output.
	 * 
	 * @param lifeCycle
	 *            Can be ignored
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void output(final LifeCycle lifeCycle) {
		Logger.info("Hello {}!", MAGIC_NUMBER);
	}

	/**
	 * Life cycle for initializing and shutting down tinylog.
	 */
	@State(Scope.Benchmark)
	public static class LifeCycle {

		@Param({ "false", "true" })
		private boolean writingThread;

		/** */
		public LifeCycle() {
		}

		/**
		 * Initializes tinylog.
		 * 
		 * @throws IOException
		 *             Failed creating temporary log file
		 */
		@Setup(Level.Trial)
		public void init() throws IOException {
			Configuration.set("autoshutdown", "false");
			Configuration.set("level", "info");
			Configuration.set("writer", "file");
			Configuration.set("writer.buffered", Boolean.toString(writingThread));
			Configuration.set("writer.file", Files.createTempFile("tinylog2_", ".log").toString());
			Configuration.set("writer.format", "{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}(): {message}");
			Configuration.set("writingthread", Boolean.toString(writingThread));
		}

		/**
		 * Shuts down tinylog.
		 */
		@TearDown(Level.Trial)
		public void shutDown() {
			ProviderRegistry.getLoggingProvider().shutdown();
		}

	}

}
