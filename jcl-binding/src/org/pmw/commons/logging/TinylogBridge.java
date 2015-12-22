/*
 * Copyright 2015 Martin Winandy
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

package org.pmw.commons.logging;

import java.lang.reflect.Method;

import org.pmw.tinylog.InternalLogger;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.LogEntryForwarder;

/**
 * Bridge to tinylog.
 */
final class TinylogBridge {

	private static Method stackTraceMethod;
	private static boolean hasSunReflection;

	static {
		try {
			stackTraceMethod = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
			stackTraceMethod.setAccessible(true);
			StackTraceElement stackTraceElement = (StackTraceElement) stackTraceMethod.invoke(new Throwable(), 0);
			if (!TinylogBridge.class.getName().equals(stackTraceElement.getClassName())) {
				stackTraceMethod = null;
			}
		} catch (Exception ex) {
			stackTraceMethod = null;
		}

		try {
			@SuppressWarnings({ "restriction", "deprecation" })
			Class<?> caller = sun.reflect.Reflection.getCallerClass(1);
			hasSunReflection = TinylogBridge.class.equals(caller);
		} catch (Exception ex) {
			hasSunReflection = false;
		}
	}

	private TinylogBridge() {
	}

	/**
	 * Check if a given logging level will be output.
	 *
	 * @param level
	 *            Logging level to test
	 * @return <code>true</code> if log entries with the given logging level will be output, <code>false</code> if not
	 */
	public static boolean isEnabled(final Level level) {
		String className = getFullyQualifiedClassName(3);
		org.pmw.tinylog.Level activeLevel = org.pmw.tinylog.Logger.getLevel(className);
		return activeLevel.ordinal() <= level.ordinal();
	}

	/**
	 * Create a log entry.
	 *
	 * @param level
	 *            Logging level of log entry
	 * @param message
	 *            Message to log
	 */
	public static void log(final Level level, final Object message) {
		LogEntryForwarder.forward(2, level, message);
	}

	/**
	 * Create a log entry.
	 *
	 * @param level
	 *            Logging level of log entry
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public static void log(final Level level, final Object message, final Throwable throwable) {
		LogEntryForwarder.forward(2, level, throwable, message == null ? null : message.toString());
	}

	@SuppressWarnings({ "restriction", "deprecation" })
	private static String getFullyQualifiedClassName(final int deep) {
		if (hasSunReflection) {
			try {
				return sun.reflect.Reflection.getCallerClass(deep + 1).getName();
			} catch (Exception ex) {
				InternalLogger.warn(ex, "Failed to get caller class from ");
			}
		}

		if (stackTraceMethod != null) {
			try {
				return ((StackTraceElement) stackTraceMethod.invoke(new Throwable(), deep)).getClassName();
			} catch (Exception ex) {
				InternalLogger.warn(ex, "Failed to get single stack trace element from throwable");
			}
		}

		StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
		return stackTraceElements[deep].getClassName();
	}

}
