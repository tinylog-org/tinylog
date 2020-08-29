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
 * Utility class for resolving legacy Java methods for receiving specific elements from the stack trace.
 */
class LegacyStackTraceAccess extends BaseStackTraceAccess {

	/** */
	LegacyStackTraceAccess() {
	}

	/**
	 * Creates a method handle for {@code sun.reflect.Reflection.getCallerClass(int)}.
	 *
	 * @return Valid method handle if the method is available, otherwise {@code null}
	 */
	MethodHandle getCallerClassGetter() {
		FailableCheck<MethodHandle> check = handle -> LegacyStackTraceAccess.class.equals(handle.invoke(1));
		return getMethod(check, "sun.reflect.Reflection", "getCallerClass", int.class);
	}

	/**
	 * Creates a method handle for {@code Throwable.getStackTraceElement(int)}.
	 *
	 * @return Valid method handle if the method is available, otherwise {@code null}
	 */
	MethodHandle getStackTraceElementGetter() {
		FailableCheck<MethodHandle> check = handle -> {
			StackTraceElement stackTraceElement = (StackTraceElement) handle.invoke(new Throwable(), 0);
			return LegacyStackTraceAccess.class.getName().equals(stackTraceElement.getClassName());
		};

		return getMethod(check, Throwable.class.getName(), "getStackTraceElement", int.class);
	}

}
