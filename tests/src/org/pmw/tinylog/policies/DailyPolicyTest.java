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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * Tests for daily policy.
 * 
 * @see DailyPolicy
 */
public class DailyPolicyTest extends AbstractTimeBasedTest {

	/**
	 * Test rolling after one day.
	 */
	@Test
	public final void testRollingAfterOneDay() {
		setTime(DAY / 2L);

		Policy policy = new DailyPolicy();
		assertTrue(policy.check(null, null));
		increaseTime(DAY - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		policy.reset();
		assertTrue(policy.check(null, null));
		increaseTime(DAY - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));
	}

	/**
	 * Test rolling at midnight.
	 */
	@Test
	public final void testRollingAtMidnight() {
		setTime(DAY / 2L);

		Policy policy = new DailyPolicy(24, 0);
		assertTrue(policy.check(null, null));
		increaseTime(DAY / 2 - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		policy.reset();
		assertTrue(policy.check(null, null));
		increaseTime(DAY - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));
	}

	/**
	 * Test continuing log files.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testContinueLogFile() throws IOException {
		setTime(DAY / 2L);
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		file.setLastModified(getTime());

		Policy policy = new DailyPolicy(14, 0);
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		increaseTime(HOUR * 2 - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		policy = new DailyPolicy(0, 0);
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		increaseTime(HOUR * 10 - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		file.delete();

		policy = new DailyPolicy();
		assertTrue(policy.initCheck(file));
	}

	/**
	 * Test discontinuing log files.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testDisontinueLogFile() throws IOException {
		setTime(DAY);
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		file.setLastModified(0L);

		Policy policy = new DailyPolicy();
		assertFalse(policy.initCheck(file));

		policy = new DailyPolicy(0, 0);
		assertFalse(policy.initCheck(file));

		file.delete();
	}

	/**
	 * Test String parameter.
	 */
	@Test
	public final void testStringParameter() {
		AbstractTimeBasedPolicy policy = new DailyPolicy("00:00");
		assertEquals(DAY, getCalendar(policy).getTimeInMillis());

		policy = new DailyPolicy("12");
		assertEquals(HOUR * 12, getCalendar(policy).getTimeInMillis());

		policy = new DailyPolicy("12:30");
		assertEquals(HOUR * 12 + HOUR / 2, getCalendar(policy).getTimeInMillis());

		policy = new DailyPolicy("24:00");
		assertEquals(DAY, getCalendar(policy).getTimeInMillis());

		try {
			policy = new DailyPolicy("");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException ex) {
			assertEquals(IllegalArgumentException.class, ex.getClass());
		}

		try {
			policy = new DailyPolicy("ab");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException ex) {
			assertEquals(IllegalArgumentException.class, ex.getClass());
		}

		try {
			policy = new DailyPolicy("12:ab");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException ex) {
			assertEquals(IllegalArgumentException.class, ex.getClass());
		}
	}

	/**
	 * Test exception for hour = -1.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testIllegalHour() {
		new DailyPolicy(-1, 0);
	}

	/**
	 * Test exception for minute = -1.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testNegativeMinute() {
		new DailyPolicy(12, -1);
	}

	/**
	 * Test exception for minute = 60.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTooHighMinute() {
		new DailyPolicy(12, 60);
	}

}
