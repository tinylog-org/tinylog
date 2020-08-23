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

/**
 * Utility class for resolving legacy Android methods for receiving specific elements from the stack trace.
 */
final class AndroidStackTraceAccess extends BaseStackTraceAccess {

	private static final int STACK_TRACE_SIZE = 32;

	private int offset = -1;

	/** */
	AndroidStackTraceAccess() {
	}

	/**
	 * Gets the offset in filled stack traces to the caller.
	 *
	 * <p>
	 *     The method {@link #AndroidStackTraceAccess()} must be called at least once before getting a valid offset.
	 * </p>
	 *
	 * @return The offset to the caller
	 */
	int getOffset() {
		return offset;
	}

	/**
	 * Creates a method handle for {@code dalvik.system.VMStack.fillStackTraceElements(Thread, StackTraceElement[])}.
	 *
	 * @return Valid filler instance if the method is available, otherwise {@code null}
	 */
	MethodHandle getStackTraceElementsFiller() {
		FailableCheck<MethodHandle> check = handle -> {
			StackTraceElement[] trace = new StackTraceElement[STACK_TRACE_SIZE];
			int count = (Integer) handle.invoke(Thread.currentThread(), trace);
			for (int i = 0; i < count; ++i) {
				StackTraceElement element = trace[i];
				if (element.getClassName().startsWith(AndroidStackTraceAccess.class.getName())
					&& element.getMethodName().contains("getStackTraceElementsFiller")) {
					offset = i;
					return true;
				}
			}
			return false;
		};

		return getMethod(check, "dalvik.system.VMStack", "fillStackTraceElements", Thread.class,
			StackTraceElement[].class);
	}

}
