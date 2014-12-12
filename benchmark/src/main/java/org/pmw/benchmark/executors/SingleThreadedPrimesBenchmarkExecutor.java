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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pmw.benchmark.Benchmark;

public final class SingleThreadedPrimesBenchmarkExecutor extends AbstractPrimeBenchmarkExecutor {

	private static final String NAME = "prime / single-threaded";

	public SingleThreadedPrimesBenchmarkExecutor(final Benchmark benchmark, final int runs, final int outliers, final int deep, final long maximum) {
		super(benchmark, runs, outliers, deep, maximum);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected List<Long> calculate(final long maximum) throws Exception {
		List<Long> primes = new ArrayList<>();

		long splitterSquarePrime = 0L;
		List<Long> primesToTest = Collections.emptyList();

		for (long number = 2L; number <= maximum; ++number) {
			if (splitterSquarePrime < number && primesToTest.size() < primes.size()) {
				long splitterPrime = primes.get(primesToTest.size());
				splitterSquarePrime = splitterPrime * splitterPrime;
				primesToTest = new SubList<Long>(primes, primesToTest.size() + 1);
			}
			if (benchmark.calculate(primesToTest, number)) {
				primes.add(number);
			}
		}

		return primes;
	}

	@Override
	protected void run() throws Exception {
		int stackTraceDeep = getAdditionStackTraceDeep();
		if (stackTraceDeep == 0) {
			super.run();
		} else {
			run(stackTraceDeep - 1);
		}
	}

	private void run(final int stackTraceDeep) throws Exception {
		if (stackTraceDeep == 0) {
			super.run();
		} else {
			run(stackTraceDeep - 1);
		}
	}

}
