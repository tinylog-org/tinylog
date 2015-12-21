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

package org.tinylog.benchmark.frameworks;

import java.io.File;
import java.util.List;

/**
 * Test the benchmark without any logging for calculating overhead.
 */
public final class Dummy implements Framework {

	public static final String NAME = "Without logging";

	public Dummy() {
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void init(final File file) {
		// Do nothing
	}

	@Override
	public void write(final long number) {
		// Do nothing
	}

	@Override
	public boolean calculate(final List<Long> primes, final long number) {
		for (Long prime : primes) {
			if (number % prime == 0L) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void dispose() {
		// Do nothing
	}

}
