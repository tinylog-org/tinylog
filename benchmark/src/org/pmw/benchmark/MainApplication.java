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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Main application for executing benchmarks with an user friendly console interface.
 */
public class MainApplication extends AbstractApplication {

	private static final boolean DEFAULT_LOCATION_INFORMATION = true;
	private static final int DEFAULT_RUNS = 1;
	private static final int DEFAULT_OUTLIERS = 0;
	private static final int DEFAULT_DEEP = 0;
	private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() * 2;
	private static final long DEFAULT_ITERATIONS = 20_000L;
	private static final long DEFAULT_PRIME = 200_000L;

	public static void main(final String[] arguments) throws Exception {
		if (arguments.length < 2) {
			showHelp();
		} else {
			List<String> frameworks = getFrameworks(arguments);
			List<String> benchmarks = getBenchmarks(arguments);
			List<String> threadingModes = getThreadingModes(arguments);

			boolean locationInformation = getBooleanParameter(arguments, "location", DEFAULT_LOCATION_INFORMATION);

			int runs = getIntParameter(arguments, "runs", DEFAULT_RUNS);
			int outliers = getIntParameter(arguments, "outliers", DEFAULT_OUTLIERS);

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

			int deep = getIntParameter(arguments, "deep", DEFAULT_DEEP);
			int threads = getIntParameter(arguments, "threads", DEFAULT_THREADS);
			long iterations = getLongParameter(arguments, "iterations", DEFAULT_ITERATIONS);
			long prime = getLongParameter(arguments, "prime", DEFAULT_PRIME);

			if (deep < 0) {
				System.err.println("Minimum amount of additional stack trace deep is 0");
				System.exit(-1);
			}
			if (threadingModes.contains("multi-threaded") && threads <= 0) {
				System.err.println("Minimum number of parallel threads is 1");
				System.exit(-1);
			}
			if (benchmarks.contains("output") && iterations <= 0) {
				System.err.println("Minimum number of logging iterations is 1");
				System.exit(-1);
			}
			if (threadingModes.contains("multi-threaded") && threads > iterations) {
				System.err.println("Number of parallel threads must be less than total number of logging iterations");
				System.exit(-1);
			}
			if (threadingModes.contains("multi-threaded") && iterations % threads != 0) {
				System.err.println("Number of logging iterations must be divisible by number of parallel threads");
				System.exit(-1);
			}
			if (benchmarks.contains("prime") && prime <= 0) {
				System.err.println("Minimum prime is 1");
				System.exit(-1);
			}

			Executor executor = new Executor(runs, outliers);
			for (String benchmark : benchmarks) {
				for (String threadingMode : threadingModes) {
					System.out.println(benchmark.toUpperCase() + " BENCHMARK (" + threadingMode.toUpperCase() + ")");
					System.out.println();
					for (String framework : frameworks) {
						executor.run(framework, locationInformation, false, benchmark, threadingMode, deep, threads, iterations, prime);
						if (supportsAsync(framework)) {
							executor.run(framework, locationInformation, true, benchmark, threadingMode, deep, threads, iterations, prime);
						}
					}
					System.out.println();
				}
			}
		}
	}

	private static List<String> getFrameworks(final String[] arguments) {
		String name = arguments[0];

		if ("all".equalsIgnoreCase(name)) {
			return Arrays.asList(FRAMEWORKS);
		}

		for (String framework : FRAMEWORKS) {
			if (framework.equalsIgnoreCase(name)) {
				return Collections.singletonList(framework);
			}
		}

		System.err.println("Unknown framework \"" + name + "\"");
		System.exit(-1);
		return null;
	}

	private static List<String> getBenchmarks(final String[] arguments) {
		String name = arguments[1];

		if ("all".equalsIgnoreCase(name)) {
			return Arrays.asList(BENCHMARKS);
		}

		for (String benchmark : BENCHMARKS) {
			if (benchmark.equalsIgnoreCase(name)) {
				return Collections.singletonList(benchmark);
			}
		}

		System.err.println("Unknown benchmark \"" + name + "\"");
		System.exit(-1);
		return null;
	}

	private static List<String> getThreadingModes(final String[] arguments) {
		if (arguments.length < 3 || arguments[2].startsWith("-")) {
			return Arrays.asList(THREADING);
		} else {
			String mode = arguments[2];

			if ("both".equalsIgnoreCase(mode)) {
				return Arrays.asList(THREADING);
			}

			for (String threading : THREADING) {
				if (threading.equalsIgnoreCase(mode)) {
					return Collections.singletonList(threading);
				}
			}

			System.err.println("Unknown threading mode \"" + mode + "\"");
			System.exit(-1);
			return null;
		}
	}

