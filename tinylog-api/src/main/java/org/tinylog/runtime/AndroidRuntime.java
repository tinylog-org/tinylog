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

package org.tinylog.runtime;

import android.os.Process;

import dalvik.system.VMStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Runtime dialect implementation for Android's Virtual Machine.
 */
final class AndroidRuntime implements RuntimeDialect {

	private static final int STACK_TRACE_SIZE = 10;

	private final Method stackTraceElementsFiller;
	private final int stackTraceOffset;

	/** */
	AndroidRuntime() {
		StackTraceElementsFiller filler = getStackTraceElementsFiller();
		stackTraceElementsFiller = filler.method;
		stackTraceOffset = filler.index;
	}

	@Override
	public String getDefaultWriter() {
		return "logcat";
	}

	@Override
	public int getProcessId() {
		return Process.myPid();
	}

	@Override
	public String getCallerClassName(final int depth) {
		return getCallerStackTraceElement(depth + 1).getClassName();
	}

	@Override
	public StackTraceElement getCallerStackTraceElement(final int depth) {
		if (stackTraceElementsFiller != null) {
			try {
				StackTraceElement[] trace = new StackTraceElement[depth + stackTraceOffset + 1];
				stackTraceElementsFiller.invoke(null, Thread.currentThread(), trace);
				return trace[depth + stackTraceOffset];
			} catch (IllegalAccessException ex) {
				InternalLogger.log(Level.ERROR, ex, "Failed getting stack trace element from dalvik.system.VMStack");
			} catch (InvocationTargetException ex) {
				InternalLogger.log(Level.ERROR, ex.getTargetException(), "Failed getting stack trace element from dalvik.system.VMStack");
			}
		}

		return new Throwable().getStackTrace()[depth];
	}

	/**
	 * Gets {@link VMStack#fillStackTraceElements(Thread, StackTraceElement[])} as accessible method including the
	 * offset position of direct caller.
	 *
	 * @return Method and position if available, {@code null} if not
	 */
	@SuppressWarnings("javadoc")
	private static StackTraceElementsFiller getStackTraceElementsFiller() {
		try {
			Method method = VMStack.class.getDeclaredMethod("fillStackTraceElements", Thread.class, StackTraceElement[].class);
			method.setAccessible(true);
			StackTraceElement[] trace = new StackTraceElement[STACK_TRACE_SIZE];
			method.invoke(null, Thread.currentThread(), trace);
			for (int i = 0; i < STACK_TRACE_SIZE; ++i) {
				StackTraceElement element = trace[i];
				if (element != null && AndroidRuntime.class.getName().equals(element.getClassName())
					&& "getStackTraceElementsFiller".equals(element.getMethodName())) {
					return new StackTraceElementsFiller(method, i);
				}
			}
			return new StackTraceElementsFiller(null, -1);
		} catch (NoClassDefFoundError error) {
			return new StackTraceElementsFiller(null, -1);
		} catch (Exception ex) {
			return new StackTraceElementsFiller(null, -1);
		}
	}

	/**
	 * Data class for storing a method and an index.
	 */
	private static final class StackTraceElementsFiller {

		private final Method method;
		private final int index;

		/**
		 * @param method
		 *            {@link VMStack#fillStackTraceElements(Thread, StackTraceElement[])}
		 * @param index
		 *            Offset position of caller
		 */
		@SuppressWarnings("javadoc")
		private StackTraceElementsFiller(final Method method, final int index) {
			this.method = method;
			this.index = index;
		}

	}

}
