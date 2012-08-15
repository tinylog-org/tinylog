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
 * Policy for one rollover per month.
 */
public class MonthlyPolicy extends AbstractTimeBasedPolicy {

	/**
	 * Trigger the first rollover after one month uptime (= {@link #MonthlyPolicy(boolean) MonthlyPolicy(false)}).
	 */
	public MonthlyPolicy() {
		this(false);
	}

	/**
	 * @param firstOfMonth
	 *            <code>true</code> to trigger the first rollover at the first of the next month, <code>false</code> to
	 *            trigger the first rollover after one month uptime
	 * 
	 */
	public MonthlyPolicy(final boolean firstOfMonth) {
		super(createCalendar(firstOfMonth), Calendar.MONTH);
	}

	/**
	 * String parameter for {@link PropertiesLoader}.
	 * 
	 * @param firstOfMonth
	 *            <code>true</code> to trigger the first rollover at the first of the next month, <code>false</code> to
	 *            trigger the first rollover after one month uptime
	 * 
	 */
	MonthlyPolicy(final String firstOfMonth) {
		this("true".equalsIgnoreCase(firstOfMonth) || "1".equalsIgnoreCase(firstOfMonth));
	}

	/**
	 * Returns the name of the policy.
	 * 
	 * @return "monthly"
	 */
	public static String getName() {
		return "monthly";
	}

	private static Calendar createCalendar(final boolean firstOfMonth) {
		Calendar calendar = Calendar.getInstance();
		if (firstOfMonth) {
			calendar.set(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			normalize(calendar, Calendar.MONTH);
		} else {
			calendar.add(Calendar.MONTH, 1);
		}
		return calendar;
	}

}
