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
	 *            Formated text for the log entry
	 * @param arguments
	 *            Arguments for the text message
	 */
	public static void log(final Level level, final String message, final Object... arguments) {
		LogEntryForwarder.forward(2, level, message, arguments);
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

}
