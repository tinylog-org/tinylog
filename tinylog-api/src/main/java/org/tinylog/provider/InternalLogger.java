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

package org.tinylog.provider;

import org.tinylog.Level;

/**
 * Static logger for logging internal warnings and errors to console.
 */
public final class InternalLogger {

	private static final int BUFFER_SIZE = 256;

	/** */
	private InternalLogger() {
	}

	/**
	 * Logs a plain text message.
	 *
	 * @param level
	 *            Severity level
	 * @param message
	 *            Plain text message
	 */
	public static void log(final Level level, final String message) {
		System.err.println("LOGGER " + level + ": " + message);
	}

	/**
	 * Logs a caught exception.
	 *
	 * @param level
	 *            Severity level
	 * @param exception
	 *            Caught exception
	 */
	public static void log(final Level level, final Throwable exception) {
		String nameOfException = exception.getClass().getName();
		String messageOfException = exception.getMessage();

		if (messageOfException == null || messageOfException.isEmpty()) {
			System.err.println("LOGGER " + level + ": " + nameOfException);
		} else {
			System.err.println("LOGGER " + level + ": " + messageOfException + " (" + nameOfException + ")");
		}
	}

	/**
	 * Logs a caught exception with a custom text message.
	 *
	 * @param level
	 *            Severity level
	 * @param message
	 *            Plain text message
	 * @param exception
	 *            Caught exception
	 */
	public static void log(final Level level, final Throwable exception, final String message) {
		String nameOfException = exception.getClass().getName();
		String messageOfException = exception.getMessage();

		StringBuilder builder = new StringBuilder(BUFFER_SIZE);
		builder.append("LOGGER ");
		builder.append(level);
		builder.append(": ");
		builder.append(message);
		builder.append(" (");
		builder.append(nameOfException);
		if (messageOfException != null && !messageOfException.isEmpty()) {
			builder.append(": ");
			builder.append(messageOfException);
		}
		builder.append(")");

		System.err.println(builder);
	}

}
