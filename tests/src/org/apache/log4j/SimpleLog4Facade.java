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
 * Simple Log4j logger that simulate the external logger facade.
 */
class SimpleLog4Facade {

	/**
	 * Get the active logging level for the caller class.
	 * 
	 * @return Active logging level
	 */
	Level getLoggingLevel() {
		return TinylogBride.getLevel();
	}

	/**
	 * Get the active logging level for the caller class.
	 * 
	 * @param callerClass
	 *            Class that has called this method
	 * @return Active logging level
	 */
	Level getLoggingLevel(final Class<?> callerClass) {
		return TinylogBride.getLevel(callerClass);
	}

	/**
	 * Check if a given logging level will be output.
	 * 
	 * @param level
	 *            Logging level to test
	 * @return <code>true</code> if log entries with the given logging level will be output, <code>false</code> if not
	 */
	boolean isEnabled(final Priority level) {
		return TinylogBride.isEnabled(level);
	}

	/**
	 * Check if a given logging level will be output.
	 * 
	 * @param callerClass
	 *            Class that has called this method
	 * @param level
	 *            Logging level to test
	 * @return <code>true</code> if log entries with the given logging level will be output, <code>false</code> if not
	 */
	boolean isEnabled(final Class<?> callerClass, final Priority level) {
		return TinylogBride.isEnabled(callerClass, level);
	}

	/**
	 * Create a log entry.
	 * 
	 * @param level
	 *            Logging level of log entry
	 * @param message
	 *            Message to log
	 */
	void log(final Priority level, final Object message) {
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
	void log(final Priority level, final Object message, final Throwable throwable) {
		TinylogBride.log(level, message, throwable);
	}

}
