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

/**
 * Policy for one rollover per month.
 */
public class MonthlyPolicy extends AbstractTimeBasedPolicy {

	/**
	 * Trigger a rollover every new month.
	 */
	public MonthlyPolicy() {
		super(createCalendar(), Calendar.MONTH);
	}

	/**
	 * Returns the name of the policy.
	 * 
	 * @return "monthly"
	 */
	public static String getName() {
		return "monthly";
	}

	private static Calendar createCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		normalize(calendar, Calendar.MONTH);
		return calendar;
	}

}
