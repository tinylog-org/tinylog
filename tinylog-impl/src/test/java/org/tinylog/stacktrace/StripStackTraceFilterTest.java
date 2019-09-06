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
 * Tests for {@link StripStackTraceFilter}.
 */
public final class StripStackTraceFilterTest {

	/**
	 * Verifies that all stack trace elements will be kept, if list of packages and classes is {@code null}.
	 */
	@Test
	public void nullArgument() {
		RuntimeException exception = new RuntimeException();
		int elements = exception.getStackTrace().length;
		
		StripStackTraceFilter filter = new StripStackTraceFilter(null);
		assertThat(filter.apply(exception).getStackTrace()).hasSize(elements);
	}
	
	/**
	 * Verifies that all stack trace elements will be kept, if list of packages and classes is an empty string.
	 */
	@Test
	public void emptyArgument() {
		RuntimeException exception = new RuntimeException();
		int elements = exception.getStackTrace().length;
		
		StripStackTraceFilter filter = new StripStackTraceFilter("");
		assertThat(filter.apply(exception).getStackTrace()).hasSize(elements);
	}

	/**
	 * Verifies that a single package can be removed from stack trace.
	 */
	@Test
	public void singlePackage() {
		RuntimeException exception = new RuntimeException();
		int elements = exception.getStackTrace().length;
		
		StripStackTraceFilter filter = new StripStackTraceFilter("org.tinylog");
		assertThat(filter.apply(exception).getStackTrace()).hasSize(elements - 1);
	}

	/**
	 * Verifies that incomplete package names will be not removed from stack trace.
	 */
	@Test
	public void incompletePackage() {
		RuntimeException exception = new RuntimeException();
		int elements = exception.getStackTrace().length;
		
		StripStackTraceFilter filter = new StripStackTraceFilter("o");
		assertThat(filter.apply(exception).getStackTrace()).hasSize(elements);
	}

	/**
	 * Verifies that a multiple packages can be removed from stack trace.
	 */
	@Test
	public void multiplePackages() {
		RuntimeException exception = new RuntimeException();
		
		StripStackTraceFilter filter = new StripStackTraceFilter("com | java | javax | jdk | org | sun");
		assertThat(filter.apply(exception).getStackTrace()).hasSize(0);
	}

	/**
	 * Verifies that a single class can be removed from stack trace.
	 */
	@Test
	public void singleClass() {
		RuntimeException exception = new RuntimeException();
		int elements = exception.getStackTrace().length;
		
		StripStackTraceFilter filter = new StripStackTraceFilter(StripStackTraceFilterTest.class.getName());
		assertThat(filter.apply(exception).getStackTrace()).hasSize(elements - 1);
	}

	/**
	 * Verifies that stack trace of cause exceptions will be stripped, too.
	 */
	@Test
	public void throwableWithCause() {
		NullPointerException childException = new NullPointerException();
		int childElements = childException.getStackTrace().length;
		
		RuntimeException parentException = new RuntimeException(childException);
		int parentElements = parentException.getStackTrace().length;

		StripStackTraceFilter filter = new StripStackTraceFilter("org.tinylog");
		
		Throwable receivedThrowable = filter.apply(parentException);
		assertThat(receivedThrowable.getStackTrace()).hasSize(parentElements - 1);
		assertThat(receivedThrowable).hasRootCauseInstanceOf(NullPointerException.class);
		assertThat(receivedThrowable.getCause().getStackTrace()).hasSize(childElements - 1);
	}

}
