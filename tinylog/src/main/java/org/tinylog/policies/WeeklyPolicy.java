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

package org.tinylog.policies;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Policy for one rollover per week.
 */
@PropertiesSupport(name = "weekly")
public final class WeeklyPolicy extends AbstractTimeBasedPolicy {

	private static final Pattern DAY_OF_WEEK_PATTERN = Pattern.compile("[1234567]");

	/**
	 * Trigger a rollover every week (= {@link #WeeklyPolicy(int) WeeklyPolicy(1)}).
	 */
	public WeeklyPolicy() {
		super(createCalendar(1), Calendar.WEEK_OF_YEAR);
	}

	/**
	 * Trigger the first rollover at 00:00 at the defined day of week.
	 * 
	 * @param dayOfWeek
	 *            Day of week (between 1..7) for rollover
	 * @throws IllegalArgumentException
	 *             if dayOfWeek is out of range (1..7)
	 */
	public WeeklyPolicy(final int dayOfWeek) throws IllegalArgumentException {
		super(createCalendar(dayOfWeek), Calendar.WEEK_OF_YEAR);
	}

	/**
	 * String parameter for {@link org.tinylog.PropertiesLoader PropertiesLoader}.
	 * 
	 * @param dayOfWeek
	 *            Day of week (between Monday..Sunday) for rollover
	 * @throws IllegalArgumentException
	 *             if dayOfWeek is unknown
	 */
	WeeklyPolicy(final String dayOfWeek) throws IllegalArgumentException {
		super(createCalendar(dayOfWeek), Calendar.WEEK_OF_YEAR);
	}

	private static Calendar createCalendar(final int dayOfWeek) {
		if (dayOfWeek < 1 || dayOfWeek > 7) {
			throw new IllegalArgumentException("dayOfWeek must be between 1..7");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_WEEK, convert(calendar, dayOfWeek));
		normalize(calendar, Calendar.WEEK_OF_YEAR);
		return calendar;
	}

	private static int convert(final Calendar calendar, final int dayOfWeek) {
		return (calendar.getFirstDayOfWeek() - 1 + dayOfWeek - 1) % 7 + 1;
	}

	private static Calendar createCalendar(final String dayOfWeek) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_WEEK, convert(calendar, dayOfWeek));
		normalize(calendar, Calendar.WEEK_OF_YEAR);
		return calendar;
	}

	private static int convert(final Calendar calendar, final String dayOfWeek) {
		if ("Monday".equalsIgnoreCase(dayOfWeek)) {
			return Calendar.MONDAY;
		} else if ("Tuesday".equalsIgnoreCase(dayOfWeek)) {
			return Calendar.TUESDAY;
		} else if ("Wednesday".equalsIgnoreCase(dayOfWeek)) {
			return Calendar.WEDNESDAY;
		} else if ("Thursday".equalsIgnoreCase(dayOfWeek)) {
			return Calendar.THURSDAY;
		} else if ("Friday".equalsIgnoreCase(dayOfWeek)) {
			return Calendar.FRIDAY;
		} else if ("Saturday".equalsIgnoreCase(dayOfWeek)) {
			return Calendar.SATURDAY;
		} else if ("Sunday".equalsIgnoreCase(dayOfWeek)) {
			return Calendar.SUNDAY;
		} else if (DAY_OF_WEEK_PATTERN.matcher(dayOfWeek).matches()) {
			return convert(calendar, Integer.parseInt(dayOfWeek));
		} else {
			throw new IllegalArgumentException("Unknown day");
		}
	}

}
