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

import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Token for outputting a defined thread context property from a log entry.
 */
final class ThreadContextToken implements Token {

	private static final String DEFAULT_EMPTY_VALUE = "";

	private final String key;
	private final String defaultValue;

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
		String value = logEntry.getContext().get(key);
		if (value == null) {
			builder.append(defaultValue);
		} else {
			builder.append(value);
		}
	}

	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		String value = logEntry.getContext().get(key);
		if (value == null && defaultValue != DEFAULT_EMPTY_VALUE) {
			statement.setString(index, defaultValue);
		} else {
			statement.setString(index, value);
		}
	}

}
