/*
 * Copyright 2017 Martin Winandy
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.writers.Writer;

/**
 * Writer stores all written log entry objects and can be used for testing the content of created log entries.
 */
public final class StorageWriter implements Writer {

	private static final List<LogEntry> entries = new ArrayList<>();

	private final Collection<LogEntryValue> values;

	/**
	 * @param properties
	 *            Configuration for writer
	 */
	public StorageWriter(final Map<String, String> properties) {
		values = parse(properties.get("values"));
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return values;
	}

	/**
	 * Consumes all stored log entries.
	 *
	 * <p>
	 * Stored log entries will be emptied afterwards.
	 * </p>
	 *
	 * @return Stored log entries
	 */
	public static List<LogEntry> consumeEntries() {
		synchronized (entries) {
			try {
				return new ArrayList<>(entries);
			} finally {
				entries.clear();
			}
		}
	}

	@Override
	public void write(final LogEntry logEntry) throws IOException {
		synchronized (entries) {
			entries.add(logEntry);
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}

	/**
	 * Parses required log entry values from a comma-separated text.
	 *
	 * @param entry
	 *            Comma-separated log entry values
	 * @return Parsed log entry values
	 */
	private static Collection<LogEntryValue> parse(final String entry) {
		if (entry == null) {
			return Collections.emptyList();
		} else {
			String[] values = entry.split(" *, *");
			return Arrays.stream(values).map(String::toUpperCase).map(LogEntryValue::valueOf).collect(Collectors.toSet());
		}
	}

}
