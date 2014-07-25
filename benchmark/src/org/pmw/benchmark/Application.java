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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pmw.benchmark.executors.MultiThreadedOutputBenchmarkExecutor;
import org.pmw.benchmark.executors.MultiThreadedPrimesBenchmarkExecutor;
import org.pmw.benchmark.executors.SingleThreadedOutputBenchmarkExecutor;
import org.pmw.benchmark.executors.SingleThreadedPrimesBenchmarkExecutor;
import org.pmw.benchmark.frameworks.DummyBenchmark;
import org.pmw.benchmark.frameworks.JulBenchmark;
import org.pmw.benchmark.frameworks.Log4j2Benchmark;
import org.pmw.benchmark.frameworks.Log4jBenchmark;
import org.pmw.benchmark.frameworks.LogbackBenchmark;
import org.pmw.benchmark.frameworks.TinylogBenchmark;

public class Application {

	private static final int DEFAULT_RUNS = 1;
	private static final int DEFAULT_OUTLIERS = 0;
	private static final int DEFAULT_DEEP = 0;
	private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() * 2;
	private static final long DEFAULT_ITERATIONS = 20_000L;
	private static final long DEFAULT_PRIME = 100_000L;

	public static void main(final String[] arguments) throws Exception {
		if (arguments.length < 2) {
			showHelp();
		} else {
			List<? extends Benchmark> benchmarks = getBenchmarks(arguments);
			String executorName = getExecutorName(arguments);
			String threadingMode = getThreadingMode(arguments);
			int runs = getIntParameter(arguments, "runs", DEFAULT_RUNS);
			int outliers = getIntParameter(arguments, "outliers", DEFAULT_OUTLIERS);
			int deep = getIntParameter(arguments, "deep", DEFAULT_DEEP);
			int threads = getIntParameter(arguments, "threads", DEFAULT_THREADS);
			long iterations = getLongParameter(arguments, "iterations", DEFAULT_ITERATIONS);
			long prime = getLongParameter(arguments, "prime", DEFAULT_PRIME);

			if (runs <= 0) {
				System.err.println("Minimum number of benchmark runs is 1");
				System.exit(-1);
			}
			if (outliers < 0) {
				System.err.println("Minimum outlier benchmark runs is 0");
				System.exit(-1);
			}
			if (outliers % 2 == 1) {
				System.err.println("Outlier benchmark runs must be divisible by 2");
				System.exit(-1);
			}
			if (outliers >= runs) {
				System.err.println("Number of outlier benchmark runs must be less than total number of benchmark runs");
				System.exit(-1);
			}
			if (deep < 0) {
				System.err.println("Minimum amount of additional stack trace deep is 0");
				System.exit(-1);
			}
			if (!threadingMode.equalsIgnoreCase("single-threaded") && threads <= 0) {
				System.err.println("Minimum number of parallel threads is 1");
				System.exit(-1);
			}
			if (!executorName.equalsIgnoreCase("prime") && iterations <= 0) {
				System.err.println("Minimum number of logging iterations is 1");
				System.exit(-1);
			}
			if (!threadingMode.equalsIgnoreCase("single-threaded") && threads > iterations) {
				System.err.println("Number of parallel threads must be less than total number of logging iterations");
				System.exit(-1);
			}
			if (!threadingMode.equalsIgnoreCase("single-threaded") && iterations % threads != 0) {
				System.err.println("Number of logging iterations must be divisible by number of parallel threads");
				System.exit(-1);
			}
			if (executorName.equalsIgnoreCase("prime") && prime <= 0) {
				System.err.println("Minimum prime is 1");
				System.exit(-1);
			}

			for (Benchmark benchmark : benchmarks) {
				if (!executorName.equalsIgnoreCase("prime")) {
					if (!threadingMode.equalsIgnoreCase("multi-threaded")) {
						new SingleThreadedOutputBenchmarkExecutor(benchmark, runs, outliers, deep, iterations).start();
					}
					if (!threadingMode.equalsIgnoreCase("single-threaded")) {
						new MultiThreadedOutputBenchmarkExecutor(benchmark, runs, outliers, deep, iterations / threads, threads).start();
					}
				}
				if (!executorName.equalsIgnoreCase("output")) {
					if (!threadingMode.equalsIgnoreCase("multi-threaded")) {
						new SingleThreadedPrimesBenchmarkExecutor(benchmark, runs, outliers, deep, prime).start();
					}
					if (!threadingMode.equalsIgnoreCase("single-threaded")) {
						new MultiThreadedPrimesBenchmarkExecutor(benchmark, runs, outliers, deep, prime, threads).start();
					}
				}
			}
		}
	}

