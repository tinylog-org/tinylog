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

public class SingleThreadBenchmarkRunner extends AbstractBenchmarkRunner {

	private static final int LOGGING_ITERATIONS = 20000;

	public SingleThreadBenchmarkRunner(final IBenchmark benchmark) {
		super(benchmark.getName() + " (single threaded)", benchmark);
	}

	public static void main(final String[] arguments) throws Exception {
		IBenchmark benchmark = createBenchmark(arguments);
		if (benchmark != null) {
			new SingleThreadBenchmarkRunner(benchmark).start();
		}
	}

	@Override
	protected long countTriggeredLogEntries() {
		return LOGGING_ITERATIONS * 5L; // TRACE, DEBUG, INFO, WARNING and ERROR
	}

	@Override
	protected long countWrittenLogEntries() {
		return LOGGING_ITERATIONS * 3L; // INFO, WARNING and ERROR will be output
	}

	@Override
	protected final void run(final IBenchmark benchmark) {
		for (int i = 0; i < LOGGING_ITERATIONS; ++i) {
			benchmark.trace(i + 1);
			benchmark.debug(i + 1);
			benchmark.info(i + 1);
			benchmark.warning(i + 1);
			benchmark.error(i + 1);
		}
	}

}
