/*
 * Copyright 2017 Martin Winandy
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

package org.pmw.tinylog.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.pmw.tinylog.AbstractCoreTest;

/**
 * Tests for Oracle's modern Java runtime implementation.
 *
 * @see LegacyJavaRuntime
 */
public class ModernJavaRuntimeTest extends AbstractCoreTest {

	/**
	 * Test getting the current process ID.
	 */
	@Test
	public final void testCurrentProcessId() {
		assertEquals(Long.toString(ProcessHandle.current().pid()), new ModernJavaRuntime().getProcessId());
	}

	/**
	 * Test getting a class name from stack trace.
	 */
	@Test
	public final void testGettingClassName() {
		String name = new ModernJavaRuntime().getClassName(1);
		assertEquals(ModernJavaRuntimeTest.class.getName(), name);
	}

	/**
	 * Test getting a stack trace element from stack trace.
	 */
	@Test
	public final void testGettingStackTraceElement() {
		StackTraceElement stackTraceElement = new ModernJavaRuntime().getStackTraceElement(1);
		assertNotNull(stackTraceElement);
		assertEquals(ModernJavaRuntimeTest.class.getName(), stackTraceElement.getClassName());
		assertEquals("testGettingStackTraceElement", stackTraceElement.getMethodName());
	}

}
