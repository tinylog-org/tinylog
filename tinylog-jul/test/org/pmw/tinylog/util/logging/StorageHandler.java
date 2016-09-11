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

package org.pmw.tinylog.util.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Handler stores all log entries in a list.
 */
public final class StorageHandler extends Handler {

	private List<LogEntry> entries = new ArrayList<>();

	/**
	 * Get and remove all stored log entries.
	 * 
	 * @return Stored log entries
	 */
	public List<LogEntry> consumeLogEntries() {
		try {
			return entries;
		} finally {
			entries = new ArrayList<>();
		}
	}

	@Override
	public void publish(final LogRecord record) {
		entries.add(new LogEntry(record.getLevel(), record.getSourceClassName(), record.getSourceMethodName(), record.getMessage(), record.getThrown()));
	}

	@Override
	public void flush() {
		// Nothing to do
	}

	@Override
	public void close() {
		// Nothing to do
	}

}
