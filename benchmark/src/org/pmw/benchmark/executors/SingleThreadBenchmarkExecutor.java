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

import org.pmw.benchmark.Benchmark;

public final class SingleThreadBenchmarkExecutor extends AbstractDeepStackTraceBenchmarkExecutor {

	private static final String NAME = "single threaded";

	private final long iterations;

	public SingleThreadBenchmarkExecutor(final Benchmark benchmark, final int runs, final int outliers, final int deep, final long iterations) {
		super(benchmark, runs, outliers, deep);
		this.iterations = iterations;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected final long countTriggeredLogEntries() {
		return iterations * 5L; // TRACE, DEBUG, INFO, WARNING and ERROR
	}

	@Override
	protected final long countWrittenLogEntries() {
		return iterations * 3L; // INFO, WARNING and ERROR will be output
	}

	@Override
	protected final void nestedRun() throws Exception {
		for (long i = 0; i < iterations; ++i) {
			benchmark.write(i + 1);
		}
	}

}
