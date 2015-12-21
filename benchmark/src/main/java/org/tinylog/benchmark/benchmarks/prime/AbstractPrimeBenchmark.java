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

package org.tinylog.benchmark.benchmarks.prime;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

import org.tinylog.benchmark.benchmarks.AbstractBenchmark;
import org.tinylog.benchmark.frameworks.Framework;

public abstract class AbstractPrimeBenchmark extends AbstractBenchmark {

	private final long maximum;
	private long primes;

	protected AbstractPrimeBenchmark(final Framework framework, final boolean locationInformation, final int deep, final long maximum) {
		super(framework, locationInformation, deep);
		this.maximum = maximum;
	}

	@Override
	public boolean isValidLogEntry(final String line) {
		return super.isValidLogEntry(line) && (!locationInformation || line.contains("calculate()")) && line.contains("is prime");
	}

	@Override
	public final long countTriggeredLogEntries() {
		return maximum;
	}

	@Override
	public final long countWrittenLogEntries() {
		return primes;
	}

	@Override
	public void run() throws Exception {
		primes = calculate(maximum).size();
	}

	protected abstract List<Long> calculate(long maximum) throws Exception;

	protected final static class SubList<T> extends AbstractList<T> implements RandomAccess {

		private final List<T> parent;
		private final int size;

		public SubList(final List<T> parent, final int size) {
			this.parent = parent;
			this.size = size;
		}

		@Override
		public T get(final int index) {
			rangeCheck(index);
			return parent.get(index);
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
