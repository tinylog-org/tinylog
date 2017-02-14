/*
 * Copyright 2017 Martin Winandy
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

package org.tinylog.util;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utility class for waiting for delayed results.
 */
public final class ResultObserver {

	/** */
	private ResultObserver() {
	}

	/**
	 * Waits until the given supplier returns a result value that matches the given predicate or timeout expires.
	 *
	 * @param <T>
	 *            Type of return value
	 * @param supplier
	 *            Provides the return value
	 * @param predicate
	 *            Verifies the return value
	 * @param timeout
	 *            Timeout in milliseconds
	 * @return Result value from supplier
	 */
	public static <T> T waitFor(final Supplier<T> supplier, final Predicate<T> predicate, final long timeout) {
		long start = System.currentTimeMillis();
		T result;

		do {
			Thread.yield();
			result = supplier.get();
			if (predicate.test(result)) {
				return result;
			}
		} while (System.currentTimeMillis() < start + timeout);

		return result;
	}

}
