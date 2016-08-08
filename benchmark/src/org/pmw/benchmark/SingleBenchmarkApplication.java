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

package org.pmw.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.MessageFormat;

import org.pmw.benchmark.benchmarks.Benchmark;
import org.pmw.benchmark.benchmarks.output.MultiThreadedOutputBenchmark;
import org.pmw.benchmark.benchmarks.output.SingleThreadedOutputBenchmark;
import org.pmw.benchmark.benchmarks.prime.MultiThreadedPrimesBenchmark;
import org.pmw.benchmark.benchmarks.prime.SingleThreadedPrimesBenchmark;
import org.pmw.benchmark.frameworks.Dummy;
import org.pmw.benchmark.frameworks.Framework;

/**
 * Should be ONLY executed by {@link MainApplication} to execute a single benchmark.
 */
public final class SingleBenchmarkApplication extends AbstractApplication {

	private static final String ERROR_LINES_COUNT_MESSAGE = "{0} lines have been written, but {1} lines expected";
	private static final String ERROR_INVALID_LINES_MESSAGE = "Found {0} invalid log entries";

	public static void main(final String[] arguments) {
		if (arguments.length != 9) {
			System.err.println(MainApplication.class.getName() + " should be used");
			System.exit(-1);
		} else {
			try {
				Framework framework = createFramework(arguments[0], Boolean.parseBoolean(arguments[1]), Boolean.parseBoolean(arguments[2]));
				boolean locationInformation = Boolean.parseBoolean(arguments[1]);
				String benchmark = arguments[3];
				String threadingMode = arguments[4];
				int depth = Integer.parseInt(arguments[5]);
				int threads = Integer.parseInt(arguments[6]);
				long iterations = Long.parseLong(arguments[7]);
				long prime = Long.parseLong(arguments[8]);

				if ("output".equals(benchmark)) {
					if ("single-threaded".equals(threadingMode)) {
						execute(new SingleThreadedOutputBenchmark(framework, locationInformation, depth, iterations));
					} else if ("multi-threaded".equals(threadingMode)) {
						execute(new MultiThreadedOutputBenchmark(framework, locationInformation, depth, iterations / threads, threads));
					}
				} else if ("primes".equals(benchmark)) {
					if ("single-threaded".equals(threadingMode)) {
						execute(new SingleThreadedPrimesBenchmark(framework, locationInformation, depth, prime));
					} else if ("multi-threaded".equals(threadingMode)) {
						execute(new MultiThreadedPrimesBenchmark(framework, locationInformation, depth, prime, threads));
					}
				}

				System.err.println("Could not find benchmark " + benchmark + " (" + threadingMode + ")");
				System.exit(-1);
			} catch (Throwable ex) {
				ex.printStackTrace();
				System.exit(-1);
			}
		}
	}

	private static void execute(final Benchmark benchmark) throws Exception {
		File file = File.createTempFile("log", ".txt");
		file.deleteOnExit();

		Framework framework = benchmark.getFramework();
		long start = System.currentTimeMillis();
		framework.init(file);
		benchmark.run();
		framework.dispose();
		long finished = System.currentTimeMillis();

		long totalLines = 0;
		long invalidLines = 0;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			++totalLines;
			if (!benchmark.isValidLogEntry(line)) {
				++invalidLines;
			}
		}
		reader.close();

		long expected = framework instanceof Dummy ? 0 : benchmark.countWrittenLogEntries();
		if (totalLines != expected) {
			System.err.println(MessageFormat.format(ERROR_LINES_COUNT_MESSAGE, totalLines, expected));
		}
		if (invalidLines > 0) {
			System.err.println(MessageFormat.format(ERROR_INVALID_LINES_MESSAGE, invalidLines));
		}

		file.delete();

		System.exit((int) (finished - start));
	}

}
