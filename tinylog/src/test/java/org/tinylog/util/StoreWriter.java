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

package org.tinylog.util;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.tinylog.Configuration;
import org.tinylog.LogEntry;
import org.tinylog.writers.LogEntryValue;
import org.tinylog.writers.Writer;

/**
 * A writer that just save the written log entry as string.
 */
public final class StoreWriter implements Writer {

	private final Set<LogEntryValue> requiredLogEntryValue;
	private LogEntry logEntry;

	/**
	 * Create a new instance of this writer that required {@link LogEntryValue#LEVEL},
	 * {@link LogEntryValue#MESSAGE} and {@link LogEntryValue#EXCEPTION}.
	 */
	public StoreWriter() {
		this.requiredLogEntryValue = EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
	}

	/**
	 * Create a new instance of this writer that required the defined log entry values.
	 * 
	 * @param requiredLogEntryValues
	 *            Required log entry values
	 */
	public StoreWriter(final LogEntryValue... requiredLogEntryValues) {
		this.requiredLogEntryValue = EnumSet.copyOf(Arrays.asList(requiredLogEntryValues));
	}

	/**
	 * Create a new instance of this writer that required the defined log entry values.
	 * 
	 * @param requiredLogEntryValues
	 *            Required log entry values
	 */
	public StoreWriter(final Set<LogEntryValue> requiredLogEntryValues) {
		this.requiredLogEntryValue = requiredLogEntryValues;
	}

	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return requiredLogEntryValue;
	}

	@Override
	public void init(final Configuration configuration) {
		// Do nothing
	}

	@Override
	public void write(final LogEntry logEntry) {
		if (this.logEntry != null) {
			throw new RuntimeException("Previous message wasn't consumed");
		}
		this.logEntry = logEntry;
	}

	@Override
	public void flush() {
		// Do nothing
	}

	@Override
	public void close() {
		// Do nothing
	}

	/**
	 * Get and remove the last written log entry.
	 * 
	 * @return Last written log entry
	 */
	public LogEntry consumeLogEntry() {
		LogEntry backup = this.logEntry;
		this.logEntry = null;
		return backup;
	}

}
