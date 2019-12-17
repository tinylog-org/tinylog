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

package org.tinylog.throwable;

import java.io.IOException;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link UnpackThrowableFilter}.
 */
public final class UnpackThrowableFilterTest {

	/**
	 * Verifies that the direct cause throwable will be used, if the parent throwable has the expected class name.
	 */
	@Test
	public void unpackSingleCause() {
		NullPointerException childException = new NullPointerException("Hello World!");
		RuntimeException parentException = new RuntimeException("Hello Heaven!", childException);
		
		ThrowableFilter filter = new UnpackThrowableFilter(RuntimeException.class.getName());
		ThrowableData data = filter.filter(new ThrowableWrapper(parentException));

		assertThat(data.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(data.getMessage()).isEqualTo("Hello World!");
		assertThat(data.getStackTrace()).containsExactly(childException.getStackTrace());
		assertThat(data.getCause()).isNull();
	}
	
	/**
	 * Verifies that the deepest cause throwable will be used, if the parent and child throwables have the expected class name.
	 */
	@Test
	public void unpackNestedCause() {
		NullPointerException grandChildException = new NullPointerException("Hello Hell!");
		RuntimeException childException = new RuntimeException("Hello World!", grandChildException);
		RuntimeException parentException = new RuntimeException("Hello Heaven!", childException);
		
		ThrowableFilter filter = new UnpackThrowableFilter(RuntimeException.class.getName());
		ThrowableData data = filter.filter(new ThrowableWrapper(parentException));

		assertThat(data.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(data.getMessage()).isEqualTo("Hello Hell!");
		assertThat(data.getStackTrace()).containsExactly(grandChildException.getStackTrace());
		assertThat(data.getCause()).isNull();
	}

	/**
	 * Verifies that multiple class names of throwables can be configured.
	 */
	@Test
	public void unpackMultipleExceptions() {
		NullPointerException childException = new NullPointerException("Hello World!");
		IOException parentException = new IOException("Hello Heaven!", childException);
		
		ThrowableFilter filter = new UnpackThrowableFilter(RuntimeException.class.getName() + " | " + IOException.class.getName());
		ThrowableData data = filter.filter(new ThrowableWrapper(parentException));

		assertThat(data.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(data.getMessage()).isEqualTo("Hello World!");
		assertThat(data.getStackTrace()).containsExactly(childException.getStackTrace());
		assertThat(data.getCause()).isNull();
	}

	/**
	 * Verifies that the deeptest cause throwable will be used, if all throwables should be unpacked.
	 */
	@Test
	public void unpackAll() {
		NullPointerException childException = new NullPointerException("Hello World!");
		RuntimeException parentException = new RuntimeException("Hello Heaven!", childException);
		
		ThrowableFilter filter = new UnpackThrowableFilter();
		ThrowableData data = filter.filter(new ThrowableWrapper(parentException));

		assertThat(data.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(data.getMessage()).isEqualTo("Hello World!");
		assertThat(data.getStackTrace()).containsExactly(childException.getStackTrace());
		assertThat(data.getCause()).isNull();
	}

	/**
	 * Verifies that the original throwable will be used, if there is no cause throwable.
	 */
	@Test
	public void missingCause() {
		RuntimeException exception = new RuntimeException("Hello World!");
		
		ThrowableFilter filter = new UnpackThrowableFilter(RuntimeException.class.getName());
		ThrowableData data = filter.filter(new ThrowableWrapper(exception));

		assertThat(data.getClassName()).isEqualTo(RuntimeException.class.getName());
		assertThat(data.getMessage()).isEqualTo("Hello World!");
		assertThat(data.getStackTrace()).containsExactly(exception.getStackTrace());
		assertThat(data.getCause()).isNull();
	}
	
	/**
	 * Verifies that the original throwable will be used, if the parent throwable doesn't have the expected class name.
	 */
	@Test
	public void otherException() {
		NullPointerException childException = new NullPointerException("Hello World!");
		IOException parentException = new IOException("Hello Heaven!", childException);
		
		ThrowableFilter filter = new UnpackThrowableFilter(RuntimeException.class.getName());
		
		ThrowableData parentData = filter.filter(new ThrowableWrapper(parentException));
		assertThat(parentData.getClassName()).isEqualTo(IOException.class.getName());
		assertThat(parentData.getMessage()).isEqualTo("Hello Heaven!");
		assertThat(parentData.getStackTrace()).containsExactly(parentException.getStackTrace());
		assertThat(parentData.getCause()).isNotNull();

		ThrowableData causeData = parentData.getCause();
		assertThat(causeData.getClassName()).isEqualTo(NullPointerException.class.getName());
		assertThat(causeData.getMessage()).isEqualTo("Hello World!");
		assertThat(causeData.getStackTrace()).containsExactly(childException.getStackTrace());
		assertThat(causeData.getCause()).isNull();
	}

}
