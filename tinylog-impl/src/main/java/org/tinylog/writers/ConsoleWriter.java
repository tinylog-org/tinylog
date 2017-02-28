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
import java.util.Map;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Writer for outputting log entries to system output streams.
 *
 * <p>
 * The error output stream will be used for log entries with the severity level warning and error. The standard output
 * stream will used for all other log entries.
 * </p>
 */
public final class ConsoleWriter extends AbstractFormatPatternWriter {

	/**
	 * @param properties
	 *            Configuration for writer
	 */
	public ConsoleWriter(final Map<String, String> properties) {
		super(properties);
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		Collection<LogEntryValue> logEntryValues = super.getRequiredLogEntryValues();
		logEntryValues.add(LogEntryValue.LEVEL);
		return logEntryValues;
	}

	@Override
	public void write(final LogEntry logEntry) {
		switch (logEntry.getLevel()) {
			case ERROR:
			case WARNING:
				System.err.print(render(logEntry));
				break;

			default:
				System.out.print(render(logEntry));
				break;
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}

}
