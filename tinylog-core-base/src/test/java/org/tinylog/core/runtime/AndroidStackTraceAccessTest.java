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
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;

class AndroidStackTraceAccessTest {

	/**
	 * Verifies that {@code dalvik.system.VMStack.fillStackTraceElements(Thread, StackTraceElement[])} is available on
	 * Android.
	 */
	@EnabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	@Test
	void fillStackTraceElementsAvailable() throws Throwable {
		AndroidStackTraceAccess access = new AndroidStackTraceAccess();
		MethodHandle fillStackTraceElements = access.getStackTraceElementsFiller();
		assertThat(fillStackTraceElements).isNotNull();

		StackTraceElement[] trace = new StackTraceElement[access.getOffset() + 1];
		fillStackTraceElements.invoke(Thread.currentThread(), trace);
		assertThat(trace[trace.length - 1]).isEqualTo(new StackTraceElement(
			AndroidStackTraceAccessTest.class.getCanonicalName(),
			"fillStackTraceElementsAvailable",
			AndroidStackTraceAccessTest.class.getSimpleName() + ".java",
			34
		));
	}

	/**
	 * Verifies that {@code dalvik.system.VMStack.fillStackTraceElements(Thread, StackTraceElement[])} is not available
	 * on standard Java.
	 */
	@DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	@Test
	void fillStackTraceElementsUnavailable() {
		assertThat(new AndroidStackTraceAccess().getStackTraceElementsFiller()).isNull();
	}

}
