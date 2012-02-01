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

public abstract class AbstractBenchmarkRunner {

	private static final int BENCHMARK_ITERATIONS = 100;
	private static final String RESULT_MESSAGE = "{0}: {1} log entries in {2}ms = {3} log entries per second";

	private final String name;
	private final IBenchmark benchmark;

	AbstractBenchmarkRunner(final String name, final IBenchmark benchmark) {
		this.name = name;
		this.benchmark = benchmark;
	}

	public final void start() throws Exception {
		long start = System.currentTimeMillis();

		for (int i = 0; i < BENCHMARK_ITERATIONS; ++i) {
			File file = File.createTempFile("log", ".txt");
			benchmark.init(file);
			run(benchmark);
			benchmark.dispose();
			file.delete();
		}

		long finished = System.currentTimeMillis();
		long time = finished - start;
		long iterations = BENCHMARK_ITERATIONS * countLogEntries();
		long iterationsPerSecond = Math.round(iterations * 1000d / time);
		System.out.println(MessageFormat.format(RESULT_MESSAGE, name, iterations, time, iterationsPerSecond));
	}

	protected abstract int countLogEntries();

	protected abstract void run(IBenchmark benchmark);

}
