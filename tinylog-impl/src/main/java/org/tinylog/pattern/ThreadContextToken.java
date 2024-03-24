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

package org.tinylog.pattern;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Token for outputting a defined thread context property from a log entry.
 */
final class ThreadContextToken implements Token {

	private static final String DEFAULT_EMPTY_VALUE = "";

	private static final String DELIMITER = ", ";

	private static final String SEPARATOR = "=";

	private final String key;
	private final String defaultValue;

	ThreadContextToken() {
		this.key = null;
		this.defaultValue = DEFAULT_EMPTY_VALUE;
	}

	/**
	 * @param key
	 *            Key of thread context property
	 */
	ThreadContextToken(final String key) {
		this.key = key;
		this.defaultValue = DEFAULT_EMPTY_VALUE;
	}

	/**
	 * @param key
	 *            Key of thread context property
	 * @param defaultValue
	 *            Default value if property doesn't exist
	 */
	ThreadContextToken(final String key, final String defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.singletonList(LogEntryValue.CONTEXT);
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		if (key != null) {
			String value = logEntry.getContext().get(key);
			if (value == null) {
				builder.append(defaultValue);
			} else {
				builder.append(value);
			}
		} else {
			boolean first = true;
			for (Map.Entry<String, String> contextEntry : logEntry.getContext().entrySet()) {
				if (first) {
					first = false;
				} else {
					builder.append(DELIMITER);
				}
				builder.append(contextEntry.getKey()).append(SEPARATOR).append(contextEntry.getValue());
			}
		}
	}

	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		if (key == null || key.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			render(logEntry, builder);
			statement.setString(index, builder.toString());
		} else {
			String value = logEntry.getContext().get(key);
			if (value == null && !DEFAULT_EMPTY_VALUE.equals(defaultValue)) {
				statement.setString(index, defaultValue);
			} else {
				statement.setString(index, value);
			}
		}
	}

}
