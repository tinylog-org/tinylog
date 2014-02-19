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

package org.pmw.tinylog.util;

import java.util.Collections;
import java.util.Set;

import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.writers.LogEntry;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * This writer does nothing and just ignores all log entries.
 */
public class NullWriter implements LoggingWriter {

	private final Set<LogEntryValue> requiredLogEntryValues;

	/** */
	public NullWriter() {
		this.requiredLogEntryValues = Collections.emptySet();
	}

	/**
	 * @param requiredLogEntryValues
	 *            Required log entry values
	 */
	public NullWriter(final Set<LogEntryValue> requiredLogEntryValues) {
		this.requiredLogEntryValues = requiredLogEntryValues;
	}

	@Override
	public final Set<LogEntryValue> getRequiredLogEntryValues() {
		return requiredLogEntryValues;
	}

	@Override
	public void init(final Configuration configuration) {
		// Do nothing
	}

	@Override
	public void write(final LogEntry logEntry) {
		// Just ignore
	}

	@Override
	public void flush() {
		// Do nothing
	}

	@Override
	public void close() {
		// Do nothing
	}

}
