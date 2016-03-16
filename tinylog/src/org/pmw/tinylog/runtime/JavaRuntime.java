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

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

import org.pmw.tinylog.InternalLogger;

/**
 * Runtime dialect implementation for Sun's and Oracle's Java Virtual Machines.
 */
public final class JavaRuntime implements RuntimeDialect {

	private final Method stackTraceMethod;
	private final boolean hasSunReflection;

	/** */
	public JavaRuntime() {
		stackTraceMethod = getStackTraceMethod();
		hasSunReflection = hasSunReflection();
	}

	@Override
	public String getProcessId() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		int index = name.indexOf('@');
		if (index > 0) {
			return name.substring(0, index);
		} else {
			return name;
		}
	}

	@SuppressWarnings({ "restriction", "deprecation" })
	@Override
	public String getClassName(final int deep) {
		if (hasSunReflection) {
			try {
				return sun.reflect.Reflection.getCallerClass(deep + 1).getName();
			} catch (Exception ex) {
				InternalLogger.warn(ex, "Failed to get caller class from sun.reflect.Reflection");
			}
		}

		return getStackTraceElement(deep + 1).getClassName();
	}

	@Override
	public StackTraceElement getStackTraceElement(final int deep) {
		if (stackTraceMethod != null) {
			try {
				return (StackTraceElement) stackTraceMethod.invoke(new Throwable(), deep);
			} catch (Exception ex) {
				InternalLogger.warn(ex, "Failed to get single stack trace element from throwable");
			}
		}

		return new Throwable().getStackTrace()[deep];
	}

	private static Method getStackTraceMethod() {
		try {
			Method method = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
			method.setAccessible(true);
			StackTraceElement stackTraceElement = (StackTraceElement) method.invoke(new Throwable(), 0);
			if (JavaRuntime.class.getName().equals(stackTraceElement.getClassName())) {
				return method;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			return null;
		}
	}

	private static boolean hasSunReflection() {
		try {
			@SuppressWarnings({ "restriction", "deprecation" })
			Class<?> caller = sun.reflect.Reflection.getCallerClass(1);
			return JavaRuntime.class.equals(caller);
		} catch (Throwable ex) {
			return false;
		}
	}

}
