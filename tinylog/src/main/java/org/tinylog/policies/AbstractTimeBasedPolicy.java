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

import java.io.File;
import java.util.Calendar;

import org.tinylog.Configuration;

/**
 * Base class for time based policies.
 */
public abstract class AbstractTimeBasedPolicy implements Policy {

	private final Calendar calendar;
	private final int field;

	private int multiplier;
	private long nextTime;

	/**
	 * @param calendar
	 *            Time for next rollover
	 * @param field
	 *            Field (e.g. {@link Calendar#HOUR_OF_DAY}) to increase after a rollover
	 */
	public AbstractTimeBasedPolicy(final Calendar calendar, final int field) {
		normalize(calendar, field);

		this.calendar = calendar;
		this.field = field;
		this.multiplier = 1;
		this.nextTime = calendar.getTimeInMillis();
	}

	@Override
	public void init(final Configuration configuration) {
		// Do nothing
	}

	@Override
	public final boolean check(final File logFile) {
		if (logFile.exists()) {
			Calendar clone = (Calendar) calendar.clone();
			clone.add(field, -1);
			return clone.getTimeInMillis() < logFile.lastModified();
		} else {
			return true;
		}
	}

	@Override
	public final boolean check(final String logEntry) {
		return System.currentTimeMillis() < nextTime;
	}

	@Override
	public final void reset() {
		Calendar clone = (Calendar) calendar.clone();
		do {
			clone.add(field, multiplier);
			++multiplier;
		} while (clone.getTimeInMillis() <= System.currentTimeMillis());
		nextTime = clone.getTimeInMillis();
	}

	/**
	 * Make sure that the time is in future.
	 * 
	 * @param calendar
	 *            Calendar to normalize
	 * @param field
	 *            Field (e.g. {@link Calendar#HOUR_OF_DAY}) to increase
	 */
	protected static void normalize(final Calendar calendar, final int field) {
		while (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
			calendar.add(field, 1);
		}
	}

}
