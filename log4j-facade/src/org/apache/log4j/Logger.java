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


/**
 * log4j logging API.
 */
public class Logger extends Category {

	Logger(final Logger parent, final String name) {
		super(parent, name);
	}

	/**
	 * Get the root logger.
	 *
	 * @return Root logger
	 */
	public static Logger getRootLogger() {
		return LogManager.getRootLogger();
	}

	/**
	 * Get or create a logger.
	 *
	 * @param name
	 *            Name of the logger
	 * @return Logger instance
	 */
	public static Logger getLogger(final String name) {
		return LogManager.getLogger(name);
	}

	/**
	 * Get or create a logger.
	 *
	 * @param clazz
	 *            Class to log
	 * @return Logger instance
	 */
	@SuppressWarnings("rawtypes")
	public static Logger getLogger(final Class clazz) {
		return LogManager.getLogger(clazz);
	}

	/**
	 * Check if log entries with the logging level trace are output or not.
	 *
	 * @return <code>true</code> if trace log entries will be output, <code>false</code> if not
	 */
	public boolean isTraceEnabled() {
		return TinylogBridge.isEnabled(Level.TRACE);
	}

	/**
	 * Create a trace log entry.
	 *
	 * @param message
	 *            Message to log
	 */
	public void trace(final Object message) {
		TinylogBridge.log(Level.TRACE, message);
	}

	/**
	 * Create a trace log entry.
	 *
	 * @param message
	 *            Message to log
	 * @param throwable
	 *            Throwable to log
	 */
	public void trace(final Object message, final Throwable throwable) {
		TinylogBridge.log(Level.TRACE, message, throwable);
	}

}
