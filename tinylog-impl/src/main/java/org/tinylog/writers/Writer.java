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

import java.util.Collection;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Writers output issued log entries.
 */
public interface Writer {

	/**
	 * Returns all log entry values that are required for outputting a log entry.
	 *
	 * @return All returned values will be filled in passed {@link LogEntry} objects
	 */
	Collection<LogEntryValue> getRequiredLogEntryValues();

	/**
	 * Outputs a given log entry.
	 *
	 * @param logEntry
	 *            Log entry to output
	 * @throws Exception
	 *             Any exception can be thrown if writing has been failed
	 */
	void write(LogEntry logEntry) throws Exception;

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
