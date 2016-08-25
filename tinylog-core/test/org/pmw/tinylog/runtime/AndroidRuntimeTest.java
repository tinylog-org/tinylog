/*
 * Copyright 2016 Martin Winandy
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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.pmw.tinylog.hamcrest.StringMatchers.matchesPattern;

import java.lang.reflect.Method;

import org.junit.Test;
import org.pmw.tinylog.AbstractCoreTest;

import android.os.Process;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;

/**
 * Tests for Google's Android runtime implementation.
 *
 * @see AndroidRuntime
 */
public class AndroidRuntimeTest extends AbstractCoreTest {

	/**
	 * Test getting the process ID.
	 */
	@Test
	public final void testProcessId() {
		new MockUp<Process>() {
			@Mock
			public int myPid() {
				return 42;
			}
		};

		String pid = new AndroidRuntime().getProcessId();
		assertEquals("42", pid);
	}

	/**
	 * Test getting a class name from stack trace.
	 * 
	 * @throws NoSuchMethodException
	 *             Test failed
	 */
	@Test
	public final void testGettingClassName() throws NoSuchMethodException {
		VMStackTraceMock mock = VMStackTraceMock.create();

		String name = new AndroidRuntime().getClassName(1);
		assertEquals(AndroidRuntimeTest.class.getName(), name);
		
		assertTrue(mock.mocked);
	}

	/**
	 * Test getting the right class name of caller even if dalvik.system.VMStack is missing.
	 */
	@Test
	public final void testGettingClassNameWithoutVMStack() {
		String name = new AndroidRuntime().getClassName(1);
		assertEquals(AndroidRuntimeTest.class.getName(), name);
	}

	/**
	 * Test getting a stack trace element from stack trace.
	 * 
	 * @throws NoSuchMethodException
	 *             Test failed
	 */
	@Test
	public final void testGettingStackTraceElemente() throws NoSuchMethodException {
		VMStackTraceMock mock = VMStackTraceMock.create();

		StackTraceElement element = new AndroidRuntime().getStackTraceElement(1);
		assertEquals(AndroidRuntimeTest.class.getName(), element.getClassName());
		assertEquals("testGettingStackTraceElemente", element.getMethodName());
		
		assertTrue(mock.mocked);
	}

	/**
	 * Test getting the right stack trace element even if dalvik.system.VMStack is missing.
	 */
	@Test
	public final void testGettingStackTraceElementeWithoutVMStack() {
		StackTraceElement element = new AndroidRuntime().getStackTraceElement(1);
		assertEquals(AndroidRuntimeTest.class.getName(), element.getClassName());
		assertEquals("testGettingStackTraceElementeWithoutVMStack", element.getMethodName());
	}

	/**
	 * Test getting the right class name of caller even if calling dalvik.system.VMStack will fail.
	 * 
	 * @throws NoSuchMethodException
	 *             Test failed
	 */
	@Test
	public final void testErrorWhileCallingVMStackTrace() throws NoSuchMethodException {
		VMStackTraceMock mock = VMStackTraceMock.create();
		AndroidRuntime runtime = new AndroidRuntime();
		VMStackTraceMock.evil = true;

		String name = runtime.getClassName(1);
		assertEquals(AndroidRuntimeTest.class.getName(), name);
		assertThat(getErrorStream().nextLine(), matchesPattern("LOGGER WARNING\\: Failed to get stack trace from dalvik.system.VMStack \\(.+\\)"));
		
		assertTrue(mock.mocked);
	}

	private static final class VMStackTraceMock extends MockUp<Class<?>> {

		private static boolean evil;

		private final Method fillStackTraceElementsMethod;
		private boolean mocked;

		private VMStackTraceMock(final Method fillStackTraceElementsMethod) {
			VMStackTraceMock.evil = false;

			this.fillStackTraceElementsMethod = fillStackTraceElementsMethod;
			this.mocked = false;
		}

		private static VMStackTraceMock create() throws NoSuchMethodException {
			Method method = VMStackTraceMock.class.getDeclaredMethod("fillStackTraceElements", Thread.class, StackTraceElement[].class);
			return new VMStackTraceMock(method);
		}

		@Mock
		private Method getDeclaredMethod(final Invocation invocation, final String name, final Class<?>... parameterTypes) {
			if ("fillStackTraceElements".equals(name)) {
				mocked = true;
				return fillStackTraceElementsMethod;
			} else {
				return invocation.proceed();
			}
		}

		@SuppressWarnings("unused")
		private static void fillStackTraceElements(final Thread thread, final StackTraceElement[] trace) {
			if (evil) {
				throw new UnsupportedOperationException();
			} else {
				StackTraceElement[] realTrace = thread.getStackTrace();
				System.arraycopy(realTrace, 0, trace, 0, Math.min(realTrace.length, trace.length));
			}
		}

	}

}
