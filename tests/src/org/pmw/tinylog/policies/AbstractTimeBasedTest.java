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

import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.mocks.SystemTimeMock;

/**
 * Basis class for time based tests.
 */
public abstract class AbstractTimeBasedTest extends AbstractTest {

	/**
	 * Milliseconds of a second
	 */
	protected static final long SECOND = 1000L;

	/**
	 * Milliseconds of a minute
	 */
	protected static final long MINUTE = 60L * SECOND;

	/**
	 * Milliseconds of an hour
	 */
	protected static final long HOUR = 60L * MINUTE;

	/**
	 * Milliseconds of a day (24 hours)
	 */
	protected static final long DAY = 24L * HOUR;

	/**
	 * Milliseconds of a year (365 days)
	 */
	protected static final long YEAR = 365L * DAY;

	private SystemTimeMock systemTimeMock;

	/**
	 * Set time zone to UTC and set up the mock for {@link System} (to control time).
	 */
	@Before
	public final void init() {
		systemTimeMock = new SystemTimeMock();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * Tear down mock and reset time zone.
	 */
	@After
	public final void dispose() {
		systemTimeMock.tearDown();
		TimeZone.setDefault(null);
	}

	/**
	 * Get the current time.
	 * 
	 * @return Current time in milliseconds
	 */
	protected final long getTime() {
		return systemTimeMock.currentTimeMillis();
	}

	/**
	 * Set the current time.
	 * 
	 * @param time
	 *            Current time in milliseconds
	 */
	protected final void setTime(final long time) {
		systemTimeMock.setCurrentTimeMillis(time);
	}

	/**
	 * Increase the current time.
	 * 
	 * @param time
	 *            Milliseconds to add
	 */
	protected final void increaseTime(final long time) {
		systemTimeMock.setCurrentTimeMillis(systemTimeMock.currentTimeMillis() + time);
	}

}
