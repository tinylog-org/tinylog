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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.pmw.benchmark.Benchmark;

public final class PrimesBenchmarkExecutor extends AbstractBenchmarkExecutor {

	private static final String NAME = "prime";

	private final long maximum;
	private long primes;

	public PrimesBenchmarkExecutor(final Benchmark benchmark, final int runs, final int outliers, final long maximum) {
		super(benchmark, runs, outliers);
		this.maximum = maximum;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected final long countTriggeredLogEntries() {
		return maximum;
	}

	@Override
	protected final long countWrittenLogEntries() {
		return primes;
	}

	@Override
	protected boolean isValidLogEntry(final String line) {
		if (super.isValidLogEntry(line)) {
			return line.contains("is prime");
		} else {
			return false;
		}
	}

	@Override
	protected void run() throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Long> primes = new ArrayList<>();

		Queue<Future<Boolean>> runningCalculators = new LinkedList<>();
		long number = 2L;

		long limit = Math.min(number * number - 1, maximum);
		for (; number <= limit; ++number) {
			runningCalculators.add(executorService.submit(new PrimesCalculator(new SubList<Long>(primes, 0, primes.size()), number)));
		}

		for (long i = 2L; i <= limit; ++i) {
			Future<Boolean> calculator = runningCalculators.poll();
			if (calculator.get()) {
				primes.add(i);
			}
			limit = Math.min(i * i - 1, maximum);
			for (; number <= limit; ++number) {
				runningCalculators.add(executorService.submit(new PrimesCalculator(new SubList<Long>(primes, 0, primes.size()), number)));
			}
		}

		executorService.shutdown();
		this.primes = primes.size();
	}

	private final class PrimesCalculator implements Callable<Boolean> {

		private final List<Long> primes;
		private final long number;

		public PrimesCalculator(final List<Long> primes, final long number) {
			this.primes = primes;
			this.number = number;
		}

		@Override
		public Boolean call() throws Exception {
			return benchmark.calculate(primes, number);
		}

	}

	private final static class SubList<T> extends AbstractList<T> implements RandomAccess {

		private final List<T> parent;
		private final int offset;
		private final int size;

		public SubList(final List<T> parent, final int offset, final int size) {
			this.parent = parent;
			this.offset = offset;
			this.size = size;
		}

		@Override
		public T get(final int index) {
			rangeCheck(index);
			return parent.get(index + offset);
		}

		@Override
		public int size() {
			return size;
		}

		private void rangeCheck(final int index) {
			if (index < 0 || index >= size) {
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
			}
		}

	}

}
