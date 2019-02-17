/*
 * Copyright 2019 Martin Winandy
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

import org.apache.log4j.spi.LoggerFactory;

/**
 * This is the central class in the log4j package. Most logging operations, except configuration, are done through this
 * class.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * 
 * @since log4j 1.2
 */
public class Logger extends Category {

	private static final boolean MINIMUM_LEVEL_COVERS_TRACE = isCoveredByMinimumLevel(org.tinylog.Level.TRACE);

	/**
	 * @param parent
	 *            Parent logger ({@code null} for the root logger)
	 * @param name
	 *            Name for the logger (typically the name of the class that will use this logger)
	 */
	Logger(final Logger parent, final String name) {
		super(parent, name);
	}

	/**
	 * @param name
	 *            Name for the logger (typically the name of the class that will use this logger)
	 */
	protected Logger(final String name) {
		super(LogManager.getParentLogger(name), name);
	}

	/**
	 * Retrieve a logger named according to the value of the <code>name</code> parameter. If the named logger already
	 * exists, then the existing instance will be returned. Otherwise, a new instance is created.
	 *
	 * <p>
	 * By default, loggers do not have a set level but inherit it from their nearest ancestor with a set level. This is
	 * one of the central features of log4j.
	 * </p>
	 *
	 * @param name
	 *            The name of the logger to retrieve.
	 * @return Logger instance
	 */
	public static Logger getLogger(final String name) {
		return LogManager.getLogger(name);
	}

	/**
	 * Shorthand for <code>getLogger(clazz.getName())</code>.
	 *
	 * @param clazz
	 *            The name of <code>clazz</code> will be used as the name of the logger to retrieve. See
	 *            {@link #getLogger(String)} for more detailed information.
	 * @return Logger instance
	 */
	@SuppressWarnings("rawtypes")
	public static Logger getLogger(final Class clazz) {
		return LogManager.getLogger(clazz.getName());
	}

	/**
	 * Return the root logger for the current logger repository.
	 * 
	 * <p>
	 * The {@link #getName() Logger.getName()} method for the root logger always returns string value: "root". However,
	 * calling <code>Logger.getLogger("root")</code> does not retrieve the root logger but a logger just under root
	 * named "root".
	 * </p>
	 * 
	 * <p>
	 * In other words, calling this method is the only way to retrieve the root logger.
	 * </p>
	 * 
	 * @return Root logger instance
	 */
	public static Logger getRootLogger() {
		return LogManager.getRootLogger();
	}

	/**
	 * Like {@link #getLogger(String)} except that the type of logger instantiated depends on the type returned by the
	 * {@link LoggerFactory#makeNewLoggerInstance} method of the <code>factory</code> parameter.
	 * 
	 * <p>
	 * This method is intended to be used by sub-classes.
	 * </p>
	 * 
	 * @param name
	 *            The name of the logger to retrieve.
	 * 
	 * @param factory
	 *            A {@link LoggerFactory} implementation that will actually create a new Instance.
	 * 
	 * @return Logger instance
	 * @since 0.8.5
	 */
	public static Logger getLogger(final String name, final LoggerFactory factory) {
		return LogManager.getLogger(name, factory);
	}

	/**
	 * Log a message object with the {@link org.apache.log4j.Level#TRACE TRACE} level.
	 *
	 * @param message
	 *            the message object to log.
	 * @see #debug(Object) for an explanation of the logic applied.
	 * @since 1.2.12
	 */
	public void trace(final Object message) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, null, message, (Object[]) null);
		}
	}

	/**
	 * Log a message object with the <code>TRACE</code> level including the stack trace of the
	 * {@link Throwable}<code>t</code> passed as parameter.
	 *
	 * <p>
	 * See {@link #debug(Object)} form for more detailed information.
	 * </p>
	 *
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 * @since 1.2.12
	 */
	public void trace(final Object message, final Throwable t) {
		if (MINIMUM_LEVEL_COVERS_TRACE) {
			provider.log(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE, t, message == t ? null : message, (Object[]) null);
		}
	}

	/**
	 * Check whether this category is enabled for the TRACE Level.
	 *
	 * @return boolean - <code>true</code> if this category is enabled for level TRACE, <code>false</code> otherwise.
	 * 
	 * @since 1.2.12
	 */
	public boolean isTraceEnabled() {
		return MINIMUM_LEVEL_COVERS_TRACE && provider.isEnabled(STACKTRACE_DEPTH, null, org.tinylog.Level.TRACE);
	}

}
