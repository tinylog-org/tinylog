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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import android.os.Process;

import dalvik.system.VMStack;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Runtime dialect implementation for Android's Virtual Machine.
 */
final class AndroidRuntime implements RuntimeDialect {

	private static final int STACK_TRACE_SIZE = 10;

	private static final Timestamp startTime = new LegacyTimestamp();

	private final Method stackTraceElementsFiller;
	private final int stackTraceOffset;

	/** */
	AndroidRuntime() {
		StackTraceElementsFiller filler = getStackTraceElementsFiller();
		stackTraceElementsFiller = filler.method;
		stackTraceOffset = filler.index;
	}

	@Override
	public boolean isAndroid() {
		return true;
	}

	@Override
	public String getDefaultWriter() {
		return "logcat";
	}

	@Override
	public long getProcessId() {
		return Process.myPid();
	}

	@Override
	public Timestamp getStartTime() {
		return startTime;
	}

	@Override
	public String getCallerClassName(final int depth) {
		return getCallerStackTraceElement(depth + 1).getClassName();
	}

	@Override
	public String getCallerClassName(final String loggerClassName) {
		return getCallerStackTraceElement(loggerClassName).getClassName();
	}

	@Override
	public StackTraceElement getCallerStackTraceElement(final int depth) {
		StackTraceElement[] trace = extractCallerStackTraceElements(depth + stackTraceOffset + 1);
		return trace == null ? new Throwable().getStackTrace()[depth] : trace[trace.length - 1];
	}

	@Override
	public StackTraceElement getCallerStackTraceElement(final String loggerClassName) {
		StackTraceElement[] trace = extractCallerStackTraceElements(stackTraceOffset + STACK_TRACE_SIZE);
		if (trace != null) {
			StackTraceElement element = findStackTraceElement(loggerClassName, trace);
			if (element != null) {
				return element;
			}
		}

		StackTraceElement element = findStackTraceElement(loggerClassName, new Throwable().getStackTrace());
		if (element == null) {
			throw new IllegalStateException("Logger class \"" + loggerClassName + "\" is missing in stack trace");
		} else {
			return element;
		}
	}

	@Override
	public Timestamp createTimestamp() {
		return new LegacyTimestamp();
	}

	@Override
	public TimestampFormatter createTimestampFormatter(final String pattern, final Locale locale) {
		return new LegacyTimestampFormatter(pattern, locale);
	}

	/**
	 * Gets {@link VMStack#fillStackTraceElements(Thread, StackTraceElement[])} as accessible method including the
	 * offset position of direct caller.
	 *
	 * @return Method and position if available, {@code null} if unavailable
	 */
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
	 * Gets the stack trace element that appears before the passed logger class name.
	 * 
	 * @param loggerClassName
	 *            Logger class name that should appear before the expected stack trace element
	 * @param trace
	 *            Source stack trace
	 * @return Found stack trace element or {@code null}
	 */
	private static StackTraceElement findStackTraceElement(final String loggerClassName, final StackTraceElement[] trace) {
		int index = 0;
		
		while (index < trace.length && !loggerClassName.equals(trace[index].getClassName())) {
			index = index + 1;
		}
		
		while (index < trace.length && loggerClassName.equals(trace[index].getClassName())) {
			index = index + 1;
		}
		
		if (index < trace.length) {
			return trace[index];
		} else {
			return null;
		}
	}

	/**
	 * Extracts a defined number of elements from stack trace.
	 * 
	 * @param count
	 *            Number of stack trace elements to extract
	 * @return Extracted stack trace elements
	 */
	private StackTraceElement[] extractCallerStackTraceElements(final int count) {
		if (stackTraceElementsFiller != null) {
			try {
				StackTraceElement[] trace = new StackTraceElement[count + 1];
				stackTraceElementsFiller.invoke(null, Thread.currentThread(), trace);
				return trace;
			} catch (IllegalAccessException ex) {
				InternalLogger.log(Level.ERROR, ex, "Failed getting stack trace element from dalvik.system.VMStack");
			} catch (InvocationTargetException ex) {
				InternalLogger.log(Level.ERROR, ex.getTargetException(), "Failed getting stack trace element from dalvik.system.VMStack");
			}
		}

		return null;
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
		private StackTraceElementsFiller(final Method method, final int index) {
			this.method = method;
			this.index = index;
		}

	}

}
