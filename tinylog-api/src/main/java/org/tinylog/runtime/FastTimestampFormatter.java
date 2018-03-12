/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.runtime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Thread-safe formatter that based on {@link SimpleDateFormat} with millisecond precision. The last formatted timestamp
 * will be cached.
 */
final class FastTimestampFormatter implements TimestampFormatter {

	private static final int MILLISECOND_PRECISION = 1;
	private static final int SECOND_PRECISION = 1000;
	private static final int MINUTE_PRECISION = 60000;

	private final DateFormat formatter;
	private final long divisor;

	private Date lastDate;
	private String lastFormat;

	/**
	 * @param pattern
	 *            Format pattern that is compatible with {@link SimpleDateFormat}
	 * @param locale
	 *            Locale for formatting
	 */
	FastTimestampFormatter(final String pattern, final Locale locale) {
		formatter = new SimpleDateFormat(pattern, locale);
		divisor = pattern.contains("S") ? MILLISECOND_PRECISION : pattern.contains("s") ? SECOND_PRECISION : MINUTE_PRECISION;
	}

	@Override
	public boolean requiresNanoseconds() {
		return false;
	}

	@Override
	public boolean isValid(final String timestamp) {
		try {
			parse(timestamp);
			return true;
		} catch (ParseException ex) {
			return false;
		}
	}

	@Override
	public String format(final Timestamp timestamp) {
		return format(timestamp.toDate());
	}

	/**
	 * Formats a legacy {@link Date}.
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

	/**
	 * Tries to parse a formatted timestamp.
	 *
	 * @param timestamp
	 *            Formatted timestamp
	 * @return Parsed date
	 * @throws ParseException
	 *             Failed to parse given timestamp
	 */
	private Date parse(final String timestamp) throws ParseException {
		synchronized (formatter) {
			return formatter.parse(timestamp);
		}
	}

}
