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

import java.text.MessageFormat;

/**
 * Apache Log4j 1.x compatible parameterized logging class with {@link MessageFormat} pattern syntax.
 *
 * @see MessageFormat#format(String, Object...)
 */
public final class LogMF extends LogXF {

	/** */
	public LogMF() {
	}

	/**
	 * Log a text message at trace level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final boolean argument) {
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at trace level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final byte argument) {
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at trace level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final char argument) {
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at trace level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final short argument) {
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at trace level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final int argument) {
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at trace level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final long argument) {
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at trace level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final float argument) {
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at trace level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final double argument) {
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at trace level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void trace(final Logger logger, final String pattern, final Object argument) {
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at trace level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, first, second));
	}

	/**
	 * Log a text message at trace level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, first, second, third));
	}

	/**
	 * Log a text message at trace level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, first, second, third, fourth));
	}

	/**
	 * Log a text message at trace level. Numbered placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void trace(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.TRACE, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log an exception or another kind of throwable at trace level with an accompanying text message. Numbered
	 * placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.TRACE, throwable, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log a text message at debug level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final boolean argument) {
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at debug level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final byte argument) {
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at debug level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final char argument) {
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at debug level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final short argument) {
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at debug level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final int argument) {
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at debug level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final long argument) {
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at debug level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final float argument) {
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at debug level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final double argument) {
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at debug level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void debug(final Logger logger, final String pattern, final Object argument) {
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at debug level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, first, second));
	}

	/**
	 * Log a text message at debug level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, first, second, third));
	}

	/**
	 * Log a text message at debug level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, first, second, third, fourth));
	}

	/**
	 * Log a text message at debug level. Numbered placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void debug(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.DEBUG, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log an exception or another kind of throwable at debug level with an accompanying text message. Numbered
	 * placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.DEBUG, throwable, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log a text message at info level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final boolean argument) {
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at info level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final byte argument) {
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at info level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final char argument) {
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at info level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final short argument) {
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at info level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final int argument) {
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at info level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final long argument) {
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at info level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final float argument) {
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at info level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final double argument) {
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at info level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void info(final Logger logger, final String pattern, final Object argument) {
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at info level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, first, second));
	}

	/**
	 * Log a text message at info level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, first, second, third));
	}

	/**
	 * Log a text message at info level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, first, second, third, fourth));
	}

	/**
	 * Log a text message at info level. Numbered placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void info(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.INFO, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log an exception or another kind of throwable at info level with an accompanying text message. Numbered
	 * placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.INFO, throwable, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log a text message at warn level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final boolean argument) {
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at warn level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final byte argument) {
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at warn level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final char argument) {
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at warn level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final short argument) {
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at warn level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final int argument) {
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at warn level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final long argument) {
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at warn level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final float argument) {
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at warn level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final double argument) {
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at warn level. "{0}" placeholders will be replaced by the given argument.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param argument
	 *            Argument for text message
	 */
	public static void warn(final Logger logger, final String pattern, final Object argument) {
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at warn level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, first, second));
	}

	/**
	 * Log a text message at warn level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, first, second, third));
	}

	/**
	 * Log a text message at warn level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, first, second, third, fourth));
	}

	/**
	 * Log a text message at warn level. Numbered placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void warn(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.WARN, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log an exception or another kind of throwable at warn level with an accompanying text message. Numbered
	 * placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.WARN, throwable, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log a text message at error level. Numbered placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void error(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.ERROR, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log an exception or another kind of throwable at error level with an accompanying text message. Numbered
	 * placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.ERROR, throwable, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log a text message at error level. Numbered placeholders will be replaced by the given arguments.
	 *
	 * @param logger
	 *            Will be ignored by tinylog
	 * @param pattern
	 *            Text message to log
	 * @param arguments
	 *            Arguments for text message
	 */
	public static void fatal(final Logger logger, final String pattern, final Object[] arguments) {
		TinylogBridge.log(Level.FATAL, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log an exception or another kind of throwable at error level with an accompanying text message. Numbered
	 * placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(Level.FATAL, throwable, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log a text message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a text message at a defined severity level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, first, second));
	}

	/**
	 * Log a text message at a defined severity level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, first, second, third));
	}

	/**
	 * Log a text message at a defined severity level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, first, second, third, fourth));
	}

	/**
	 * Log a text message at a defined severity level. Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(level, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log an exception or another kind of throwable at a defined severity level with an accompanying text message.
	 * Numbered placeholders will be replaced by the given arguments.
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
		TinylogBridge.log(level, throwable, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log a localized message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a localized message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a localized message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a localized message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a localized message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a localized message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a localized message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a localized message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a localized message at a defined severity level. "{0}" placeholders will be replaced by the given argument.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, argument));
	}

	/**
	 * Log a localized message at a defined severity level. Numbered placeholders will be replaced by the given
	 * arguments.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, first, second));
	}

	/**
	 * Log a localized message at a defined severity level. Numbered placeholders will be replaced by the given
	 * arguments.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, first, second, third));
	}

	/**
	 * Log a localized message at a defined severity level. Numbered placeholders will be replaced by the given
	 * arguments.
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
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, first, second, third, fourth));
	}

	/**
	 * Log a localized message at a defined severity level. Numbered placeholders will be replaced by the given
	 * arguments.
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
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final String bundleName, final String key, final Object[] arguments) {
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, new LazyMessageFormat(pattern, arguments));
	}

	/**
	 * Log an exception or another kind of throwable at a defined severity level with an accompanying localized message.
	 * Numbered placeholders will be replaced by the given arguments.
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
	 *            Argument for localized message
	 */
	public static void logrb(final Logger logger, final Level level, final Throwable throwable, final String bundleName, final String key,
			final Object[] arguments) {
		String pattern = getResourceBundleString(bundleName, key);
		TinylogBridge.log(level, throwable, new LazyMessageFormat(pattern, arguments));
	}

	private static final class LazyMessageFormat {

		private final String pattern;
		private final Object[] arguments;

		private LazyMessageFormat(final String pattern, final Object... arguments) {
			this.pattern = pattern;
			this.arguments = arguments;
		}

		@Override
		public String toString() {
			return MessageFormat.format(pattern, arguments);
		}

	}

}
