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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.FileSize;

/**
 * Benchmark for Logback.
 */
public class LogbackBenchmark {

	private static final int MAGIC_NUMBER = 42;

	/** */
	public LogbackBenchmark() {
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
	 * Life cycle for initializing and shutting down Logback.
	 */
	@State(Scope.Benchmark)
	public static class LifeCycle {

		private static final int BUFFER_SIZE = 64 * 1024;

		@Param({ "false", "true" })
		private boolean async;

		private LoggerContext context;
		private Logger logger;
		private Path file;
		private Appender<ILoggingEvent> appender;

		/** */
		public LifeCycle() {
		}

		/**
		 * Initializes Logback.
		 *
		 * @throws IOException
		 *             Failed creating temporary log file
		 */
		@Setup(Level.Trial)
		public void init() throws IOException {
			context = (LoggerContext) LoggerFactory.getILoggerFactory();

			logger = context.getLogger(LogbackBenchmark.class);
			logger.setLevel(ch.qos.logback.classic.Level.INFO);

			file = Files.createTempFile("logback_", ".log");

			appender = async ? createAsyncAppender(file.toString()) : createFileAppender(file.toString());
			appender.start();

			context.getLogger(Logger.ROOT_LOGGER_NAME).detachAndStopAllAppenders();
			context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender);
		}

		/**
		 * Shuts down Logback.
		 * 
		 * @throws IOException
		 *             Failed to delete log file
		 */
		@TearDown(Level.Trial)
		public void release() throws IOException {
			appender.stop();
			context.stop();
			Files.delete(file);
		}

		/**
		 * Creates an asynchronous appender that contains a file appender for the given file.
		 *
		 * @param file
		 *            Path to log file
		 * @return Created asynchronous appender
		 */
		private Appender<ILoggingEvent> createAsyncAppender(final String file) {
			AsyncAppender appender = new AsyncAppender();
			appender.setIncludeCallerData(true);
			appender.setDiscardingThreshold(0);
			appender.setContext(context);

			Appender<ILoggingEvent> subAppender = createFileAppender(file);
			subAppender.start();
			appender.addAppender(subAppender);

			return appender;
		}

		/**
		 * Creates a file appender for the given file.
		 *
		 * @param file
		 *            Path to log file
		 * @return Created file appender
		 */
		private Appender<ILoggingEvent> createFileAppender(final String file) {
			FileAppender<ILoggingEvent> appender = new FileAppender<ILoggingEvent>();
			appender.setContext(context);
			appender.setAppend(false);
			appender.setBufferSize(new FileSize(BUFFER_SIZE));
			appender.setImmediateFlush(!async);
			appender.setFile(file);

			PatternLayoutEncoder encoder = new PatternLayoutEncoder();
			encoder.setContext(context);
			encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss} [%thread] %class.%method\\(\\): %message%n");
			encoder.setImmediateFlush(!async);
			encoder.start();
			appender.setEncoder(encoder);

			return appender;
		}

	}

}
