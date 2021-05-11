/*
 * Copyright 2021 Martin Winandy
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

package org.tinylog.benchmarks.logging;

/**
 * Base benchmark class for logging framework benchmarks.
 *
 * @param <T> Life cycle implementation for the logging framework to benchmark
 */
public abstract class AbstractBenchmark<T extends AbstractLifeCycle> {

	/**
	 * Number of log entries to issue in {@link #output(AbstractLifeCycle)}.
	 */
	public static final int LOG_ENTRIES = 1_000_000;

	/**
	 * Argument for parameterized log entries.
	 */
	public static final int MAGIC_NUMBER = 42;

	/**
	 * Benchmarks issuing a single log entry that will be discarded.
	 *
	 * @param lifeCycle Life cycle of the logging framework to benchmark
	 */
	public abstract void discard(T lifeCycle);

	/**
	 * Benchmarks issuing a bunch of log entries that will be actually output.
	 *
	 * @param lifeCycle Life cycle of the logging framework to benchmark
	 * @throws Exception Failed to output log entries
	 */
	public abstract void output(T lifeCycle) throws Exception;

	/**
	 * Benchmarks a single logging statement. Used with {@code Mode.SampleTime}
	 * to measure logging latency.
	 *
	 * @param lifeCycle Life cycle of the logging framework to benchmark
	 * @throws Exception Failed to output log entries
	 */
	public abstract void outputSingle(T lifeCycle) throws Exception;

}
