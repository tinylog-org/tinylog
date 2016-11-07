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

package org.tinylog.writers;

import java.util.Set;

import org.tinylog.backend.LogEntry;
import org.tinylog.backend.LogEntryValue;

/**
 * Writers output issued log entries.
 */
public interface Writer {

	/**
	 * Returns all log entry values that are required for outputting a log entry.
	 *
	 * @return All returned values will be filled in passed {@link LogEntry} objects
	 */
	Set<LogEntryValue> getRequiredLogEntryValues();

	/**
	 * Outputs a given log entry.
	 *
	 * <p>
	 * A writer will be called either always or never by writing thread. This depends on the configuration. If a writer
	 * is called by writing thread, it is guaranteed that all calls are from the same thread. In this no synchronization
	 * necessary. If loggers call a writer directly, the calls can be from different threads and synchronization is
	 * required.
	 * </p>
	 *
	 * @param logEntry
	 *            Log entry to output
	 * @param writingThread
	 *            {@code true} if called by writing thread, {@code false} if called directly by logger
	 * @throws Exception
	 *             Any exception can be thrown if writing has been failed
	 */
	void write(LogEntry logEntry, boolean writingThread) throws Exception;

	/**
	 * Outputs buffered log entries immediately.
	 *
	 * @throws Exception
	 *             Any exception can be thrown if flushing has been failed
	 */
	void flush() throws Exception;

	/**
	 * Closes the writer. All allocated resources should be released in this method.
	 *
	 * @throws Exception
	 *             Any exception can be thrown if closing has been failed
	 */
	void close() throws Exception;

}
