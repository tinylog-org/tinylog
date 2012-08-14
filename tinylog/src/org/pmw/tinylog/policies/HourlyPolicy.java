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

import org.pmw.tinylog.PropertiesLoader;

/**
 * Policy for one rollover per hour.
 */
public class HourlyPolicy extends AbstractTimeBasedPolicy {

	/**
	 * Trigger the first rollover after one hour uptime (= {@link #HourlyPolicy(boolean) HourlyPolicy(false)}).
	 */
	public HourlyPolicy() {
		this(false);
	}

	/**
	 * @param fullHour
	 *            <code>true</code> to trigger the first rollover at the next full hour, <code>false</code> to trigger
	 *            the first rollover after one hour uptime
	 * 
	 */
	public HourlyPolicy(final boolean fullHour) {
		super(createCalendar(fullHour), Calendar.HOUR_OF_DAY);
	}

	/**
	 * String parameter for {@link PropertiesLoader}.
	 * 
	 * @param fullHour
	 *            <code>true</code> to trigger the first rollover at the next full hour, <code>false</code> to trigger
	 *            the first rollover after one hour uptime
	 */
	HourlyPolicy(final String fullHour) {
		this("true".equalsIgnoreCase(fullHour) || "1".equalsIgnoreCase(fullHour));
	}

	private static Calendar createCalendar(final boolean fullHour) {
		Calendar calendar = Calendar.getInstance();
		if (fullHour) {
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		return calendar;
	}

}
