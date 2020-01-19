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
import org.tinylog.format.MessageFormatter;

/**
 * API for providing log entries to a concrete logging framework implementation.
 */
public interface LoggingProvider {

	/**
	 * Returns the corresponding context provider.
	 *
	 * @return Corresponding context provider
	 */
	ContextProvider getContextProvider();

	/**
	 * Gets the lowest activated severity level for all tags.
	 *
	 * <p>
	 * The result of this method is cacheable and can be used to stop processing log entries at an early stage, before
	 * doing any expensive operations. All lower severity levels than the returned level will be never output. But it is
	 * not guaranteed the returned severity level or higher will be really output (for example if output depends on
	 * package or class name).
	 * </p>
	 *
	 * @return Lowest activated severity level
	 */
	Level getMinimumLevel();

	/**
	 * Gets the lowest activated severity level for a tag.
	 *
	 * <p>
	 * The result of this method is cacheable and can be used to stop processing log entries at an early stage, before
	 * doing any expensive operations. All lower severity levels than the returned level will be never output. But it is
	 * not guaranteed the returned severity level or higher will be really output (for example if output depends on
	 * package or class name).
	 * </p>
	 *
	 * @param tag
	 *            Tag to check (can be {@code null})
	 * @return Lowest activated severity level
	 */
	Level getMinimumLevel(String tag);

	/**
	 * Checks whether log entries with given tag and severity level will be output.
	 *
	 * @param depth
	 *            Depth of caller in stack trace (e.g. '1' if there is only one method between caller and this method in
	 *            the stack trace)
	 * @param tag
	 *            Tag to check (can be {@code null})
	 * @param level
	 *            Severity level to check
	 * @return {@code true} if given severity level is enabled, {@code false} if disabled
	 */
	boolean isEnabled(int depth, String tag, Level level);

	/**
	 * Provides a regular log entry.
	 *
	 * @param depth
	 *            Depth of caller in stack trace (e.g. '1' if there is only one method between caller and this method in
	 *            the stack trace)
	 * @param tag
	 *            Tag of log entry or {@code null} if untagged
	 * @param level
	 *            Severity level of log entry
	 * @param exception
	 *            Exception to log or {@code null}
	 * @param formatter
	 *            Formatter for text message, only required if there are any arguments to insert
	 * @param obj
	 *            Message to log or {@code null}
	 * @param arguments
	 *            Arguments for message or {@code null}
	 */
	void log(int depth, String tag, Level level, Throwable exception, MessageFormatter formatter, Object obj, Object... arguments);

	/**
	 * Provides a regular log entry.
	 *
	 * @param loggerClassName
	 *            Fully-qualified class name of the logger instance
	 * @param tag
	 *            Tag of log entry or {@code null} if untagged
	 * @param level
	 *            Severity level of log entry
	 * @param exception
	 *            Exception to log or {@code null}
	 * @param formatter
	 *            Formatter for text message, only required if there are any arguments to insert
	 * @param obj
	 *            Message to log or {@code null}
	 * @param arguments
	 *            Arguments for message or {@code null}
	 */
	void log(String loggerClassName, String tag, Level level, Throwable exception, MessageFormatter formatter, Object obj,
		Object... arguments);

	/**
	 * Shuts down the logging provider and frees all allocated resources. This method should be called only if auto
	 * shutdown is explicitly disabled.
	 * 
	 * @throws InterruptedException
	 *             Interrupted while waiting for complete shutdown
	 */
	void shutdown() throws InterruptedException;

}
