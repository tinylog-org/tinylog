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

import org.pmw.benchmark.Benchmark;

public abstract class AbstractDeepStackTraceBenchmarkExecutor extends AbstractBenchmarkExecutor {

	private final int deep;

	protected AbstractDeepStackTraceBenchmarkExecutor(final Benchmark benchmark, final int runs, final int outliers, final int deep) {
		super(benchmark, runs, outliers);
		this.deep = deep;
	}

	@Override
	protected final void run() throws Exception {
		run(deep);
	}

	protected abstract void nestedRun() throws Exception;

	@Override
	protected boolean isValidLogEntry(final String line) {
		if (super.isValidLogEntry(line) && line.contains("write()")) {
			return line.contains("Trace") ^ line.contains("Debug") ^ line.contains("Info") ^ line.contains("Warning") ^ line.contains("Error");
		} else {
			return false;
		}
	}

	private void run(final int stackTraceDeep) throws Exception {
		if (stackTraceDeep == 0) {
			nestedRun();
		} else {
			run(stackTraceDeep - 1);
		}
	}

}
