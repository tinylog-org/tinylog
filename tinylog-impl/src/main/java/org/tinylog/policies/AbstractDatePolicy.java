/*
 * Copyright 2021 Martin Winandy
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

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class for date based policies.
 *
 * @see DailyPolicy
 * @see MonthlyPolicy
 */
public abstract class AbstractDatePolicy implements Policy {

	/* Regular expression for a time with hours, optional minutes, and optional time zone */
	private static final Pattern TIME_PATTERN = Pattern.compile(
		"^([01]?[0-9]|2[0-3])([^\\d]+([0-5]?[0-9]))?(@(.+))?$"
	);

	private static final int GROUP_HOUR = 1;
	private static final int GROUP_MINUTE = 3;
	private static final int GROUP_ZONE = 5;

	private final Calendar calendar;

	/**
	 * @param name
	 *            Human-readable policy name that can be used in error messages
	 * @param argument
	 *            Time for starting new log file (e.g. "23:30")
	 * @throws IllegalArgumentException
	 *             Invalid time or zone passed in argument
	 */
	protected AbstractDatePolicy(final String name, final String argument) {
		if (argument == null || argument.isEmpty()) {
			calendar = Calendar.getInstance();
			truncate(calendar, 0, 0);
		} else {
			Matcher matcher = TIME_PATTERN.matcher(argument);
			if (matcher.matches()) {
				String hour = matcher.group(GROUP_HOUR);
				String minute = matcher.group(GROUP_MINUTE);

				String zoneId = matcher.group(GROUP_ZONE);
				TimeZone timeZone = zoneId == null ? null : TimeZone.getTimeZone(zoneId);
				if (timeZone == null) {
					calendar = Calendar.getInstance();
				} else if (!timeZone.getID().equals(zoneId)) {
					throw new IllegalArgumentException("Invalid time zone \"" + zoneId + "\" for " + name);
				} else {
					calendar = Calendar.getInstance(timeZone);
				}

				truncate(calendar, Integer.parseInt(hour), minute == null ? 0 : Integer.parseInt(minute));
			} else {
				throw new IllegalArgumentException("Invalid time for " + name + ": " + argument);
			}
		}

		reset();
	}

	@Override
	public boolean continueExistingFile(final String path) {
		Calendar clone = (Calendar) calendar.clone();
		scrollBack(clone);
		return clone.getTimeInMillis() <= new File(path).lastModified();
	}

	@Override
	public boolean continueCurrentFile(final byte[] entry) {
		return calendar.getTimeInMillis() > System.currentTimeMillis();
	}

	@Override
	public void reset() {
		while (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
			scrollAhead(calendar);
		}
	}

	/**
	 * Truncates a calendar to the start of a policy epoch. The start time can be defined by hour and minute.
	 *
	 * @param calendar Calendar to manipulate
	 * @param hourOfDay Start hour
	 * @param minuteOfHour Start minute of passed hour
	 */
	protected abstract void truncate(Calendar calendar, int hourOfDay, int minuteOfHour);

	/**
	 * Scrolls a calendar back to the previous policy epoch.
	 *
	 * @param calendar Calendar to manipulate
	 */
	protected abstract void scrollBack(Calendar calendar);

	/**
	 * Scrolls a calendar ahead to the next policy epoch.
	 *
	 * @param calendar Calendar to manipulate
	 */
	protected abstract void scrollAhead(Calendar calendar);

}
