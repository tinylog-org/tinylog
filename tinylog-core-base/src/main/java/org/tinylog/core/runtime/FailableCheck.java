/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core.runtime;

/**
 * Predicate for testing values that allows throwing exception of other kind of throwables.
 *
 * @param <T> Supported value type
 */
@FunctionalInterface
interface FailableCheck<T> {

	/**
	 * Verifies if the passed value is valid.
	 *
	 * @param value Value to verify
	 * @return {@code true} if the passed value is valid, {@code false} if not
	 * @throws Throwable Any exception or other kind of throwable can be thrown and has to be handles by the caller
	 */
	boolean test(T value) throws Throwable;

}
