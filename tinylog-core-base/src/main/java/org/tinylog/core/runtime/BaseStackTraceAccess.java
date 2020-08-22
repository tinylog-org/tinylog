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
 * Base utility class for resolving legacy methods for receiving specific elements from the stack trace.
 */
abstract class BaseStackTraceAccess {

	/** */
	protected BaseStackTraceAccess() {
	}

	/**
	 * Gets a verified method handle.
	 *
	 * @param check Verifier for the found method handle
	 * @param className Class name
	 * @param methodName Method name
	 * @param argumentTypes Method argument types
	 * @return The found method handle or {@code null} if no valid method can be resolved
	 */
	protected final MethodHandle getMethod(FailableCheck<MethodHandle> check, String className, String methodName,
			Class<?>... argumentTypes) {
		try {
			Class<?> clazz = Class.forName(className);
			Method method = clazz.getDeclaredMethod(methodName, argumentTypes);
			method.setAccessible(true);
			MethodHandle handle = MethodHandles.lookup().unreflect(method);
			return check.test(handle) ? handle : null;
		} catch (Throwable ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
