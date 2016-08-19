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

import java.lang.reflect.Method;

import org.pmw.tinylog.InternalLogger;

import android.os.Process;
import dalvik.system.VMStack;

/**
 * Runtime dialect implementation for Android's Virtual Machine.
 */
public final class AndroidRuntime implements RuntimeDialect {

	private final Method stackTraceMethod;
	private final int stackTraceOffset;

	/** */
	public AndroidRuntime() {
		StackTraceMethodInfo info = getStackTraceMethod();
		if (info == null) {
			stackTraceMethod = null;
			stackTraceOffset = -1;
		} else {
			stackTraceMethod = info.method;
			stackTraceOffset = info.index;
		}
	}

	@Override
	public String getProcessId() {
		return Integer.toString(Process.myPid());
	}

	@Override
	public String getClassName(final int depth) {
		return getStackTraceElement(depth + 1).getClassName();
	}

	@Override
	public StackTraceElement getStackTraceElement(final int depth) {
		if (stackTraceMethod != null) {
			try {
				StackTraceElement[] trace = new StackTraceElement[depth + stackTraceOffset + 1];
				stackTraceMethod.invoke(null, Thread.currentThread(), trace);
				return trace[depth + stackTraceOffset];
			} catch (Exception ex) {
				InternalLogger.warn(ex, "Failed to get stack trace from dalvik.system.VMStack");
			}
		}

		return new Throwable().getStackTrace()[depth];
	}

	private static StackTraceMethodInfo getStackTraceMethod() {
		try {
			Method method = VMStack.class.getDeclaredMethod("fillStackTraceElements", Thread.class, StackTraceElement[].class);
			method.setAccessible(true);
			StackTraceElement[] trace = new StackTraceElement[10];
			method.invoke(null, Thread.currentThread(), trace);
			for (int i = 0; i < 10; ++i) {
				StackTraceElement element = trace[i];
				if (element != null && AndroidRuntime.class.getName().equals(element.getClassName())) {
					return new StackTraceMethodInfo(method, i);
				}
			}
			return null;
		} catch (Throwable ex) {
			return null;
		}
	}

	private static final class StackTraceMethodInfo {

		private final Method method;
		private final int index;

		private StackTraceMethodInfo(final Method method, final int index) {
			this.method = method;
			this.index = index;
		}

	}

}
