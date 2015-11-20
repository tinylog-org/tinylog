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

package org.apache.log4j;

/**
 * Apache Log4j 1.x compatible parameterized logging class with SLF4J pattern syntax. "{}" placeholders will be replaced
 * in declared order by given arguments.
 */
public class LogSF extends LogXF {

	/** */
	public LogSF() {
	}

	/**
	 * Log a text message at trace level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final boolean argument) {
		TinylogBridge.log(Level.TRACE, pattern, argument);
	}

	/**
	 * Log a text message at trace level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final byte argument) {
		TinylogBridge.log(Level.TRACE, pattern, argument);
	}

	/**
	 * Log a text message at trace level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final char argument) {
		TinylogBridge.log(Level.TRACE, pattern, argument);
	}

	/**
	 * Log a text message at trace level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final short argument) {
		TinylogBridge.log(Level.TRACE, pattern, argument);
	}

	/**
	 * Log a text message at trace level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final int argument) {
		TinylogBridge.log(Level.TRACE, pattern, argument);
	}

	/**
	 * Log a text message at trace level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final long argument) {
		TinylogBridge.log(Level.TRACE, pattern, argument);
	}

	/**
	 * Log a text message at trace level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final float argument) {
		TinylogBridge.log(Level.TRACE, pattern, argument);
	}

	/**
	 * Log a text message at trace level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final double argument) {
		TinylogBridge.log(Level.TRACE, pattern, argument);
	}

	/**
	 * Log a text message at trace level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final Object argument) {
		TinylogBridge.log(Level.TRACE, pattern, argument);
	}

	/**
	 * Log a text message at trace level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final Object first, final Object second) {
		TinylogBridge.log(Level.TRACE, pattern, first, second);
	}

	/**
	 * Log a text message at trace level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 * @param third
	 *            Third argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final Object first, final Object second, final Object third) {
		TinylogBridge.log(Level.TRACE, pattern, first, second, third);
	}

	/**
	 * Log a text message at trace level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 * @param third
	 *            Third argument for text message
	 * @param fourth
	 *            Fourth argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final Object first, final Object second, final Object third, final Object fourth) {
		TinylogBridge.log(Level.TRACE, pattern, first, second, third, fourth);
	}

	/**
	 * Log a text message at trace level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void trace(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.TRACE, pattern, arguments);
	}

	/**
	 * Log an exception or another kind of throwable at trace level with an accompanying text message. "{}" placeholders
	 * will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param throwable
	 *            Throwable to log
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void trace(final Logger logger, final Throwable throwable, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.TRACE, throwable, pattern, arguments);
	}

	/**
	 * Log a text message at debug level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final boolean argument) {
		TinylogBridge.log(Level.DEBUG, pattern, argument);
	}

	/**
	 * Log a text message at debug level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final byte argument) {
		TinylogBridge.log(Level.DEBUG, pattern, argument);
	}

	/**
	 * Log a text message at debug level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final char argument) {
		TinylogBridge.log(Level.DEBUG, pattern, argument);
	}

	/**
	 * Log a text message at debug level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final short argument) {
		TinylogBridge.log(Level.DEBUG, pattern, argument);
	}

	/**
	 * Log a text message at debug level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final int argument) {
		TinylogBridge.log(Level.DEBUG, pattern, argument);
	}

	/**
	 * Log a text message at debug level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final long argument) {
		TinylogBridge.log(Level.DEBUG, pattern, argument);
	}

	/**
	 * Log a text message at debug level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final float argument) {
		TinylogBridge.log(Level.DEBUG, pattern, argument);
	}

	/**
	 * Log a text message at debug level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final double argument) {
		TinylogBridge.log(Level.DEBUG, pattern, argument);
	}

	/**
	 * Log a text message at debug level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final Object argument) {
		TinylogBridge.log(Level.DEBUG, pattern, argument);
	}

	/**
	 * Log a text message at debug level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final Object first, final Object second) {
		TinylogBridge.log(Level.DEBUG, pattern, first, second);
	}

	/**
	 * Log a text message at debug level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 * @param third
	 *            Third argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final Object first, final Object second, final Object third) {
		TinylogBridge.log(Level.DEBUG, pattern, first, second, third);
	}

	/**
	 * Log a text message at debug level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 * @param third
	 *            Third argument for text message
	 * @param fourth
	 *            Fourth argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final Object first, final Object second, final Object third, final Object fourth) {
		TinylogBridge.log(Level.DEBUG, pattern, first, second, third, fourth);
	}

	/**
	 * Log a text message at debug level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void debug(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.DEBUG, pattern, arguments);
	}

	/**
	 * Log an exception or another kind of throwable at debug level with an accompanying text message. "{}" placeholders
	 * will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param throwable
	 *            Throwable to log
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void debug(final Logger logger, final Throwable throwable, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.DEBUG, throwable, pattern, arguments);
	}

	/**
	 * Log a text message at info level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final boolean argument) {
		TinylogBridge.log(Level.INFO, pattern, argument);
	}

	/**
	 * Log a text message at info level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final byte argument) {
		TinylogBridge.log(Level.INFO, pattern, argument);
	}

	/**
	 * Log a text message at info level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final char argument) {
		TinylogBridge.log(Level.INFO, pattern, argument);
	}

	/**
	 * Log a text message at info level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final short argument) {
		TinylogBridge.log(Level.INFO, pattern, argument);
	}

	/**
	 * Log a text message at info level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final int argument) {
		TinylogBridge.log(Level.INFO, pattern, argument);
	}

	/**
	 * Log a text message at info level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final long argument) {
		TinylogBridge.log(Level.INFO, pattern, argument);
	}

	/**
	 * Log a text message at info level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final float argument) {
		TinylogBridge.log(Level.INFO, pattern, argument);
	}

	/**
	 * Log a text message at info level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final double argument) {
		TinylogBridge.log(Level.INFO, pattern, argument);
	}

	/**
	 * Log a text message at info level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final Object argument) {
		TinylogBridge.log(Level.INFO, pattern, argument);
	}

	/**
	 * Log a text message at info level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final Object first, final Object second) {
		TinylogBridge.log(Level.INFO, pattern, first, second);
	}

	/**
	 * Log a text message at info level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 * @param third
	 *            Third argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final Object first, final Object second, final Object third) {
		TinylogBridge.log(Level.INFO, pattern, first, second, third);
	}

	/**
	 * Log a text message at info level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 * @param third
	 *            Third argument for text message
	 * @param fourth
	 *            Fourth argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final Object first, final Object second, final Object third, final Object fourth) {
		TinylogBridge.log(Level.INFO, pattern, first, second, third, fourth);
	}

	/**
	 * Log a text message at info level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void info(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.INFO, pattern, arguments);
	}

	/**
	 * Log an exception or another kind of throwable at info level with an accompanying text message. "{}" placeholders
	 * will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param throwable
	 *            Throwable to log
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void info(final Logger logger, final Throwable throwable, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.INFO, throwable, pattern, arguments);
	}

	/**
	 * Log a text message at warn level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final boolean argument) {
		TinylogBridge.log(Level.WARN, pattern, argument);
	}

	/**
	 * Log a text message at warn level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final byte argument) {
		TinylogBridge.log(Level.WARN, pattern, argument);
	}

	/**
	 * Log a text message at warn level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final char argument) {
		TinylogBridge.log(Level.WARN, pattern, argument);
	}

	/**
	 * Log a text message at warn level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final short argument) {
		TinylogBridge.log(Level.WARN, pattern, argument);
	}

	/**
	 * Log a text message at warn level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final int argument) {
		TinylogBridge.log(Level.WARN, pattern, argument);
	}

	/**
	 * Log a text message at warn level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final long argument) {
		TinylogBridge.log(Level.WARN, pattern, argument);
	}

	/**
	 * Log a text message at warn level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final float argument) {
		TinylogBridge.log(Level.WARN, pattern, argument);
	}

	/**
	 * Log a text message at warn level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final double argument) {
		TinylogBridge.log(Level.WARN, pattern, argument);
	}

	/**
	 * Log a text message at warn level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final Object argument) {
		TinylogBridge.log(Level.WARN, pattern, argument);
	}

	/**
	 * Log a text message at warn level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final Object first, final Object second) {
		TinylogBridge.log(Level.WARN, pattern, first, second);
	}

	/**
	 * Log a text message at warn level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 * @param third
	 *            Third argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final Object first, final Object second, final Object third) {
		TinylogBridge.log(Level.WARN, pattern, first, second, third);
	}

	/**
	 * Log a text message at warn level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 * @param third
	 *            Third argument for text message
	 * @param fourth
	 *            Fourth argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final Object first, final Object second, final Object third, final Object fourth) {
		TinylogBridge.log(Level.WARN, pattern, first, second, third, fourth);
	}

	/**
	 * Log a text message at warn level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void warn(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.WARN, pattern, arguments);
	}

	/**
	 * Log an exception or another kind of throwable at warn level with an accompanying text message. "{}" placeholders
	 * will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param throwable
	 *            Throwable to log
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void warn(final Logger logger, final Throwable throwable, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.WARN, throwable, pattern, arguments);
	}

	/**
	 * Log a text message at error level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void error(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.ERROR, pattern, arguments);
	}

	/**
	 * Log an exception or another kind of throwable at error level with an accompanying text message. "{}" placeholders
	 * will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param throwable
	 *            Throwable to log
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void error(final Logger logger, final Throwable throwable, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.ERROR, throwable, pattern, arguments);
	}

	/**
	 * Log a text message at error level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void fatal(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.FATAL, pattern, arguments);
	}

	/**
	 * Log an exception or another kind of throwable at error level with an accompanying text message. "{}" placeholders
	 * will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param throwable
	 *            Throwable to log
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void fatal(final Logger logger, final Throwable throwable, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.FATAL, throwable, pattern, arguments);
	}

	/**
	 * Log a text message at a defined severity level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final boolean argument) {
		TinylogBridge.log(level, pattern, argument);
	}

	/**
	 * Log a text message at a defined severity level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final byte argument) {
		TinylogBridge.log(level, pattern, argument);
	}

	/**
	 * Log a text message at a defined severity level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final char argument) {
		TinylogBridge.log(level, pattern, argument);
	}

	/**
	 * Log a text message at a defined severity level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final short argument) {
		TinylogBridge.log(level, pattern, argument);
	}

	/**
	 * Log a text message at a defined severity level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final int argument) {
		TinylogBridge.log(level, pattern, argument);
	}

	/**
	 * Log a text message at a defined severity level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final long argument) {
		TinylogBridge.log(level, pattern, argument);
	}

	/**
	 * Log a text message at a defined severity level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final float argument) {
		TinylogBridge.log(level, pattern, argument);
	}

	/**
	 * Log a text message at a defined severity level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final double argument) {
		TinylogBridge.log(level, pattern, argument);
	}

	/**
	 * Log a text message at a defined severity level. First "{}" placeholder will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final Object argument) {
		TinylogBridge.log(level, pattern, argument);
	}

	/**
	 * Log a text message at a defined severity level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final Object first, final Object second) {
		TinylogBridge.log(level, pattern, first, second);
	}

	/**
	 * Log a text message at a defined severity level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 * @param third
	 *            Third argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final Object first, final Object second, final Object third) {
		TinylogBridge.log(level, pattern, first, second, third);
	}

	/**
	 * Log a text message at a defined severity level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param first
	 *            First argument for text message
	 * @param second
	 *            Second argument for text message
	 * @param third
	 *            Third argument for text message
	 * @param fourth
	 *            Fourth argument for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final Object first, final Object second, final Object third,
			final Object fourth) {
		TinylogBridge.log(level, pattern, first, second, third, fourth);
	}

	/**
	 * Log a text message at a defined severity level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void log(final Logger logger, final Level level, final String pattern, final Object[] arguments) {
		TinylogBridge.log(level, pattern, arguments);
	}

	/**
	 * Log an exception or another kind of throwable at a defined severity level with an accompanying text message. "{}"
	 * placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param throwable
	 *            Throwable to log
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void log(final Logger logger, final Level level, final Throwable throwable, final String pattern, final Object[] arguments) {
		TinylogBridge.log(level, throwable, pattern, arguments);
	}

	/**
	 * Log a localized message at a defined severity level. First "{}" placeholder will be replaced by the given
	 * argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param argument
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final boolean argument) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), argument);
	}

	/**
	 * Log a localized message at a defined severity level. First "{}" placeholder will be replaced by the given
	 * argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param argument
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final byte argument) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), argument);
	}

	/**
	 * Log a localized message at a defined severity level. First "{}" placeholder will be replaced by the given
	 * argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param argument
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final char argument) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), argument);
	}

	/**
	 * Log a localized message at a defined severity level. First "{}" placeholder will be replaced by the given
	 * argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param argument
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final short argument) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), argument);
	}

	/**
	 * Log a localized message at a defined severity level. First "{}" placeholder will be replaced by the given
	 * argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param argument
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final int argument) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), argument);
	}

	/**
	 * Log a localized message at a defined severity level. First "{}" placeholder will be replaced by the given
	 * argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param argument
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final long argument) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), argument);
	}

	/**
	 * Log a localized message at a defined severity level. First "{}" placeholder will be replaced by the given
	 * argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param argument
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final float argument) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), argument);
	}

	/**
	 * Log a localized message at a defined severity level. First "{}" placeholder will be replaced by the given
	 * argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param argument
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final double argument) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), argument);
	}

	/**
	 * Log a localized message at a defined severity level. First "{}" placeholder will be replaced by the given
	 * argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param argument
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final Object argument) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), argument);
	}

	/**
	 * Log a localized message at a defined severity level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param first
	 *            First argument for localized message
	 * @param second
	 *            Second argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final Object first, final Object second) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), first, second);
	}

	/**
	 * Log a localized message at a defined severity level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param first
	 *            First argument for localized message
	 * @param second
	 *            Second argument for localized message
	 * @param third
	 *            Third argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final Object first, final Object second,
			final Object third) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), first, second, third);
	}

	/**
	 * Log a localized message at a defined severity level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param first
	 *            First argument for localized message
	 * @param second
	 *            Second argument for localized message
	 * @param third
	 *            Third argument for localized message
	 * @param fourth
	 *            Fourth argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final Object first, final Object second,
			final Object third, final Object fourth) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), first, second, third, fourth);
	}

	/**
	 * Log a localized message at a defined severity level. "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param arguments
	 *            Arguments for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final Object[] arguments) {
		TinylogBridge.log(level, getResourceBundleString(bundleName, key), arguments);
	}

	/**
	 * Log an exception or another kind of throwable at a defined severity level with an accompanying localized message.
	 * "{}" placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param level
	 *            Severity level
	 * @param throwable
	 *            Throwable to log
	 * @param bundleName
	 *            Name of resource bundle
	 * @param key
	 *            Key of localized message to log
	 * @param arguments
	 *            Arguments for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final Throwable throwable, final String bundleName, final String key,
			final Object[] arguments) {
		TinylogBridge.log(level, throwable, getResourceBundleString(bundleName, key), arguments);
	}
	
}
