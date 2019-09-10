/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.stacktrace;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StackTraceFilterAdapter}.
 */
public final class StackTraceFilterAdapterTest {

	/**
	 * Verifies that the class name of the source throwable will be looped through.
	 */
	@Test
	public void keepClassName() {
		RuntimeException exception = new RuntimeException();

		StackTraceFilterAdapter filter = new StackTraceFilterAdapter(exception);
		assertThat(filter.getClassName()).isEqualTo(RuntimeException.class.getName());
	}

	/**
	 * Verifies that the message of the source throwable will be looped through.
	 */
	@Test
	public void keepMessage() {
		RuntimeException exception = new RuntimeException("Hello World!");

		StackTraceFilterAdapter filter = new StackTraceFilterAdapter(exception);
		assertThat(filter.getMessage()).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that the stack trace of the source throwable will be looped through.
	 */
	@Test
	public void loopTroughStackTrace() {
		RuntimeException exception = new RuntimeException();

		StackTraceFilterAdapter filter = new StackTraceFilterAdapter(exception);
		assertThat(filter.getStackTrace()).containsExactly(exception.getStackTrace());
	}

	/**
	 * Verifies that a null cause of the source throwable will be looped through.
	 */
	@Test
	public void loopTroughNullCause() {
		RuntimeException exception = new RuntimeException("Hello World!");

		StackTraceFilterAdapter parentFilter = new StackTraceFilterAdapter(exception);
		StackTraceFilterAdapter childFilter = parentFilter.getCause();

		assertThat(childFilter).isNull();
	}

	/**
	 * Verifies that an existing cause of the source throwable will be looped through.
	 */
	@Test
	public void keepExistingCause() {
		RuntimeException exception = new RuntimeException("Hello Heaven!", new NullPointerException("Hello Hell!"));

		StackTraceFilterAdapter parentFilter = new StackTraceFilterAdapter(exception);
		StackTraceFilterAdapter childFilter = parentFilter.getCause();

		assertThat(childFilter).isNotNull();
		assertThat(childFilter.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(childFilter.getMessage()).isEqualTo("Hello Hell!");
	}

}
