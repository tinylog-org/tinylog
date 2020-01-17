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
import java.util.Locale;

import org.tinylog.configuration.Configuration;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.runtime.TimestampFormatter;

/**
 * Token for outputting the date and time of issue of a log entry.
 */
final class DateToken implements Token {

	private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final Locale locale = Configuration.getLocale();

	private final boolean formatted;
	private final TimestampFormatter formatter;

	/**	*/
	DateToken() {
		this.formatted = false;
		this.formatter = RuntimeProvider.createTimestampFormatter(DEFAULT_DATE_FORMAT_PATTERN, locale);
	}

	/**
	 * @param pattern
	 *            Format pattern for formatting dates
	 */
	DateToken(final String pattern) {
		this.formatted = true;
		this.formatter = RuntimeProvider.createTimestampFormatter(pattern, locale);
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.singletonList(LogEntryValue.DATE);
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		builder.append(formatter.format(logEntry.getTimestamp()));
	}

	@Override
	public void apply(final LogEntry logEntry, final PreparedStatement statement, final int index) throws SQLException {
		if (formatted) {
			statement.setString(index, formatter.format(logEntry.getTimestamp()));
		} else {
			statement.setTimestamp(index, logEntry.getTimestamp().toSqlTimestamp());
		}
	}

}