	private static List<? extends Benchmark> getBenchmarks(final String[] arguments) {
		String framework = arguments[0];
		if ("all".equalsIgnoreCase(framework)) {
			List<Benchmark> benchmarks = new ArrayList<Benchmark>();
			benchmarks.add(new DummyBenchmark());
			benchmarks.add(new JulBenchmark());
			benchmarks.add(new Log4jBenchmark(false));
			benchmarks.add(new Log4jBenchmark(true));
			benchmarks.add(new Log4j2Benchmark(false));
			benchmarks.add(new Log4j2Benchmark(true));
			benchmarks.add(new LogbackBenchmark(false));
			benchmarks.add(new LogbackBenchmark(true));
			benchmarks.add(new TinylogBenchmark(false));
			benchmarks.add(new TinylogBenchmark(true));
			return benchmarks;
		} else if ("dummy".equalsIgnoreCase(framework)) {
			return Arrays.asList(new DummyBenchmark());
		} else if ("jul".equalsIgnoreCase(framework)) {
			return Arrays.asList(new JulBenchmark());
		} else if ("log4j".equalsIgnoreCase(framework)) {
			return Arrays.asList(new Log4jBenchmark(false), new Log4jBenchmark(true));
		} else if ("log4j2".equalsIgnoreCase(framework)) {
			return Arrays.asList(new Log4j2Benchmark(false), new Log4j2Benchmark(true));
		} else if ("logback".equalsIgnoreCase(framework)) {
			return Arrays.asList(new LogbackBenchmark(false), new LogbackBenchmark(true));
		} else if ("tinylog".equalsIgnoreCase(framework)) {
			return Arrays.asList(new TinylogBenchmark(false), new TinylogBenchmark(true));
		} else {
			System.err.println("Unknown framework \"" + framework + "\"");
			System.exit(-1);
			return null;
		}
	}

	private static String getExecutorName(final String[] arguments) {
		String name = arguments[1];
		if ("all".equalsIgnoreCase(name) || "output".equalsIgnoreCase(name) || "prime".equalsIgnoreCase(name)) {
			return name.toLowerCase();
		} else {
			System.err.println("Unknown benchmark \"" + name + "\"");
			System.exit(-1);
			return null;
		}
	}

	private static String getThreadingMode(final String[] arguments) {
		if (arguments.length < 3 || arguments[2].startsWith("-")) {
			return "both";
		} else {
			String mode = arguments[2];
			if ("both".equalsIgnoreCase(mode) || "single-threaded".equalsIgnoreCase(mode) || "multi-threaded".equalsIgnoreCase(mode)) {
				return mode.toLowerCase();
			} else {
				System.err.println("Unknown threading mode \"" + mode + "\"");
				System.exit(-1);
				return null;
			}
		}
	}

	private static int getIntParameter(final String[] arguments, final String name, final int defaultValue) {
		String value = getStringParameter(arguments, name);
		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException ex) {
				System.err.println("Invalid number \"" + value + "\"");
				System.exit(-1);
				return defaultValue;
			}
		}
	}

	private static long getLongParameter(final String[] arguments, final String name, final long defaultValue) {
		String value = getStringParameter(arguments, name);
		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException ex) {
				System.err.println("Invalid number \"" + value + "\"");
				System.exit(-1);
				return defaultValue;
			}
		}
	}

	private static String getStringParameter(final String[] arguments, final String name) {
		String value = getValueForParameter(arguments, "--" + name);
		if (value == null) {
			value = getValueForParameter(arguments, "-" + name.substring(0, 1));
		}
		return value;
	}

	private static String getValueForParameter(final String[] arguments, final String parameter) {
		for (int i = 0; i < arguments.length; ++i) {
			if (parameter.equalsIgnoreCase(arguments[i])) {
				if (i >= arguments.length - 1) {
					System.err.println("Value is missing for \"" + parameter + "\"");
					System.exit(-1);
					return null;
				} else {
					return arguments[i + 1];
				}
			}
		}
		return null;
	}

	private static void showHelp() {
		System.out.println("Run logging framework benchmarks.");
		System.out.println();
		System.out.println("  framework benchmark [-runs X] [-outliers X] [-deep X] [-iterations X] [-threads X] [-prime X]");
		System.out.println();
		System.out.println("  framework          Name of logging framework or \"all\"");
		System.out.println("  benchmark          Name of benchmark or \"all\"");
		System.out.println("  threading          \"single-threaded\" or \"multi-threaded\" execution (default is \"both\")");
		System.out.println();
		System.out.println("  -r --runs X        Number of benchmark runs (default is 1)");
		System.out.println("  -o --outliers X    Number of outlier benchmark runs to exclude from result (default is 0)");
		System.out.println("  -d --deep X        Amount of additional stack trace deep for more realistic results (default is 0)");
		System.out.println("  -t --threads X     Number of parallel threads in multi-threaded benchmarks (default is number of cores * 2)");
		System.out.println("  -i --iterations X  Number of logging iterations in output benchmark (default is 20,000)");
		System.out.println("  -p --prime X       Maximum prime to calculate in prime benchmark (default is 100,000)");
		System.out.println();
		System.out.println("Frameworks");
		System.out.println();
		System.out.println("  jul                JUL (java.util.logging.Logger)");
		System.out.println("  log4j              Apache Log4j 1.x");
		System.out.println("  log4j2             Apache Log4j 2");
		System.out.println("  logback            Logback");
		System.out.println("  tinylog            tinylog");
		System.out.println("  dummy              Without any logging output (helpful for warm-up or calculating logging overhead)");
		System.out.println();
		System.out.println("Benchmarks");
		System.out.println();
		System.out.println("  output             Writes a big amount of log entries to a file (tests the maximum logging output)");
		System.out.println("  prime              Calculates primes and log the results (tests the influence of logging for a CPU intensive program)");
		System.out.println();
	}

}
