/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.benchmark.executors;

import java.util.ArrayList;
import java.util.List;

import org.pmw.benchmark.Benchmark;

public final class MultiThreadedOutputBenchmarkExecutor extends AbstractOutputBenchmarkExecutor {

	private static final String NAME = "output / multi-threaded";

	private final long iterations;
	private final int threads;

	public MultiThreadedOutputBenchmarkExecutor(final Benchmark benchmark, final int runs, final int outliers, final int deep, final long iterations,
			final int threads) {
		super(benchmark, runs, outliers, deep);
		this.iterations = iterations;
		this.threads = threads;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected final long countTriggeredLogEntries() {
		return threads * iterations * 5L; // TRACE, DEBUG, INFO, WARNING and ERROR
	}

	@Override
	protected final long countWrittenLogEntries() {
		return threads * iterations * 3L; // INFO, WARNING and ERROR will be output
	}

	@Override
	protected void run() throws Exception {
		final List<LoggingThread> loggingThreads = new ArrayList<LoggingThread>(threads);

		for (int i = 0; i < threads; ++i) {
			LoggingThread loggingThread = new LoggingThread("logging-" + i);
			loggingThreads.add(loggingThread);
			loggingThread.start();
		}

		for (int i = threads - 1; i >= 0; --i) {
			LoggingThread loggingThread = loggingThreads.get(i);
			while (true) {
				try {
					loggingThread.join();
					break;
				} catch (InterruptedException ex) {
					// Continue
				}
			}
			if (loggingThread.exception != null) {
				throw loggingThread.exception;
			}
		}
	}

	private class LoggingThread extends Thread {

		private volatile Exception exception;

		public LoggingThread(final String name) {
			super(name);
		}

		@Override
		public void run() {
			try {
				write(getAdditionStackTraceDeep(), iterations);
			} catch (Exception ex) {
				exception = ex;
			}
		}

	}

}
