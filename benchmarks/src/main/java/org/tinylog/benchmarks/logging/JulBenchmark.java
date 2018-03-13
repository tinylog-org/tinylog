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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

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
		lifeCycle.logger.log(java.util.logging.Level.CONFIG, "Hello {}!", MAGIC_NUMBER);
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
		lifeCycle.logger.log(java.util.logging.Level.INFO, "Hello {}!", MAGIC_NUMBER);
	}

	/**
	 * Life cycle for initializing and shutting down java.util.logging.
	 */
	@State(Scope.Benchmark)
	public static class LifeCycle {

		private Logger logger;
		private FileHandler handler;

		/** */
		public LifeCycle() {
		}

		/**
		 * Initializes java.util.logging.
		 *
		 * @throws IOException
		 *             Failed creating temporary log file
		 */
		@Setup(Level.Trial)
		public void init() throws IOException {
			logger = Logger.getLogger(JulBenchmark.class.getName());
			handler = new FileHandler(Files.createTempFile("log4j1_", ".log").toString(), false);
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
			logger.setUseParentHandlers(false);
			logger.setLevel(java.util.logging.Level.INFO);
		}

		/**
		 * Shuts down java.util.logging.
		 */
		@TearDown(Level.Trial)
		public void release() {
			handler.close();
			logger.removeHandler(handler);
		}

	}

	/**
	 * Simple formatter for formatting log records.
	 */
	private static final class SimpleFormatter extends Formatter {

		private static final String NEW_LINE = System.getProperty("line.separator");

		private final SimpleDateFormat formatter;

		/** */
		private SimpleFormatter() {
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}

		@Override
		public String format(final LogRecord record) {
			String date;
			synchronized (formatter) {
				date = formatter.format(new Date());
			}

			StringBuilder builder = new StringBuilder();
			builder.append(date);
			builder.append(" [");
			builder.append(Thread.currentThread().getName());
			builder.append("] ");
			builder.append(record.getSourceClassName());
			builder.append(".");
			builder.append(record.getSourceMethodName());
			builder.append("(): ");
			builder.append(formatMessage(record));

			if (record.getThrown() != null) {
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				printWriter.println();
				record.getThrown().printStackTrace(printWriter);
				printWriter.close();
				builder.append(stringWriter.toString());
			}

			builder.append(NEW_LINE);

			return builder.toString();
		}

	}
}
