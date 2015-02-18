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

package org.pmw.benchmark.benchmarks.prime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.pmw.benchmark.frameworks.Framework;

public final class MultiThreadedPrimesBenchmark extends AbstractPrimeBenchmark {

	private final int threads;

	public MultiThreadedPrimesBenchmark(final Framework framework, final boolean locationInformation, final int deep, final long maximum, final int threads) {
		super(framework, locationInformation, deep, maximum);
		this.threads = threads;
	}

	@Override
	protected List<Long> calculate(final long maximum) throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		List<Long> primes = new ArrayList<>();

		Queue<Future<List<Long>>> runningCalculators = new LinkedList<>();
		Queue<Long> endRanges = new LinkedList<>();

		long start = 2L;
		long end = Math.min(start * start - 1, maximum);

		runningCalculators.add(executorService.submit(new PrimesCalculator(Collections.<Long> emptyList(), start, end)));
		endRanges.add(end);

		long splitterSquarePrime = 0L;
		List<Long> primesToTest = Collections.emptyList();

		while (!runningCalculators.isEmpty()) {
			Future<List<Long>> calculator = runningCalculators.poll();
			primes.addAll(calculator.get());

			if (end < maximum) {
				start = end + 1;
				end = Math.min(square(endRanges.poll()), maximum);

				while (splitterSquarePrime < end && primesToTest.size() < primes.size()) {
					long splitterPrime = primes.get(primesToTest.size());
					splitterSquarePrime = splitterPrime * splitterPrime;
					primesToTest = new SubList<>(primes, primesToTest.size() + 1);
				}

				if (end - start < threads) {
					runningCalculators.add(executorService.submit(new PrimesCalculator(primesToTest, start, end)));
					endRanges.add(end);
				} else {
					long count = (end - start) / threads;
					for (long i = 0; i < threads; ++i) {
						end = start + count - 1;
						runningCalculators.add(executorService.submit(new PrimesCalculator(primesToTest, start, end)));
						endRanges.add(end);
						start += count;
					}
				}
			}
		}

		executorService.shutdown();
		return primes;
	}

	private static long square(final long value) {
		return value * value;
	}

	private final class PrimesCalculator implements Callable<List<Long>> {

		private final List<Long> primes;
		private final long start;
		private final long end;

		public PrimesCalculator(final List<Long> primes, final long start, final long end) {
			this.primes = primes;
			this.start = start;
			this.end = end;
		}

		@Override
		public List<Long> call() throws Exception {
			int stackTraceDeep = getAdditionStackTraceDeep();
			if (stackTraceDeep == 0) {
				List<Long> found = new ArrayList<>();
				for (long number = start; number <= end; ++number) {
					if (framework.calculate(primes, number)) {
						found.add(number);
					}
				}
				return found;
			} else {
				return call(stackTraceDeep - 1);
			}
		}

		private List<Long> call(final int stackTraceDeep) throws Exception {
			if (stackTraceDeep == 0) {
				List<Long> found = new ArrayList<>();
				for (long number = start; number <= end; ++number) {
					if (framework.calculate(primes, number)) {
						found.add(number);
					}
				}
				return found;
			} else {
				return call(stackTraceDeep - 1);
			}
		}

	}

}
