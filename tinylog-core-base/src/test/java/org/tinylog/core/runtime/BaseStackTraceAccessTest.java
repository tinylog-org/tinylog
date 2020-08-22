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

import java.lang.invoke.MethodHandle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseStackTraceAccessTest {

	private final BaseStackTraceAccess access = new BaseStackTraceAccess() { };

	/**
	 * Verifies that an invokable method handle is provided for existing and valid methods.
	 */
	@Test
	public void validMethod() throws Throwable {
		MethodHandle handle = access.getMethod(
			ignore -> true, String.class.getCanonicalName(), "substring", int.class, int.class
		);

		assertThat(handle).isNotNull();
		assertThat(handle.invoke("12345", 2, 4)).isEqualTo("34");
	}

	/**
	 * Verifies that {@code null} is provided for existing but invalid methods.
	 */
	@Test
	public void invalidMethod() {
		MethodHandle handle = access.getMethod(
			ignore -> false, String.class.getCanonicalName(), "substring", int.class, int.class
		);

		assertThat(handle).isNull();
	}

	/**
	 * Verifies that {@code null} is provided for non-existent classes.
	 */
	@Test
	public void nonExistentClass() {
		MethodHandle handle = access.getMethod(
			ignore -> true, "invalid.Foo", "substring", int.class, int.class
		);

		assertThat(handle).isNull();
	}

	/**
	 * Verifies that {@code null} is provided for non-existent methods.
	 */
	@Test
	public void nonExistentMethod() {
		MethodHandle handle = access.getMethod(
			ignore -> true, String.class.getCanonicalName(), "substring", double.class, double.class
		);

		assertThat(handle).isNull();
	}

}
