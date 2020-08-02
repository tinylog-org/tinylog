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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModernIndexBasedStackTraceLocationTest {

	/**
	 * Verifies that the fully-qualified caller class name can be resolved.
	 */
	@Test
	void callerClassName() {
		ModernIndexBasedStackTraceLocation location = new ModernIndexBasedStackTraceLocation(0);
		String caller = location.getCallerClassName();

		assertThat(caller).isEqualTo(ModernIndexBasedStackTraceLocationTest.class.getName());
	}

	/**
	 * Verifies that the complete caller stack trace element can be resolved directly.
	 */
	@Test
	void callerStackTraceElement() {
		ModernIndexBasedStackTraceLocation location = new ModernIndexBasedStackTraceLocation(0);
		StackTraceElement caller = location.getCallerStackTraceElement();

		assertThat(caller.getFileName())
			.isEqualTo(ModernIndexBasedStackTraceLocationTest.class.getSimpleName() + ".java");
		assertThat(caller.getClassName()).isEqualTo(ModernIndexBasedStackTraceLocationTest.class.getName());
		assertThat(caller.getMethodName()).isEqualTo("callerStackTraceElement");
		assertThat(caller.getLineNumber()).isEqualTo(39);
	}

	/**
	 * Verifies that the caller stack trace element can be resolved from called sub methods.
	 */
	@Test
	void passedStackTraceLocation() {
		ModernIndexBasedStackTraceLocation location = new ModernIndexBasedStackTraceLocation(0);
		StackTraceElement caller = getCallerStackTraceElement(location.push());

		assertThat(caller.getFileName())
			.isEqualTo(ModernIndexBasedStackTraceLocationTest.class.getSimpleName() + ".java");
		assertThat(caller.getClassName()).isEqualTo(ModernIndexBasedStackTraceLocationTest.class.getName());
		assertThat(caller.getMethodName()).isEqualTo("passedStackTraceLocation");
		assertThat(caller.getLineNumber()).isEqualTo(54);
	}

	/**
	 * Retrieves the caller stack trace element from the passed stack trace location.
	 *
	 * @param location The stack trace location from which the caller is to be received
	 * @return The received caller stack trace element
	 */
	private StackTraceElement getCallerStackTraceElement(ModernIndexBasedStackTraceLocation location) {
		return location.getCallerStackTraceElement();
	}

}
