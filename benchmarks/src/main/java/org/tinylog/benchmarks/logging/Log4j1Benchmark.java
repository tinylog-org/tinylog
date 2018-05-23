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
import java.nio.file.Path;

import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * Benchmark for Apache Log4j 1.
 */
public class Log4j1Benchmark {

	private static final int MAGIC_NUMBER = 42;

	/** */
	public Log4j1Benchmark() {
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
		lifeCycle.logger.debug("Hello " + MAGIC_NUMBER + "!");
	}

	/**
	 * Benchmarks issuing log entries that will be output.
	 *
	 * @param lifeCycle
	 *            Life cycle with logger instance
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void output(final LifeCycle lifeCycle) {
		lifeCycle.logger.info("Hello " + MAGIC_NUMBER + "!");
	}

	/**
	 * Life cycle for initializing and shutting down Log4j.
	 */
	@State(Scope.Benchmark)
	public static class LifeCycle {

		private static final int BUFFER_SIZE = 64 * 1024;

		@Param({ "false", "true" })
		private boolean async;

		private Logger logger;
		private Path file;
		private Appender appender;

		/** */
		public LifeCycle() {
		}

		/**
		 * Initializes Log4j.
		 *
		 * @throws IOException
		 *             Failed creating temporary log file or appender
		 */
		@Setup(Level.Trial)
		public void init() throws IOException {
			file = Files.createTempFile("log4j1_", ".log");
			appender = createAppender(file.toString());

			logger = Logger.getLogger(Log4j1Benchmark.class);
			logger.removeAllAppenders();
			logger.addAppender(appender);
			logger.setLevel(org.apache.log4j.Level.INFO);
		}

		/**
		 * Shuts down Log4j.
		 * 
		 * @throws IOException
		 *             Failed to delete log file
		 */
		@TearDown(Level.Trial)
		public void release() throws IOException {
			appender.close();
			Files.delete(file);
		}

		/**
		 * Creates a file appender.
		 *
		 * @param file
		 *            Path to log file
		 * @return Created appender
		 * @throws IOException
		 *             Failed creating appender
		 */
		private Appender createAppender(final String file) throws IOException {
			Layout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%t] %C.%M(): %m%n");

			if (async) {
				AsyncAppender appender = new AsyncAppender();
				appender.setLocationInfo(true);
				appender.addAppender(new FileAppender(layout, file, false, true, BUFFER_SIZE));
				return appender;
			} else {
				return new FileAppender(layout, file, false, false, 0);
			}
		}

	}

}
