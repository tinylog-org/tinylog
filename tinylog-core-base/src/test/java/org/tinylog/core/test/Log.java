/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage for {@link LogEntry LogEntries}.
 */
public class Log {

	private List<LogEntry> entries;

	/** */
	public Log() {
		entries = new ArrayList<>();
	}

	/**
	 * Appends a new log entry to the end of this log.
	 *
	 * @param entry Log entry to append to this log
	 */
	public void add(LogEntry entry) {
		entries.add(entry);
	}

	/**
	 * Retrieves all stored log entries and clears the entire log afterwards.
	 *
	 * @return All store log entries
	 */
	public Iterable<LogEntry> consume() {
		try {
			return entries;
		} finally {
			entries = new ArrayList<>();
		}
	}

}
