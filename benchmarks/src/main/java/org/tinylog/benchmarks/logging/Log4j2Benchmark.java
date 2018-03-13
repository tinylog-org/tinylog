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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
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
 * Benchmark for Apache Log4j 2.
 */
public class Log4j2Benchmark {

	private static final int MAGIC_NUMBER = 42;

	/** */
	public Log4j2Benchmark() {
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
		lifeCycle.logger.debug("Hello {}!", MAGIC_NUMBER);
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
		lifeCycle.logger.info("Hello {}!", MAGIC_NUMBER);
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

		/** */
		public LifeCycle() {
		}

		/**
		 * Initializes Log4j.
		 *
		 * @throws IOException
		 *             Failed creating temporary log file or loading configuration
		 */
		@Setup(Level.Trial)
		public void init() throws IOException {
			if (async) {
				System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
			}

			String logFile = Files.createTempFile("log4j2_", ".log").toString();

			StringBuilder builder = new StringBuilder();
			builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			builder.append("<Configuration>");
			builder.append("<Appenders>");
			builder.append("<File name=\"file\" fileName=\"" + logFile + "\"");
			builder.append(" bufferedIO=\"" + async + "\" bufferSize=\"" + BUFFER_SIZE + "\">");
			builder.append("<PatternLayout><Pattern>%d{yyyy-MM-dd HH:mm:ss} [%t] %C.%M(): %m%n</Pattern></PatternLayout>");
			builder.append("</File>");
			builder.append("</Appenders>");
			builder.append("<Loggers>");
			builder.append("<Root level=\"info\" includeLocation=\"true\">");
			builder.append("<AppenderRef ref=\"file\"/>");
			builder.append("</Root>");
			builder.append("</Loggers>");
			builder.append("</Configuration>");

			byte[] configuration = builder.toString().getBytes(StandardCharsets.UTF_8);
			ConfigurationSource source = new ConfigurationSource(new ByteArrayInputStream(configuration));
			Configurator.initialize(null, source);

			logger = LogManager.getLogger(Log4j2Benchmark.class);
		}

		/**
		 * Shuts down Log4j.
		 */
		@TearDown(Level.Trial)
		public void release() {
			LogManager.shutdown();
		}

	}

}
