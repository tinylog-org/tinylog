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
 * API for providing log entries to a concrete logging framework implementation.
 */
public interface LoggingProvider {

	/**
	 * Gets the lowest activated severity level.
	 * 
	 * <p>
	 * The result of this method can be used to stop processing log entries at an early stage, before doing any
	 * expensive operations. All lower severity levels than the returned level will be never output. But it is not
	 * guaranteed the returned severity level or higher will be really output (for example if output depends on package
	 * or class name).
	 * </p>
	 * 
	 * @return Lowest activated severity level
	 */
	Level getMinimumLevel();

	/**
	 * Checks whether log entries with given severity level will be output.
	 * 
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is enabled, {@code false} if disabled
	 */
	boolean isEnabled(Level level);

	/**
	 * Provides a regular log entry.
	 * 
	 * @param level
	 *            Severity level of log entry
	 * @param exception
	 *            Exception to log or {@code null}
	 * @param obj
	 *            Message to log or {@code null}
	 * @param arguments
	 *            Arguments for message or {@code null}
	 */
	void log(Level level, Throwable exception, Object obj, Object... arguments);

	/**
	 * Provides an internal log entry (typically used for internal tinylog warnings and errors).
	 * 
	 * @param level
	 *            Severity level of log entry
	 * @param exception
	 *            Exception to log or {@code null}
	 * @param obj
	 *            Message to log or {@code null}
	 * @param arguments
	 *            Arguments for message or {@code null}
	 */
	void internal(Level level, Throwable exception, Object obj, Object... arguments);

}
