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

package org.pmw.benchmark;

public class MultiThreadBenchmarkRunner extends AbstractBenchmarkRunner {

	private static final int PARALLEL_THREADS = 10;
	private static final int LOGGING_ITERATIONS = 2000;

	public MultiThreadBenchmarkRunner(final IBenchmark benchmark) {
		super(benchmark.getName() + " (multi threaded)", benchmark);
	}

	public static void main(final String[] arguments) throws Exception {
		IBenchmark benchmark = createBenchmark(arguments);
		if (benchmark != null) {
			new MultiThreadBenchmarkRunner(benchmark).start();
		}
	}

	@Override
	protected long countTriggeredLogEntries() {
		return (long) PARALLEL_THREADS * (long) LOGGING_ITERATIONS * 5L; // TRACE, DEBUG, INFO, WARNING and ERROR
	}

	@Override
	protected long countWrittenLogEntries() {
		return (long) PARALLEL_THREADS * (long) LOGGING_ITERATIONS * 3L; // INFO, WARNING and ERROR will be output
	}

	@Override
	protected final void run(final IBenchmark benchmark) {
		ThreadGroup threadGroup = new ThreadGroup("logging");
		for (int i = 0; i < PARALLEL_THREADS; ++i) {
			new Thread(threadGroup, "logging-" + i) {

				@Override
				public void run() {
					for (int i = 0; i < LOGGING_ITERATIONS; ++i) {
						benchmark.trace(i + 1);
						benchmark.debug(i + 1);
						benchmark.info(i + 1);
						benchmark.warning(i + 1);
						benchmark.error(i + 1);
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
	}

}
