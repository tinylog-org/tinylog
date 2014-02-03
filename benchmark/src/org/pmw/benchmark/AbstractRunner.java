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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.Arrays;

import org.pmw.benchmark.dummy.Dummy;

public abstract class AbstractRunner {

	private static final int BENCHMARK_ITERATIONS = 1; // Number of benchmark to run
	private static final int OUTLIERS_CUT = 0; // Number of best and worst results to exclude

	private static final String RESULT_MESSAGE = "{0}: {1} log entries in {2}ms = {3} log entries per second";
	private static final String ERROR_MESSAGE = "{0} lines has been written, but {1} lines expected";

	private final String name;
	private final ILoggingFramework framework;

	AbstractRunner(final String name, final ILoggingFramework framework) {
		this.name = name;
		this.framework = framework;
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
			framework.init(files[i]);

			long start = System.currentTimeMillis();
			run(framework);
			framework.dispose();
			long finished = System.currentTimeMillis();
			times[i] = finished - start;
		}

		long time = calcTime(times);
		long iterations = (BENCHMARK_ITERATIONS - OUTLIERS_CUT * 2) * countTriggeredLogEntries();
		long iterationsPerSecond = Math.round(iterations * 1000d / time);

		System.out.println(MessageFormat.format(RESULT_MESSAGE, name, iterations, time, iterationsPerSecond));

		if (!(framework instanceof Dummy)) {
			long lines = 0;
			for (int i = 0; i < BENCHMARK_ITERATIONS; ++i) {
				BufferedReader reader = new BufferedReader(new FileReader(files[i]));
				while (reader.readLine() != null) {
					++lines;
				}
				reader.close();
			}

			long expected = BENCHMARK_ITERATIONS * countWrittenLogEntries();
			if (lines != expected) {
				System.err.println(MessageFormat.format(ERROR_MESSAGE, lines, expected));
			}
		}

		for (int i = 0; i < BENCHMARK_ITERATIONS; ++i) {
			files[i].delete();
		}
	}

	protected abstract void run(final ILoggingFramework framework) throws Exception;

	protected static ILoggingFramework createLoggingFramework(final String[] arguments) {
		if (arguments.length == 0) {
			System.out.println("Require name of logging framework class as first argument");
			return null;
		}

		try {
			return (ILoggingFramework) Class.forName(arguments[0]).newInstance();
		} catch (ReflectiveOperationException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected abstract long countTriggeredLogEntries();

	protected abstract long countWrittenLogEntries();

	private long calcTime(final long[] times) {
		Arrays.sort(times);
		long time = 0L;
		for (int i = OUTLIERS_CUT; i < BENCHMARK_ITERATIONS - OUTLIERS_CUT; ++i) {
			time += times[i];
		}
		return time;
	}
}
