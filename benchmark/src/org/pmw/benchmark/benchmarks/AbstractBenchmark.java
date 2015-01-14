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

package org.pmw.benchmark.benchmarks;

import java.util.Calendar;

import org.pmw.benchmark.frameworks.Framework;

public abstract class AbstractBenchmark implements Benchmark {

	protected final Framework framework;

	private final int deep;

	protected AbstractBenchmark(final Framework framework, final int deep) {
		this.framework = framework;
		this.deep = deep;
	}

	public Framework getFramework() {
		return framework;
	}

	@Override
	public boolean isValidLogEntry(final String line) {
		if (!line.contains(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)))) {
			return false;
		}

		if (!line.contains(framework.getClass().getName())) {
			return false;
		}

		return true;
	}

	protected int getAdditionStackTraceDeep() {
		return deep;
	}

}
