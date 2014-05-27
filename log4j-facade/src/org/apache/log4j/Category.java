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

import sun.reflect.Reflection;

/**
 * Deprecated log4j logging API (use {@link Logger} instead).
 *
 * @see Logger
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class Category {

	private final Category parent;
	private final String name;

	Category(final Category parent, final String name) {
		this.parent = parent;
		this.name = name;
	}

	/**
	 * @deprecated Replaced by {@link Logger#getRootLogger()}
	 */
	@Deprecated
	public static final Category getRoot() {
		return LogManager.getRootLogger();
	}

	/**
	 * @deprecated Replaced by {@link Logger#getLogger(String)}
	 */
	@Deprecated
	public static Category getInstance(final String name) {
		return LogManager.getLogger(name);
	}

	/**
	 * @deprecated Replaced by {@link Logger#getLogger(String)}
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static Category getInstance(final Class clazz) {
		return LogManager.getLogger(clazz);
	}

	/**
	 * Get the parent logger.
	 *
	 * @return Parent logger
	 */
	public final Category getParent() {
		return parent;
	}

	/**
	 * Get the name of the logger.
	 *
	 * @return Name of the logger
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @deprecated Replaced by {@link Category#getLevel()}
	 */
	@Deprecated
	public final Level getPriority() {
		if (TinylogBride.hasSunReflection()) {
			return TinylogBride.getLevel(Reflection.getCallerClass(2));
		} else {
			return TinylogBride.getLevel();
		}
	}

	/**
	 * @deprecated Replaced by {@link Category#getEffectiveLevel()}
	 */
	@Deprecated
	public Priority getChainedPriority() {
		if (TinylogBride.hasSunReflection()) {
			return TinylogBride.getLevel(Reflection.getCallerClass(2));
		} else {
			return TinylogBride.getLevel();
		}
	}

	/**
	 * Get the active logging level for the caller class. In log4j-facade this method does exactly the same as
	 * {@link Category#getEffectiveLevel()}.
	 *
	 * @return Active logging level
	 */
	public final Level getLevel() {
		if (TinylogBride.hasSunReflection()) {
			return TinylogBride.getLevel(Reflection.getCallerClass(2));
		} else {
			return TinylogBride.getLevel();
		}
	}

	/**
	 * Get the active logging level for the caller class. In log4j-facade this method does exactly the same as
	 * {@link Category#getLevel()}.
	 *
	 * @return Active logging level
	 */
	public Level getEffectiveLevel() {
		if (TinylogBride.hasSunReflection()) {
			return TinylogBride.getLevel(Reflection.getCallerClass(2));
		} else {
			return TinylogBride.getLevel();
		}
	}

	/**
	 * Check if log entries with the logging level debug are output or not.
	 *
	 * @return <code>true</code> if debug log entries will be output, <code>false</code> if not
	 */
	public boolean isDebugEnabled() {
		if (TinylogBride.hasSunReflection()) {
			return TinylogBride.isEnabled(Reflection.getCallerClass(2), Level.DEBUG);
		} else {
			return TinylogBride.isEnabled(Level.DEBUG);
		}
	}

	/**
	 * Create a debug log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public void debug(final Object message) {
		TinylogBride.log(Level.DEBUG, message);
	}

	/**
	 * Create a debugr log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public void debug(final Object message, final Throwable throwable) {
		TinylogBride.log(Level.DEBUG, message, throwable);
	}

	/**
	 * Check if log entries with the logging level info are output or not.
	 *
	 * @return <code>true</code> if info log entries will be output, <code>false</code> if not
	 */
	public boolean isInfoEnabled() {
		if (TinylogBride.hasSunReflection()) {
			return TinylogBride.isEnabled(Reflection.getCallerClass(2), Level.INFO);
		} else {
			return TinylogBride.isEnabled(Level.INFO);
		}
	}

	/**
	 * Create an info log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public void info(final Object message) {
		TinylogBride.log(Level.INFO, message);
	}

	/**
	 * Create an info log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public void info(final Object message, final Throwable throwable) {
		TinylogBride.log(Level.INFO, message, throwable);
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public void warn(final Object message) {
		TinylogBride.log(Level.WARN, message);
	}

	/**
	 * Create a warning log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public void warn(final Object message, final Throwable throwable) {
		TinylogBride.log(Level.WARN, message, throwable);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public void error(final Object message) {
		TinylogBride.log(Level.ERROR, message);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public void error(final Object message, final Throwable throwable) {
		TinylogBride.log(Level.ERROR, message, throwable);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public void fatal(final Object message) {
		TinylogBride.log(Level.FATAL, message);
	}

	/**
	 * Create an error log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public void fatal(final Object message, final Throwable throwable) {
		TinylogBride.log(Level.FATAL, message, throwable);
	}

	/**
	 * Check if a given logging level will be output.
	 *
	 * @param level
	 *            Logging level to test
	 * @return <code>true</code> if log entries with the given logging level will be output, <code>false</code> if not
	 */
	public boolean isEnabledFor(final Priority level) {
		if (TinylogBride.hasSunReflection()) {
			return TinylogBride.isEnabled(Reflection.getCallerClass(2), level);
		} else {
			return TinylogBride.isEnabled(level);
		}
	}

	/**
	 * Create an error log entry.
	 *
	 * @param assertion
	 *            If <code>false</code> an error log entry will be generated, otherwise nothing will happen
	 * @param message
	 *            Message to log
	 */
	public void assertLog(final boolean assertion, final String message) {
		if (!assertion) {
			TinylogBride.log(Level.ERROR, message);
		}
	}

	/**
	 * Create a log entry.
	 *
	 * @param level
	 *            Logging level of log entry
	 * @param message
	 *            Message to log
	 */
	public void log(final Priority level, final Object message) {
		TinylogBride.log(level, message);
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
	public void log(final Priority level, final Object message, final Throwable throwable) {
		TinylogBride.log(level, message, throwable);
	}

}
