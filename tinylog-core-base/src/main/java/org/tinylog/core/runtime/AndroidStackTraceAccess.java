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

import java.lang.reflect.Method;

import dalvik.system.VMStack;

/**
 * Utility class for resolving legacy Android methods for receiving specific elements from the stack trace.
 */
final class AndroidStackTraceAccess {

	private static final int STACK_TRACE_SIZE = 32;

	/** */
	private AndroidStackTraceAccess() {
	}

	/**
	 * Generates a stack trace filler instance for {@code VMStack.fillStackTraceElements(Thread, StackTraceElement[])}.
	 *
	 * @return Valid filler instance if the method is available, otherwise {@code null}
	 */
	static StackTraceElementsFiller getStackTraceElementsFiller() {
		try {
			String name = "fillStackTraceElements";
			Method method = VMStack.class.getDeclaredMethod(name, Thread.class, StackTraceElement[].class);
			method.setAccessible(true);
			StackTraceElement[] trace = new StackTraceElement[STACK_TRACE_SIZE];
			method.invoke(null, Thread.currentThread(), trace);
			for (int i = 0; i < STACK_TRACE_SIZE; ++i) {
				StackTraceElement element = trace[i];
				if (element != null && AndroidStackTraceAccess.class.getName().equals(element.getClassName())
					&& "getStackTraceElementsFiller".equals(element.getMethodName())) {
					return new StackTraceElementsFiller(method, i);
				}
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		return null;
	}

}
