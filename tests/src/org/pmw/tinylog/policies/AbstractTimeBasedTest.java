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

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.TimeZone;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

import org.junit.After;
import org.junit.Before;

/**
 * Basis class for times based tests.
 */
public abstract class AbstractTimeBasedTest {

	/**
	 * Milliseconds of an hour
	 */
	protected static final long HOUR = 60L * 60L * 1000L;

	/**
	 * Milliseconds of a day (24 hours)
	 */
	protected static final long DAY = 24L * HOUR;

	/**
	 * Milliseconds of a year (365 days)
	 */
	protected static final long YEAR = 365L * DAY;

	private MockSystem mockSystem;

	/**
	 * Set up the mock for {@link System}.
	 */
	@Before
	public final void init() {
		mockSystem = new MockSystem();
		Mockit.setUpMocks(mockSystem);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * Tear down the mock for {@link System}.
	 */
	@After
	public final void dispose() {
		TimeZone.setDefault(null);
		Mockit.tearDownMocks(System.class);
	}

	/**
	 * Set the current time.
	 * 
	 * @param time
	 *            Current time in milliseconds
	 */
	protected final void setTime(final long time) {
		mockSystem.time = time;
	}

	/**
	 * Increase the current time.
	 * 
	 * @param time
	 *            Milliseconds to add
	 */
	protected final void increaseTime(final long time) {
		mockSystem.time += time;
	}

	/**
	 * Get the calendar of an {@link AbstractTimeBasedPolicy}.
	 * 
	 * @param timeBasedPolicy
	 *            Policy to get {@link Calendar} for
	 * @return Calendar with next rolling time
	 */
	protected static final Calendar getCalendar(final AbstractTimeBasedPolicy timeBasedPolicy) {
		try {
			Field field = AbstractTimeBasedPolicy.class.getDeclaredField("calendar");
			field.setAccessible(true);
			return (Calendar) field.get(timeBasedPolicy);
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Mock for {@link System}.
	 */
	@MockClass(realClass = System.class)
	protected static final class MockSystem {

		private long time;

		/** */
		protected MockSystem() {
			time = 0L;
		}

		/**
		 * Get the current time in milliseconds.
		 * 
		 * @return Current time in milliseconds
		 */
		@Mock
		public long currentTimeMillis() {
			return time;
		}

	}

}
