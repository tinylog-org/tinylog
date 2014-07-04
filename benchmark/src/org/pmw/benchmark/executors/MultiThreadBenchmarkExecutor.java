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

import java.util.List;
import java.util.Vector;

import org.pmw.benchmark.Benchmark;

public final class MultiThreadBenchmarkExecutor extends AbstractDeepStackTraceBenchmarkExecutor {

	private static final String NAME = "multi threaded";

	private final long iterations;
	private final long threads;

	public MultiThreadBenchmarkExecutor(final Benchmark benchmark, final int runs, final int outliers, final int deep, final long iterations, final long threads) {
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
	protected final void nestedRun() throws Exception {
		final List<Exception> exceptions = new Vector<>();
		ThreadGroup threadGroup = new ThreadGroup("logging");
		for (long i = 0; i < threads; ++i) {
			new Thread(threadGroup, "logging-" + i) {

				@Override
				public void run() {
					try {
						for (long i = 0; i < iterations; ++i) {
							benchmark.write(i + 1);
						}
					} catch (Exception ex) {
						exceptions.add(ex);
					}
				}

			}.start();
		}
		while (threadGroup.activeCount() > 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignored) {
				// Ignore
			}
		}
		if (!exceptions.isEmpty()) {
			throw exceptions.get(0);
		}
	}

}
