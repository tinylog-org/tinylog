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

import java.util.Calendar;

/**
 * Policy for triggering a rollover every month at a defined time. The default time is 00:00.
 */
public class MonthlyPolicy extends AbstractDatePolicy {

	/** */
	public MonthlyPolicy() {
		this(null);
	}

	/**
	 * @param argument
	 *            Time for starting new log file (e.g. "23:30")
	 */
	public MonthlyPolicy(final String argument) {
		super("monthly policy", argument);
	}

	@Override
	protected void truncate(final Calendar calendar, final int hourOfDay, final int minuteOfHour) {
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minuteOfHour);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	@Override
	protected void scrollBack(final Calendar calendar) {
		calendar.add(Calendar.MONTH, -1);
	}

	@Override
	protected void scrollAhead(final Calendar calendar) {
		calendar.add(Calendar.MONTH, 1);
	}

}
