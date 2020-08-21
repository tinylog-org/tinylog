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
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * Utility class for resolving legacy Java methods for receiving specific elements from the stack trace.
 */
final class LegacyStackTraceAccess {

	/** */
	private LegacyStackTraceAccess() {
	}

	/**
	 * Check if {@code sun.reflect.Reflection} is available.
	 *
	 * @return {@code true} if available, otherwise {@code false}
	 */
	@SuppressWarnings("removal")
	static boolean checkIfSunReflectionIsAvailable() {
		try {
			return LegacyStackTraceAccess.class.equals(sun.reflect.Reflection.getCallerClass(1));
		} catch (Throwable ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Creates a method handle for {@code Throwable.getStackTraceElement(int)}.
	 *
	 * @return Valid method handle if the method is available, otherwise {@code null}
	 */
	static MethodHandle getStackTraceElementGetter() {
		try {
			Method method = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
			method.setAccessible(true);
			MethodHandle handle = MethodHandles.lookup().unreflect(method);
			StackTraceElement stackTraceElement = (StackTraceElement) handle.invoke(new Throwable(), 0);
			if (LegacyStackTraceAccess.class.getName().equals(stackTraceElement.getClassName())) {
				return handle;
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		return null;
	}

}
