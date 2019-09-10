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

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link UnpackStackTraceFilter}.
 */
public final class UnpackStackTraceFilterTest {

	/**
	 * Verifies that the direct cause throwable will be used, if the parent throwable has the expected class name.
	 */
	@Test
	public void unpackSingleCause() {
		NullPointerException childException = new NullPointerException("Hello Hell!");
		RuntimeException parentException = new RuntimeException("Hello Heaven!", childException);
		
		StackTraceFilter filter = create(parentException, RuntimeException.class.getName());
		
		assertThat(filter.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(filter.getMessage()).isEqualTo("Hello Hell!");
		assertThat(filter.getStackTrace()).containsExactly(childException.getStackTrace());
		assertThat(filter.getCause()).isNull();
	}
	
	/**
	 * Verifies that the deepest cause throwable will be used, if the parent and child throwables have the expected class name.
	 */
	@Test
	public void unpackNestedCause() {
		NullPointerException grandChildException = new NullPointerException("Hello Hell!");
		RuntimeException childException = new RuntimeException("Hello World!", grandChildException);
		RuntimeException parentException = new RuntimeException("Hello Heaven!", childException);
		
		StackTraceFilter filter = create(parentException, RuntimeException.class.getName());
		
		assertThat(filter.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(filter.getMessage()).isEqualTo("Hello Hell!");
		assertThat(filter.getStackTrace()).containsExactly(grandChildException.getStackTrace());
		assertThat(filter.getCause()).isNull();
	}

	/**
	 * Verifies that multiple class names of throwables can be configured.
	 */
	@Test
	public void unpackMultipleExceptions() {
		NullPointerException childException = new NullPointerException("Hello Hell!");
		IOException parentException = new IOException("Hello Heaven!", childException);
		
		StackTraceFilter filter = create(parentException, RuntimeException.class.getName(), IOException.class.getName());
		
		assertThat(filter.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(filter.getMessage()).isEqualTo("Hello Hell!");
		assertThat(filter.getStackTrace()).containsExactly(childException.getStackTrace());
		assertThat(filter.getCause()).isNull();
	}

	/**
	 * Verifies that the cause throwable will be used, if all throwables should be unpacked.
	 */
	@Test
	public void unpackAll() {
		NullPointerException childException = new NullPointerException("Hello Hell!");
		RuntimeException parentException = new RuntimeException("Hello Heaven!", childException);
		
		StackTraceFilter filter = create(parentException);
		
		assertThat(filter.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(filter.getMessage()).isEqualTo("Hello Hell!");
		assertThat(filter.getStackTrace()).containsExactly(childException.getStackTrace());
		assertThat(filter.getCause()).isNull();
	}

	/**
	 * Verifies that the original throwable will be used, if there is no cause throwable.
	 */
	@Test
	public void missingCause() {
		RuntimeException exception = new RuntimeException("Hello World!");
		
		StackTraceFilter filter = create(exception, RuntimeException.class.getName());
		
		assertThat(filter.getClassName()).isEqualTo(RuntimeException.class.getName());
		assertThat(filter.getMessage()).isEqualTo("Hello World!");
		assertThat(filter.getStackTrace()).containsExactly(exception.getStackTrace());
		assertThat(filter.getCause()).isNull();
	}
	
	/**
	 * Verifies that the original throwable will be used, if the parent throwable doesn't have the expected class name.
	 */
	@Test
	public void otherException() {
		NullPointerException childException = new NullPointerException("Hello Hell!");
		IOException parentException = new IOException("Hello Heaven!", childException);
		
		StackTraceFilter parentFilter = create(parentException, RuntimeException.class.getName());
		
		assertThat(parentFilter.getClassName()).isEqualTo(IOException.class.getName());
		assertThat(parentFilter.getMessage()).isEqualTo("Hello Heaven!");
		assertThat(parentFilter.getStackTrace()).containsExactly(parentException.getStackTrace());
		assertThat(parentFilter.getCause()).isNotNull();

		StackTraceFilter childFilter = parentFilter.getCause();
		
		assertThat(childFilter.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(childFilter.getMessage()).isEqualTo("Hello Hell!");
		assertThat(childFilter.getStackTrace()).containsExactly(childException.getStackTrace());
		assertThat(childFilter.getCause()).isNull();
	}

	/**
	 * Creates a new {@link UnpackStackTraceFilter} instance.
	 * 
	 * @param throwable
	 *            Source throwable to pass
	 * @param classNames
	 *            Class names of throwables to pass
	 * @return Created instance
	 */
	private UnpackStackTraceFilter create(final Throwable throwable, final String... classNames) {
		StackTraceFilterAdapter origin = new StackTraceFilterAdapter(throwable);
		return new UnpackStackTraceFilter(origin, Arrays.asList(classNames));
	}

}
