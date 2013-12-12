/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog.policies;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Policy for one rollover per day.
 */
@PropertiesSupport(name = "daily")
public final class DailyPolicy extends AbstractTimeBasedPolicy {

	/**
	 * Trigger a rollover every new day.
	 */
	public DailyPolicy() {
		super(createCalendar(0, 0), Calendar.DATE);
	}

	/**
	 * Trigger the first rollover at the defined time.
	 * 
	 * @param hour
	 *            Hour of the day (between 0..23)
	 * @param minute
	 *            Minute of the day (between 0..59)
	 * @throws IllegalArgumentException
	 *             if time is out of range (00:00 - 23:59)
	 */
	public DailyPolicy(final int hour, final int minute) throws IllegalArgumentException {
		super(createCalendar(hour % 24, minute), Calendar.DATE);
	}

	/**
	 * String parameter for {@link org.pmw.tinylog.PropertiesLoader PropertiesLoader}.
	 * 
	 * @param time
	 *            Time of the day (between 00:00..23:59)
	 * @throws IllegalArgumentException
	 *             if time is out of range (00:00 - 23:59)
	 */
	DailyPolicy(final String time) throws IllegalArgumentException {
		this(parseHour(time), parseMinute(time));
	}

	private static Calendar createCalendar(final int hour, final int minute) {
		if (hour < 0) {
			throw new IllegalArgumentException("hour must be between 0..23");
		}
		if (minute < 0 || minute >= 60) {
			throw new IllegalArgumentException("minute must be between 0..59");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		normalize(calendar, Calendar.DATE);
		return calendar;
	}

	private static int parseHour(final String time) {
		String[] parts = time.split(Pattern.quote(":"));
		try {
			return Integer.parseInt(parts[0].trim());
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid time");
		}
	}

	private static int parseMinute(final String time) {
		String[] parts = time.split(Pattern.quote(":"));
		if (parts.length >= 2) {
			try {
				return Integer.parseInt(parts[1].trim());
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Invalid time");
			}
		} else {
			return 0;
		}
	}

}
