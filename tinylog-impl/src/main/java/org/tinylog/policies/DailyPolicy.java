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

package org.tinylog.policies;

import java.io.File;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Policy for triggering a rollover every day at a define time. The default time is 00:00.
 */
public final class DailyPolicy implements Policy {

	/* Regular expression for a time with hours and optional minutes */
	private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3])([^\\d]+([0-5]?[0-9]))?$");

	private static final int GROUP_HOUR = 1;
	private static final int GROUP_MINUTE = 3;

	private final Calendar calendar;

	/**
	 * @param argument
	 *            Time for starting new log file (e.g. "23:30")
	 */
	public DailyPolicy(final String argument) {
		calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		if (argument == null || argument.isEmpty()) {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
		} else {
			Matcher matcher = TIME_PATTERN.matcher(argument);
			if (matcher.matches()) {
				calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(GROUP_HOUR)));
				calendar.set(Calendar.MINUTE, matcher.groupCount() >= GROUP_MINUTE ? Integer.parseInt(matcher.group(GROUP_MINUTE)) : 0);
			} else {
				throw new IllegalArgumentException("Invalid time for daily policy: " + argument);
			}
		}

		reset();
	}

	@Override
	public boolean continueExistingFile(final String path) {
		Calendar clone = (Calendar) calendar.clone();
		clone.add(Calendar.DATE, -1);
		return clone.getTimeInMillis() <= new File(path).lastModified();
	}

	@Override
	public boolean continueCurrentFile(final byte[] entry) {
		return calendar.getTimeInMillis() > System.currentTimeMillis();
	}

	@Override
	public void reset() {
		while (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
			calendar.add(Calendar.DATE, 1);
		}
	}

}
