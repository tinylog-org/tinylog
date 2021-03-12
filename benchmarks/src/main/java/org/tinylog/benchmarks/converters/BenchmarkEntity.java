/*
 * Copyright 2021 Martin Winandy
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

package org.tinylog.benchmarks.converters;

import java.util.Objects;

import org.tinylog.benchmarks.logging.LocationInfo;

/**
 * Data class for bundling a benchmark name with the applied location information.
 */
public class BenchmarkEntity implements Comparable<BenchmarkEntity> {

	private final String benchmark;
	private final LocationInfo location;

	/**
	 * @param benchmark Benchmark name like "output" or "discard"
	 * @param location Applied location information
	 */
	public BenchmarkEntity(final String benchmark, final LocationInfo location) {
		this.benchmark = benchmark;
		this.location = location;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof BenchmarkEntity) {
			BenchmarkEntity other = (BenchmarkEntity) obj;
			return Objects.equals(benchmark, other.benchmark) && location == other.location;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(final BenchmarkEntity other) {
		int result = benchmark.compareTo(other.benchmark);
		if (result == 0) {
			result = location.compareTo(other.location);
		}
		return result;
	}

	@Override
	public int hashCode() {
		return Objects.hash(benchmark, location);
	}

	@Override
	public String toString() {
		return "Benchmark: " + benchmark + ", Location info: " + location;
	}

}
