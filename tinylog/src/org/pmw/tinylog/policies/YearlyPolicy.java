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
 * Policy for one rollover per year.
 */
public class YearlyPolicy extends AbstractTimeBasedPolicy {

	private static final Pattern MONTH_NUMBER_PATTERN = Pattern.compile("[123456789]|10|11|12");

	/**
	 * Trigger the first rollover after one year uptime.
	 */
	public YearlyPolicy() {
		super(createCalendar(), Calendar.YEAR);
	}

	/**
	 * Trigger the first rollover at the first of the defined month.
	 * 
	 * @param month
	 *            Month (between 1..12) for rollover
	 */
	public YearlyPolicy(final int month) {
		super(createCalendar(month), Calendar.YEAR);
	}

	/**
	 * Trigger the first rollover at first of the defined month.
	 * 
	 * @param month
	 *            Month (between 1..12) for rollover
	 */
	public YearlyPolicy(final String month) {
		this(convert(month));
	}

	/**
	 * Returns the name of the policy.
	 * 
	 * @return "yearly"
	 */
	public static String getName() {
		return "yearly";
	}

	private static Calendar createCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		return calendar;
	}

	private static Calendar createCalendar(final int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MONTH, month - 1);
		normalize(calendar, Calendar.YEAR);
		return calendar;
	}

	private static int convert(final String month) {
		if ("January".equalsIgnoreCase(month)) {
			return 1;
		} else if ("February".equalsIgnoreCase(month)) {
			return 2;
		} else if ("March".equalsIgnoreCase(month)) {
			return 3;
		} else if ("April".equalsIgnoreCase(month)) {
			return 4;
		} else if ("May".equalsIgnoreCase(month)) {
			return 5;
		} else if ("June".equalsIgnoreCase(month)) {
			return 6;
		} else if ("July".equalsIgnoreCase(month)) {
			return 7;
		} else if ("August".equalsIgnoreCase(month)) {
			return 8;
		} else if ("September".equalsIgnoreCase(month)) {
			return 9;
		} else if ("October".equalsIgnoreCase(month)) {
			return 10;
		} else if ("November".equalsIgnoreCase(month)) {
			return 11;
		} else if ("December".equalsIgnoreCase(month)) {
			return 12;
		} else if (MONTH_NUMBER_PATTERN.matcher(month).matches()) {
			return Integer.parseInt(month);
		} else {
			throw new IllegalArgumentException("Unknown month");
		}
	}

}
