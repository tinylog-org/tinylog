/*
 * Copyright 2014 Martin Winandy
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

package org.slf4j.impl;

import org.tinylog.Level;
import org.tinylog.LogEntryForwarder;

/**
 * Bridge to tinylog.
 */
final class TinylogBridge {

	private static final int DEEP_OF_STACK_TRACE = 2;

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
		return LogEntryForwarder.isEnabled(DEEP_OF_STACK_TRACE, level);
	}

	/**
	 * Create a log entry.
	 *
	 * @param level
	 *            Logging level of log entry
	 * @param message
	 *            Message to log
	 */
	public static void log(final Level level, final String message) {
		LogEntryForwarder.forward(DEEP_OF_STACK_TRACE, level, message);
	}

	/**
	 * Create a log entry.
	 *
	 * @param level
	 *            Logging level of log entry
	 * @param message
	 *            Formatted text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void log(final Level level, final String message, final Object... arguments) {
		LogEntryForwarder.forward(2, level, getLastElementIfThrowable(level, arguments), message, arguments);
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
	public static void log(final Level level, final String message, final Throwable throwable) {
		LogEntryForwarder.forward(2, level, throwable, message);
	}

	/**
	 * If the message being logged is of type ERROR the last object needs to be test to see if it is Throwable,
	 * if not no further action is needed, if so the throwable needs to be tracked so that the stacktrace is logged.
	 *
	 * @param level
	 *            Logging level to test
	 * @param arguments
	 *            Arguments for the text message
	 * @return The Throwable if the last element is Throwable otherwise null.
	 */
	private static Throwable getLastElementIfThrowable(final Level level, final Object... arguments) {
		if (level == Level.ERROR && arguments[arguments.length - 1] instanceof Throwable) {
			return (Throwable) arguments[arguments.length - 1];
		}
		return null;
	}

}
