/*
 * Copyright 2013 Martin Winandy
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

package org.apache.log4j;

import java.lang.reflect.Method;

import org.pmw.tinylog.LogEntryForwarder;
import org.pmw.tinylog.LoggingLevel;

/**
 * Bridge to tinylog.
 */
class TinylogBride {

	private static Method stackTraceMethod;
	private static Method callerClassMethod;

	static {
		try {
			stackTraceMethod = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
			stackTraceMethod.setAccessible(true);
			StackTraceElement stackTraceElement = (StackTraceElement) stackTraceMethod.invoke(new Throwable(), 0);
			if (!TinylogBride.class.getName().equals(stackTraceElement.getClassName())) {
				stackTraceMethod = null;
			}
		} catch (Exception ex) {
			stackTraceMethod = null;
		}

		try {
			Class<?> reflectionClass = Class.forName("sun.reflect.Reflection");
			callerClassMethod = reflectionClass.getDeclaredMethod("getCallerClass", int.class);
			callerClassMethod.setAccessible(true);
			Class<?> callerClass = (Class<?>) callerClassMethod.invoke(null, 1);
			if (!TinylogBride.class.getName().equals(callerClass.getName())) {
				callerClassMethod = null;
			}
		} catch (Exception ex) {
			callerClassMethod = null;
		}
	}

	private TinylogBride() {
	}

	/**
	 * Get the active logging level for the caller class.
	 * 
	 * @return Active logging level
	 */
	public static Level getLoggingLevel() {
		Package affectedPackage = getPackageFromStackTrace(3);
		LoggingLevel activeLevel = affectedPackage == null ? org.pmw.tinylog.Logger.getLoggingLevel() : org.pmw.tinylog.Logger
				.getLoggingLevel(affectedPackage.getName());
		return toLog4jLevel(activeLevel);
	}

	/**
	 * Check if a given logging level will be output.
	 * 
	 * @param level
	 *            Logging level to test
	 * @return <code>true</code> if log entries with the given logging level will be output, <code>false</code> if not
	 */
	public static boolean isEnabled(final Priority level) {
		Package affectedPackage = getPackageFromStackTrace(3);
		LoggingLevel activeLevel = affectedPackage == null ? org.pmw.tinylog.Logger.getLoggingLevel() : org.pmw.tinylog.Logger
				.getLoggingLevel(affectedPackage.getName());
		return activeLevel.ordinal() <= toTinylogLevel(level).ordinal();
	}

	/**
	 * Create a log entry.
	 * 
	 * @param level
	 *            Logging level of log entry
	 * @param message
	 *            Message to log
	 */
	public static void log(final Priority level, final Object message) {
		LogEntryForwarder.forward(3, toTinylogLevel(level), message);
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
	public static void log(final Priority level, final Object message, final Throwable throwable) {
		LogEntryForwarder.forward(2, toTinylogLevel(level), throwable, message == null ? null : message.toString());
	}

	private static LoggingLevel toTinylogLevel(final Priority level) {
		if (level.isGreaterOrEqual(Level.OFF)) {
			return LoggingLevel.OFF;
		} else if (level.isGreaterOrEqual(Level.ERROR)) {
			return LoggingLevel.ERROR;
		} else if (level.isGreaterOrEqual(Level.WARN)) {
			return LoggingLevel.WARNING;
		} else if (level.isGreaterOrEqual(Level.INFO)) {
			return LoggingLevel.INFO;
		} else if (level.isGreaterOrEqual(Level.DEBUG)) {
			return LoggingLevel.DEBUG;
		} else {
			return LoggingLevel.TRACE;
		}
	}

	private static Level toLog4jLevel(final LoggingLevel level) {
		if (level.ordinal() >= LoggingLevel.OFF.ordinal()) {
			return Level.OFF;
		} else if (level.ordinal() >= LoggingLevel.ERROR.ordinal()) {
			return Level.ERROR;
		} else if (level.ordinal() >= LoggingLevel.WARNING.ordinal()) {
			return Level.WARN;
		} else if (level.ordinal() >= LoggingLevel.INFO.ordinal()) {
			return Level.INFO;
		} else if (level.ordinal() >= LoggingLevel.DEBUG.ordinal()) {
			return Level.DEBUG;
		} else {
			return Level.TRACE;
		}

	}

	private static Package getPackageFromStackTrace(final int deep) {
		if (callerClassMethod != null) {
			try {
				Class<?> callerClass = (Class<?>) callerClassMethod.invoke(null, deep + 1);
				return callerClass.getPackage();
			} catch (Exception ex) {
				// Fallback
			}
		}

		if (stackTraceMethod != null) {
			try {
				return getPackageFromStackTraceElement((StackTraceElement) stackTraceMethod.invoke(new Throwable(), deep));
			} catch (Exception ex) {
				// Fallback
			}
		}

		StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
		return getPackageFromStackTraceElement(stackTraceElements[deep]);
	}

	private static Package getPackageFromStackTraceElement(final StackTraceElement stackTraceElement) {
		if (stackTraceElement == null) {
			return null;
		} else {
			String className = stackTraceElement.getClassName();
			if (className == null) {
				return null;
			} else {
				int index = className.lastIndexOf('.');
				if (index == -1) {
					return Package.getPackage("");
				} else {
					return Package.getPackage(className.substring(0, index));
				}
			}
		}
	}

}
