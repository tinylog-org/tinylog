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

package org.tinylog.core.runtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Thread-safe timestamp formatter implementation for {@link LegacyTimestamp LegacyTimestamps}.
 */
public final class LegacyTimestampFormatter implements TimestampFormatter<Date> {

	private static final int MILLISECOND_PRECISION = 1;
	private static final int SECOND_PRECISION = 1000;
	private static final int MINUTE_PRECISION = 60000;

	private final DateFormat formatter;
	private final long divisor;

	private Date lastDate;
	private String lastFormat;

	/**
	 * @param pattern Date and time pattern, compatible with {@link SimpleDateFormat}
	 * @param locale Locale for language or country depending format outputs
	 */
	LegacyTimestampFormatter(String pattern, Locale locale) {
		formatter = new SimpleDateFormat(pattern, locale);
		divisor = pattern.contains("S") ? MILLISECOND_PRECISION
			: pattern.contains("s") ? SECOND_PRECISION
			: MINUTE_PRECISION;
	}

	@Override
	public String format(Timestamp<Date> timestamp) {
		Date date = timestamp.resole();

		synchronized (formatter) {
			if (lastDate == null || date.getTime() / divisor != lastDate.getTime() / divisor) {
				lastDate = date;
				lastFormat = formatter.format(date);
			}
			return lastFormat;
		}
	}

}
