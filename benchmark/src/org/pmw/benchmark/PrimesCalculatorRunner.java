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

package org.pmw.benchmark;

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

public class PrimesCalculatorRunner extends AbstractRunner {

	private static final long MAX_NUMBER = 10_000_000L;

	private long primes;

	public PrimesCalculatorRunner(final ILoggingFramework framework) {
		super(framework.getName() + " (primes)", framework);
	}

	public static void main(final String[] arguments) throws Exception {
		ILoggingFramework framework = createLoggingFramework(arguments);
		if (framework != null) {
			new PrimesCalculatorRunner(framework).start();
		}
	}

	@Override
	protected long countTriggeredLogEntries() {
		return MAX_NUMBER;
	}

	@Override
	protected long countWrittenLogEntries() {
		return primes;
	}

	@Override
	protected void run(final ILoggingFramework framework) throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 10);
		List<Long> primes = new ArrayList<>();
		framework.trace(1L);

		Queue<Future<Boolean>> runningCalculators = new LinkedList<>();
		long number = 2L;

		long limit = Math.min(number * number - 1, MAX_NUMBER);
		for (; number <= limit; ++number) {
			runningCalculators.add(executorService.submit(new PrimesCalculator(framework, new SubList<Long>(primes, 0, primes.size()), number)));
		}

		for (long i = 2L; i <= limit; ++i) {
			Future<Boolean> calculator = runningCalculators.poll();
			if (calculator.get()) {
				primes.add(i);
			}
			limit = Math.min(i * i - 1, MAX_NUMBER);
			for (; number <= limit; ++number) {
				runningCalculators.add(executorService.submit(new PrimesCalculator(framework, new SubList<Long>(primes, 0, primes.size()), number)));
			}
		}

		executorService.shutdown();
		this.primes = primes.size();
	}

	private class PrimesCalculator implements Callable<Boolean> {

		private final ILoggingFramework framework;
		private final List<Long> primes;
		private final long number;

		public PrimesCalculator(final ILoggingFramework framework, final List<Long> primes, final long number) {
			this.framework = framework;
			this.primes = primes;
			this.number = number;
		}

		@Override
		public Boolean call() {
			for (Long prime : primes) {
				if (number % prime == 0L) {
					framework.trace(number);
					return Boolean.FALSE;
				}
			}
			framework.info(number);
			return Boolean.TRUE;
		}
	}

	private class SubList<T> extends AbstractList<T> implements RandomAccess {

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
