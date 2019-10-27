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
import java.util.Collections;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DropCauseStackTraceFilter}.
 */
public final class DropCauseStackTraceFilterTest {

	/**
	 * Verifies that a single cause throwable will be dropped, if the parent throwable has the expected class name.
	 */
	@Test
	public void dropSingleCause() {
		NullPointerException childException = new NullPointerException("Hello Hell!");
		RuntimeException parentException = new RuntimeException("Hello Heaven!", childException);

		StackTraceFilter filter = new DropCauseStackTraceFilter(Collections.singletonList(RuntimeException.class.getName()));
		ThrowableData data = filter.filter(new ThrowableWrapper(parentException));

		assertThat(data.getClassName()).isEqualTo(RuntimeException.class.getName());
		assertThat(data.getMessage()).isEqualTo("Hello Heaven!");
		assertThat(data.getStackTrace()).containsExactly(parentException.getStackTrace());
		assertThat(data.getCause()).isNull();
	}

	/**
	 * Verifies that all nested cause throwables will be dropped, if the parent throwable has the expected class name.
	 */
	@Test
	public void dropNestedCause() {
		NullPointerException grandChildException = new NullPointerException("Hello Hell!");
		RuntimeException childException = new RuntimeException("Hello World!", grandChildException);
		RuntimeException parentException = new RuntimeException("Hello Heaven!", childException);

		StackTraceFilter filter = new DropCauseStackTraceFilter(Collections.singletonList(RuntimeException.class.getName()));
		ThrowableData data = filter.filter(new ThrowableWrapper(parentException));

		assertThat(data.getClassName()).isEqualTo(RuntimeException.class.getName());
		assertThat(data.getMessage()).isEqualTo("Hello Heaven!");
		assertThat(data.getStackTrace()).containsExactly(parentException.getStackTrace());
		assertThat(data.getCause()).isNull();
	}

	/**
	 * Verifies that multiple class names of throwables can be configured.
	 */
	@Test
	public void dropMultipleExceptions() {
		NullPointerException childException = new NullPointerException("Hello Hell!");
		IOException parentException = new IOException("Hello Heaven!", childException);

		StackTraceFilter filter = new DropCauseStackTraceFilter(Arrays.asList(RuntimeException.class.getName(), IOException.class.getName()));
		ThrowableData data = filter.filter(new ThrowableWrapper(parentException));

		assertThat(data.getClassName()).isEqualTo(IOException.class.getName());
		assertThat(data.getMessage()).isEqualTo("Hello Heaven!");
		assertThat(data.getStackTrace()).containsExactly(parentException.getStackTrace());
		assertThat(data.getCause()).isNull();
	}

	/**
	 * Verifies that all kind of cause throwables will be dropped, if no throwable class names have been defined.
	 */
	@Test
	public void dropAll() {
		NullPointerException childException = new NullPointerException("Hello Hell!");
		RuntimeException parentException = new RuntimeException("Hello Heaven!", childException);

		StackTraceFilter filter = new DropCauseStackTraceFilter(Collections.emptyList());
		ThrowableData data = filter.filter(new ThrowableWrapper(parentException));

		assertThat(data.getClassName()).isEqualTo(RuntimeException.class.getName());
		assertThat(data.getMessage()).isEqualTo("Hello Heaven!");
		assertThat(data.getStackTrace()).containsExactly(parentException.getStackTrace());
		assertThat(data.getCause()).isNull();
	}

	/**
	 * Verifies that throwables without any cause throwables will be output unmodified.
	 */
	@Test
	public void missingCause() {
		RuntimeException exception = new RuntimeException("Hello World!");

		StackTraceFilter filter = new DropCauseStackTraceFilter(Collections.singletonList(RuntimeException.class.getName()));
		ThrowableData data = filter.filter(new ThrowableWrapper(exception));

		assertThat(data.getClassName()).isEqualTo(RuntimeException.class.getName());
		assertThat(data.getMessage()).isEqualTo("Hello World!");
		assertThat(data.getStackTrace()).containsExactly(exception.getStackTrace());
		assertThat(data.getCause()).isNull();
	}

	/**
	 * Verifies that throwables that don't have the expected class name will be output unmodified with their origin cause throwables.
	 */
	@Test
	public void otherException() {
		NullPointerException childException = new NullPointerException("Hello Hell!");
		IOException parentException = new IOException("Hello Heaven!", childException);

		StackTraceFilter filter = new DropCauseStackTraceFilter(Collections.singletonList(RuntimeException.class.getName()));
	
		ThrowableData parentData = filter.filter(new ThrowableWrapper(parentException));
		assertThat(parentData.getClassName()).isEqualTo(IOException.class.getName());
		assertThat(parentData.getMessage()).isEqualTo("Hello Heaven!");
		assertThat(parentData.getStackTrace()).containsExactly(parentException.getStackTrace());
		assertThat(parentData.getCause()).isNotNull();

		ThrowableData causeData = parentData.getCause();
		assertThat(causeData.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(causeData.getMessage()).isEqualTo("Hello Hell!");
		assertThat(causeData.getStackTrace()).containsExactly(childException.getStackTrace());
		assertThat(causeData.getCause()).isNull();
	}

}
