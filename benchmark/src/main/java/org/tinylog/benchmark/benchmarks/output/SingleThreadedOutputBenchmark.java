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

package org.tinylog.benchmark.benchmarks.output;

import org.tinylog.benchmark.frameworks.Framework;

public final class SingleThreadedOutputBenchmark extends AbstractOutputBenchmark {

	private final long iterations;

	public SingleThreadedOutputBenchmark(final Framework framework, final boolean locationInformation, final int deep, final long iterations) {
		super(framework, locationInformation, deep);
		this.iterations = iterations;
	}

	@Override
	public long countTriggeredLogEntries() {
		return iterations * 5L; // TRACE, DEBUG, INFO, WARNING and ERROR
	}

	@Override
	public long countWrittenLogEntries() {
		return iterations * 3L; // INFO, WARNING and ERROR will be output
	}

	@Override
	public void run() throws Exception {
		write(getAdditionStackTraceDeep(), iterations);
	}

}
