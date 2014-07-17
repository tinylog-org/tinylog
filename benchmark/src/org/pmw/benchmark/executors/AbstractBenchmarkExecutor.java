/*
 * Copyright 2014 Martin Winandy
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.pmw.benchmark.Benchmark;
import org.pmw.benchmark.frameworks.DummyBenchmark;

public abstract class AbstractBenchmarkExecutor {

	private static final String RESULT_MESSAGE = "{0} ({1}): {2} log entries in {3}ms = {4} log entries per second";
	private static final String ERROR_LINES_COUNT_MESSAGE = "{0} lines have been written, but {1} lines expected";
	private static final String ERROR_INVALID_LINES_MESSAGE = "Found {0} invalid log entries";

	protected final Benchmark benchmark;

	private final int runs;
	private final int outliers;

	protected AbstractBenchmarkExecutor(final Benchmark benchmark, final int runs, final int outliers) {
		this.benchmark = benchmark;
		this.runs = runs;
		this.outliers = outliers;
	}

	public abstract String getName();

	public final void start() throws Exception {
		File[] files = new File[runs];
		for (int i = 0; i < runs; ++i) {
			File file = File.createTempFile("log", ".txt");
			file.deleteOnExit();
			files[i] = file;
		}

		long[] times = new long[runs];
		for (int i = 0; i < runs; ++i) {
			benchmark.init(files[i]);
			long start = System.currentTimeMillis();
			run();
			benchmark.dispose();
			long finished = System.currentTimeMillis();
			times[i] = finished - start;
		}

		long time = Math.round((double) calculateTotalTime(times) / (runs - outliers));
		long countedIterations = countTriggeredLogEntries();
		long iterationsPerSecond = Math.round(countedIterations * 1000d / time);

		System.out.println(MessageFormat.format(RESULT_MESSAGE, benchmark.getName(), getName(), countedIterations, time, iterationsPerSecond));

		long totalLines = 0;
		long invalidLines = 0;
		for (int i = 0; i < runs; ++i) {
			BufferedReader reader = new BufferedReader(new FileReader(files[i]));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				++totalLines;
				if (!isValidLogEntry(line)) {
					++invalidLines;
				}
			}
			reader.close();
		}

		long expected = benchmark instanceof DummyBenchmark ? 0 : runs * countWrittenLogEntries();
		if (totalLines != expected) {
			System.err.println(MessageFormat.format(ERROR_LINES_COUNT_MESSAGE, totalLines, expected));
		}
		if (invalidLines > 0) {
			System.err.println(MessageFormat.format(ERROR_INVALID_LINES_MESSAGE, invalidLines));
		}

		for (int i = 0; i < runs; ++i) {
			files[i].delete();
		}
	}

	protected abstract long countTriggeredLogEntries();

	protected abstract long countWrittenLogEntries();

	protected abstract void run() throws Exception;

	protected boolean isValidLogEntry(final String line) {
		if (!line.contains(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)))) {
			return false;
		}

		if (!line.contains(benchmark.getClass().getName())) {
			return false;
		}

		return true;
	}

	private long calculateTotalTime(final long[] times) {
		Arrays.sort(times);
		long time = 0L;
		for (int i = outliers / 2; i < runs - outliers / 2; ++i) {
			time += times[i];
		}
		return time;
	}

}
