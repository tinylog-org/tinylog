/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog;

/**
 * API for external logging facades.
 */
public final class LogEntryForwarder {

	private LogEntryForwarder() {
	}

	/**
	 * Forward a logging message.
	 * 
	 * @param deepOfStackTrace
	 *            Deep of (additional) stack trace (is needed to find the right stack trace element in a stack trace)
	 * @param level
	 *            Logging level
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void forward(final int deepOfStackTrace, final LoggingLevel level, final String message, final Object... arguments) {
		Logger.output(deepOfStackTrace + Logger.DEEP_OF_STACK_TRACE, level, null, message, arguments);
	}

	/**
	 * Forward a logging message.
	 * 
	 * @param deepOfStackTrace
	 *            Deep of (additional) stack trace (is needed to find the right stack trace element in a stack trace)
	 * @param level
	 *            Logging level
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void forward(final int deepOfStackTrace, final LoggingLevel level, final Throwable exception, final String message,
			final Object... arguments) {
		Logger.output(deepOfStackTrace + Logger.DEEP_OF_STACK_TRACE, level, exception, message, arguments);
	}

	/**
	 * Forward a logging message.
	 * 
	 * @param stackTraceElement
	 *            Stack trace element for class, method and source information
	 * @param level
	 *            Logging level
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void forward(final StackTraceElement stackTraceElement, final LoggingLevel level, final String message, final Object... arguments) {
		Logger.output(stackTraceElement, level, null, message, arguments);
	}

	/**
	 * Forward a logging message.
	 * 
	 * @param stackTraceElement
	 *            Stack trace element for class, method and source information
	 * @param level
	 *            Logging level
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void forward(final StackTraceElement stackTraceElement, final LoggingLevel level, final Throwable exception, final String message,
			final Object... arguments) {
		Logger.output(stackTraceElement, level, exception, message, arguments);
	}

}
