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

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;

public abstract class AbstractBenchmarkRunner {

	private static final int BENCHMARK_ITERATIONS = 120;
	private static final int OUTLIERS_CUT = 10;
	private static final String RESULT_MESSAGE = "{0}: {1} log entries in {2}ms = {3} log entries per second";

	private final String name;
	private final IBenchmark benchmark;

	AbstractBenchmarkRunner(final String name, final IBenchmark benchmark) {
		this.name = name;
		this.benchmark = benchmark;
	}

	public final void start() throws Exception {
		File[] files = new File[BENCHMARK_ITERATIONS];
		for (int i = 0; i < BENCHMARK_ITERATIONS; ++i) {
			File file = File.createTempFile("log", ".txt");
			file.deleteOnExit();
			files[i] = file;
		}

		long[] times = new long[BENCHMARK_ITERATIONS];
		for (int i = 0; i < BENCHMARK_ITERATIONS; ++i) {
			benchmark.init(files[i]);

			long start = System.currentTimeMillis();
			run(benchmark);
			long finished = System.currentTimeMillis();
			times[i] = finished - start;

			benchmark.dispose();
		}

		long time = calcTime(times);
		long iterations = (BENCHMARK_ITERATIONS - OUTLIERS_CUT * 2) * countLogEntries();
		long iterationsPerSecond = Math.round(iterations * 1000d / time);

		for (int i = 0; i < BENCHMARK_ITERATIONS; ++i) {
			files[i].delete();
		}

		System.out.println(MessageFormat.format(RESULT_MESSAGE, name, iterations, time, iterationsPerSecond));
	}

	protected abstract int countLogEntries();

	protected abstract void run(IBenchmark benchmark);

	private long calcTime(final long[] times) {
		Arrays.sort(times);
		long time = 0L;
		for (int i = OUTLIERS_CUT; i < BENCHMARK_ITERATIONS - OUTLIERS_CUT * 2; ++i) {
			time += times[i];
		}
		return time;
	}
}
