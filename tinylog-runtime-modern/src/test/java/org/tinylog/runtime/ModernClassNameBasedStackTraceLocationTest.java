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

package org.tinylog.runtime;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModernClassNameBasedStackTraceLocationTest {

	/**
	 * Verifies that the fully-qualified caller class name can be resolved.
	 */
	@Test
	void callerClassName() {
		String callee = Callee.class.getName();
		ModernClassNameBasedStackTraceLocation location = new ModernClassNameBasedStackTraceLocation(callee);
		String caller = Callee.execute(location::getCallerClassName);

		assertThat(caller).isEqualTo(ModernClassNameBasedStackTraceLocationTest.class.getName());
	}

	/**
	 * Verifies that the complete caller stack trace element can be resolved.
	 */
	@Test
	void callerStackTraceElement() {
		String callee = Callee.class.getName();
		ModernClassNameBasedStackTraceLocation location = new ModernClassNameBasedStackTraceLocation(callee);
		StackTraceElement caller = Callee.execute(location::getCallerStackTraceElement);

		assertThat(caller.getFileName())
			.isEqualTo(ModernClassNameBasedStackTraceLocationTest.class.getSimpleName() + ".java");
		assertThat(caller.getClassName()).isEqualTo(ModernClassNameBasedStackTraceLocationTest.class.getName());
		assertThat(caller.getMethodName()).isEqualTo("callerStackTraceElement");
		assertThat(caller.getLineNumber()).isEqualTo(43);
	}

	/**
	 * Verifies that the caller stack trace element can be resolved from called sub methods.
	 */
	@Test
	void passedStackTraceLocation() {
		String callee = Callee.class.getName();
		ModernClassNameBasedStackTraceLocation location = new ModernClassNameBasedStackTraceLocation(callee);
		StackTraceElement caller = Callee.execute(() -> getCallerStackTraceElement(location.push()));

		assertThat(caller.getFileName())
			.isEqualTo(ModernClassNameBasedStackTraceLocationTest.class.getSimpleName() + ".java");
		assertThat(caller.getClassName()).isEqualTo(ModernClassNameBasedStackTraceLocationTest.class.getName());
		assertThat(caller.getMethodName()).isEqualTo("passedStackTraceLocation");
		assertThat(caller.getLineNumber()).isEqualTo(59);
	}

	/**
	 * Retrieves the caller stack trace element from the passed stack trace location.
	 *
	 * @param location The stack trace location from which the caller is to be received
	 * @return The received caller stack trace element
	 */
	private StackTraceElement getCallerStackTraceElement(ModernClassNameBasedStackTraceLocation location) {
		return location.getCallerStackTraceElement();
	}

	/**
	 * Helper class to simulate a callee.
	 */
	private static final class Callee {

		/**
		 * Executes the passed {@link Supplier}.
		 *
		 * @param supplier The supplier to execute
		 * @param <T> Return type
		 * @return The produced value from the passed supplier
		 */
		static <T> T execute(Supplier<T> supplier) {
			return supplier.get();
		}

	}

}
