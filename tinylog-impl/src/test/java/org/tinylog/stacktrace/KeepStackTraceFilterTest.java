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
 * Tests for {@link KeepStackTraceFilter}.
 */
public final class KeepStackTraceFilterTest {

	/**
	 * Verifies that all stack trace elements will be removed, if list of packages and classes is {@code null}.
	 */
	@Test
	public void nullArgument() {
		RuntimeException exception = new RuntimeException();
		
		KeepStackTraceFilter filter = new KeepStackTraceFilter(null);
		assertThat(filter.apply(exception).getStackTrace()).isEmpty();
	}
	
	/**
	 * Verifies that all stack trace elements will be removed, if list of packages and classes is an empty string.
	 */
	@Test
	public void emptyArgument() {
		RuntimeException exception = new RuntimeException();
		
		KeepStackTraceFilter filter = new KeepStackTraceFilter("");
		assertThat(filter.apply(exception).getStackTrace()).isEmpty();
	}

	/**
	 * Verifies that a single package can be kept on stack trace.
	 */
	@Test
	public void singlePackage() {
		RuntimeException exception = new RuntimeException();
		
		KeepStackTraceFilter filter = new KeepStackTraceFilter("org.tinylog");
		assertThat(filter.apply(exception).getStackTrace()).hasSize(1);
	}

	/**
	 * Verifies that incomplete package names will be remove all elements from stack trace.
	 */
	@Test
	public void incompletePackage() {
		RuntimeException exception = new RuntimeException();
		
		KeepStackTraceFilter filter = new KeepStackTraceFilter("o");
		assertThat(filter.apply(exception).getStackTrace()).isEmpty();
	}

	/**
	 * Verifies that a multiple packages can be kept on stack trace.
	 */
	@Test
	public void multiplePackages() {
		RuntimeException exception = new RuntimeException();
		int elements = exception.getStackTrace().length;
	
		KeepStackTraceFilter filter = new KeepStackTraceFilter("com | java | javax | jdk | org | sun");
		assertThat(filter.apply(exception).getStackTrace()).hasSize(elements);
	}

	/**
	 * Verifies that a single class can be kept on stack trace.
	 */
	@Test
	public void singleClass() {
		RuntimeException exception = new RuntimeException();
		
		KeepStackTraceFilter filter = new KeepStackTraceFilter(KeepStackTraceFilterTest.class.getName());
		assertThat(filter.apply(exception).getStackTrace()).hasSize(1);
	}

	/**
	 * Verifies that stack trace of cause exceptions will be stripped, too.
	 */
	@Test
	public void throwableWithCause() {
		NullPointerException childException = new NullPointerException();
		RuntimeException parentException = new RuntimeException(childException);

		KeepStackTraceFilter filter = new KeepStackTraceFilter("org.tinylog");
		
		Throwable receivedThrowable = filter.apply(parentException);
		assertThat(receivedThrowable.getStackTrace()).hasSize(1);
		assertThat(receivedThrowable).hasRootCauseInstanceOf(NullPointerException.class);
		assertThat(receivedThrowable.getCause().getStackTrace()).hasSize(1);
	}

}
