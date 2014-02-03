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

public class SingleThreadBenchmark extends AbstractDeepStackTraceBenchmark {

	private static final int LOGGING_ITERATIONS = 20000;

	public SingleThreadBenchmark(final ILoggingFramework framework) {
		super(framework.getName() + " (single threaded)", framework);
	}

	public static void main(final String[] arguments) throws Exception {
		ILoggingFramework framework = createLoggingFramework(arguments);
		if (framework != null) {
			new SingleThreadBenchmark(framework).start();
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
	protected final void doRun(final ILoggingFramework framework) {
		for (int i = 0; i < LOGGING_ITERATIONS; ++i) {
			framework.trace(i + 1);
			framework.debug(i + 1);
			framework.info(i + 1);
			framework.warning(i + 1);
			framework.error(i + 1);
		}
	}

}
