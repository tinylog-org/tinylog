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
 * Tests for yearly policy.
 * 
 * @see YearlyPolicy
 */
public class YearlyPolicyTest extends AbstractTimeBasedTest {

	/**
	 * Test rolling after one year.
	 */
	@Test
	public final void testRollingAfterOneYear() {
		setTime(YEAR / 2L);

		Policy policy = new YearlyPolicy();
		assertTrue(policy.check(null, null));
		increaseTime(YEAR - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		policy.reset();
		assertTrue(policy.check(null, null));
		increaseTime(YEAR - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertTrue(policy.check(null, null)); // Leap year
		increaseTime(DAY - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));
	}

	/**
	 * Test rolling at the end of the year.
	 */
	@Test
	public final void testRollingAtEndOfYear() {
		setTime(YEAR / 2L);

		Policy policy = new YearlyPolicy(1);
		assertTrue(policy.check(null, null));
		increaseTime(YEAR / 2L - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		policy.reset();
		assertTrue(policy.check(null, null));
		increaseTime(YEAR - 1L);
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
		setTime(0L);
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		file.setLastModified(getTime() + 1L);

		Policy policy = new YearlyPolicy(1);
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		increaseTime(DAY * 365L - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		file.delete();

		policy = new YearlyPolicy();
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
		setTime(0L);
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		file.setLastModified(getTime());

		Policy policy = new YearlyPolicy();
		assertFalse(policy.initCheck(file));

		policy = new YearlyPolicy(1);
		assertFalse(policy.initCheck(file));

		file.delete();
	}

	/**
	 * Test String parameter.
	 */
	@Test
	public final void testStringParameter() {
		AbstractTimeBasedPolicy policy = new YearlyPolicy("1");
		assertEquals(YEAR, getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("12");
		assertEquals(YEAR - DAY * 31L, getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("January");
		assertEquals(YEAR, getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("february");
		assertEquals(DAY * 31L, getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("MARCH");
		assertEquals(DAY * (31L + 28L), getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("April");
		assertEquals(DAY * (31L + 28L + 31L), getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("May");
		assertEquals(DAY * (31L + 28L + 31L + 30L), getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("June");
		assertEquals(DAY * (31L + 28L + 31L + 30L + 31L), getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("July");
		assertEquals(DAY * (31L + 28L + 31L + 30L + 31L + 30L), getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("August");
		assertEquals(DAY * (31L + 28L + 31L + 30L + 31L + 30L + 31L), getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("September");
		assertEquals(DAY * (31L + 28L + 31L + 30L + 31L + 30L + 31L + 31L), getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("October");
		assertEquals(DAY * (31L + 28L + 31L + 30L + 31L + 30L + 31L + 31L + 30L), getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("November");
		assertEquals(DAY * (31L + 28L + 31L + 30L + 31L + 30L + 31L + 31L + 30L + 31L), getCalendar(policy).getTimeInMillis());

		policy = new YearlyPolicy("December");
		assertEquals(YEAR - DAY * 31L, getCalendar(policy).getTimeInMillis());

		try {
			policy = new YearlyPolicy("");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException ex) {
			assertEquals(IllegalArgumentException.class, ex.getClass());
		}

		try {
			policy = new YearlyPolicy("0");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException ex) {
			assertEquals(IllegalArgumentException.class, ex.getClass());
		}

		try {
			policy = new YearlyPolicy("13");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException ex) {
			assertEquals(IllegalArgumentException.class, ex.getClass());
		}
	}

}
