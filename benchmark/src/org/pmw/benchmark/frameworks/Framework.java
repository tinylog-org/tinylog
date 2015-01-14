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

package org.pmw.benchmark.frameworks;

import java.io.File;
import java.util.List;

/**
 * Adapter for benchmarking a logging framework.
 */
public interface Framework {

	/**
	 * Name of the framework under test.
	 */
	String getName();

	/**
	 * Initialize the framework for the benchmark.
	 *
	 * @param file
	 *            Logging file
	 */
	void init(final File file) throws Exception;

	/**
	 * Write the number as trace, debug, info, warning and error logging message.
	 */
	void write(final long number) throws Exception;

	/**
	 * Calculate if the number is prime. If, then output the hit as info message, otherwise as a trace message.
	 *
	 * Algorithm:
	 *
	 * <pre>
	 * for (Long prime : primes)
	 * 	if (number % prime == 0L)
	 * 		return false;
	 * return true;
	 * </pre>
	 */
	boolean calculate(final List<Long> primes, final long number) throws Exception;

	/**
	 * Dispose the framework after the benchmark.
	 */
	void dispose() throws Exception;

}
