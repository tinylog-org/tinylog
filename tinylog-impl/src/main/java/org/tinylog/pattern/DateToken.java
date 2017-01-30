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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import org.tinylog.core.ConfigurationParser;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;

/**
 * Token for outputting the date and time of issue of a log entry.
 */
final class DateToken implements Token {

	private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final int MILLISECONDS_PRECISION = 1;
	private static final int SECONDS_PRECISION = 1000;
	private static final int MINUTES_PRECISION = 60000;

	private static final Locale locale = ConfigurationParser.getLocale();

	private final DateFormat formatter;
	private final long divisor;

	private Date lastDate;
	private String lastFormat;

	/**	*/
	DateToken() {
		this.formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT_PATTERN, locale);
		this.divisor = SECONDS_PRECISION;
	}

	/**
	 * @param pattern
	 *            Format pattern for formatting dates
	 */
	DateToken(final String pattern) {
		this.formatter = new SimpleDateFormat(pattern, locale);
		this.divisor = pattern.contains("SSS") ? MILLISECONDS_PRECISION : pattern.contains("ss") ? SECONDS_PRECISION : MINUTES_PRECISION;
	}

	@Override
	public Collection<LogEntryValue> getRequiredLogEntryValues() {
		return Collections.singletonList(LogEntryValue.DATE);
	}

	@Override
	public void render(final LogEntry logEntry, final StringBuilder builder) {
		builder.append(format(logEntry.getDate()));
	}

	/**
	 * Formats a date. The last formatted date will be cached.
	 *
	 * @param date
	 *            Date to format
	 * @return Formatted date
	 */
	private String format(final Date date) {
		synchronized (formatter) {
			if (lastDate == null || date.getTime() / divisor != lastDate.getTime() / divisor) {
				lastDate = date;
				lastFormat = formatter.format(date);
			}
			return lastFormat;
		}
	}

}