	private static boolean getBooleanParameter(final String[] arguments, final String name, final boolean defaultValue) {
		String value = getStringParameter(arguments, name);
		if (value == null) {
			return defaultValue;
		} else {
			return Boolean.parseBoolean(value);
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
		System.out.println("  framework benchmark [threading] [--location X] [--runs X] [--outliers X] [--deep X] [--iterations X] [--threads X] [--prime X]");
		System.out.println();
		System.out.println("  framework          Name of logging framework or \"all\"");
		System.out.println("  benchmark          Name of benchmark or \"all\"");
		System.out.println("  threading          \"single-threaded\" or \"multi-threaded\" execution (default is \"both\")");
		System.out.println();
		System.out.println("  -l --location X    Include location information in format pattern (default is true)");
		System.out.println("  -r --runs X        Number of benchmark runs (default is 1)");
		System.out.println("  -o --outliers X    Number of outlier benchmark runs to exclude from result (default is 0)");
		System.out.println("  -d --deep X        Amount of additional stack trace deep for more realistic results (default is 0)");
		System.out.println("  -t --threads X     Number of parallel threads in multi-threaded benchmarks (default is number of cores * 2)");
		System.out.println("  -i --iterations X  Number of logging iterations in output benchmark (default is 20,000)");
		System.out.println("  -p --prime X       Maximum prime to calculate in prime benchmark (default is 200,000)");
		System.out.println();
		System.out.println("Frameworks");
		System.out.println();
		System.out.println("  jul                JUL (java.util.logging.Logger)");
		System.out.println("  log4j1             Apache Log4j 1.x");
		System.out.println("  log4j2             Apache Log4j 2.x");
		System.out.println("  logback            Logback");
		System.out.println("  tinylog            tinylog");
		System.out.println("  dummy              Without any logging output (helpful for calculating logging overhead)");
		System.out.println();
		System.out.println("Benchmarks");
		System.out.println();
		System.out.println("  output             Writes a big amount of log entries to a file (tests the maximum logging output)");
		System.out.println("  primes             Calculates primes and log the results (tests the influence of logging for a CPU intensive program)");
		System.out.println();
		System.out.println("Location information (format pattern for log entries)");
		System.out.println();
		System.out.println("  true               yyyy-MM-dd HH:mm:ss [thread] package.class.method(): message");
		System.out.println("  false              yyyy-MM-dd HH:mm:ss: message");
		System.out.println();
	}

	private static final class Executor {

		private final int runs;
		private final int outliers;

		public Executor(final int runs, final int outliers) {
			this.runs = runs;
			this.outliers = outliers;
		}

		public void run(final String framework, final boolean locationInformation, final boolean async, final String benchmark, final String threadingMode,
				final int deep, final int threads, final long iterations, final long prime) throws Exception {
			int[] times = new int[runs];
			for (int i = 0; i < runs; ++i) {
				int time = execute(framework, locationInformation, async, benchmark, threadingMode, deep, threads, iterations, prime);
				if (time < 0) {
					return;
				} else {
					times[i] += time;
				}
			}

			Arrays.sort(times);
			long time = 0L;
			for (int i = outliers / 2; i < runs - outliers / 2; ++i) {
				time += times[i];
			}
			time = Math.round((double) time / (runs - outliers));

			System.out.println(String.format("%1$-30s %2$10dms", createFramework(framework, locationInformation, async).getName(), time));
		}

		private static int execute(final String framework, final boolean locationInformation, final boolean async, final String benchmark,
				final String threadingMode, final int deep, final int threads, final long iterations, final long prime) throws Exception {
			String jvm = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
			String classpath = System.getProperty("java.class.path");

			List<String> command = new ArrayList<String>();
			command.add(jvm);
			command.add(SingleBenchmarkApplication.class.getName());
			command.add(framework);
			command.add(Boolean.toString(locationInformation));
			command.add(Boolean.toString(async));
			command.add(benchmark);
			command.add(threadingMode);
			command.add(Integer.toString(deep));
			command.add(Integer.toString(threads));
			command.add(Long.toString(iterations));
			command.add(Long.toString(prime));

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			Map<String, String> environment = processBuilder.environment();
			environment.put("CLASSPATH", classpath);

			Process process = processBuilder.start();
			int result = process.waitFor();

			InputStream stream = process.getErrorStream();
			byte[] data = new byte[stream.available()];
			stream.read(data, 0, data.length);
			System.err.print(new String(data));

			stream = process.getInputStream();
			data = new byte[stream.available()];
			stream.read(data, 0, data.length);
			System.out.print(new String(data));

			return result;
		}

	}

}
