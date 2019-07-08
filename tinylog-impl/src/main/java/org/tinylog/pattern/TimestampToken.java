/*
 * Copyright 2019 Martin Winandy
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
 * Token for outputting the time of issue of a log entry as a Unix timestamp.
 */
final class TimestampToken implements Token {

	private static final long SECONDS_DIVISOR = 1000;

	private final boolean useMilliseconds;

	/**	*/
	TimestampToken() {
		this.useMilliseconds = false;
	}

	/**
	 * @param unit
	 *            Unit of timestamp (e.g. milliseconds)
	 *            Invalid value defaults to seconds.
	 */
	TimestampToken(final String unit) {
		this.useMilliseconds = "milliseconds".equals(unit);
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.singletonList(LogEntryValue.DATE);
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		builder.append(getTime(logEntry));
	}

	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		statement.setLong(index, getTime(logEntry));
	}

	/**
	 * Gets the time of issue from a long entry.
	 *
	 * @param logEntry
	 *            Log entry to get time of issue from
	 * @return Time of issue as Unix timestamp
	 */
	private long getTime(final LogEntry logEntry) {
		long timestamp = logEntry.getTimestamp().toDate().getTime();
		return useMilliseconds ? timestamp : timestamp / SECONDS_DIVISOR;
	}

}
