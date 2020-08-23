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
 * Base class for stack trace location implementations for Java 8.
 */
abstract class AbstractJavaStackTraceLocation implements StackTraceLocation {

	/**
	 * Property is set to a valid handle for {@code sun.reflect.Reflection.getCallerClass(int)} if this method is
	 * available, or to {@code null} if this method is unavailable.
	 */
	protected static final MethodHandle callerClassGetter;

	/**
	 * Property is set to a valid handle for {@code Throwable.getStackTraceElement(int)} if this method is available,
	 * or to {@code null} if this method is unavailable.
	 */
	protected static final MethodHandle stackTraceElementGetter;

	static {
		LegacyStackTraceAccess access = new LegacyStackTraceAccess();
		callerClassGetter = access.getCallerClassGetter();
		stackTraceElementGetter = access.getStackTraceElementGetter();
	}

}
