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

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * Tests for monthly policy.
 * 
 * @see MonthlyPolicy
 */
public class MonthlyPolicyTest extends AbstractTimeBasedTest {

	/**
	 * Test rolling after one month.
	 */
	@Test
	public final void testRollingAfterOneMonth() {
		setTime(DAY * 16L);

		Policy policy = new MonthlyPolicy();
		assertTrue(policy.check(null, null));
		increaseTime(DAY * 31L - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		policy.reset();
		assertTrue(policy.check(null, null));
		increaseTime(DAY * 28L - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));
	}

	/**
	 * Test rolling after at 1st of next month.
	 */
	@Test
	public final void testRollingAtFirstOfNextMonth() {
		setTime(DAY * 16L);

		Policy policy = new MonthlyPolicy(true);
		assertTrue(policy.check(null, null));
		increaseTime(DAY * 15L - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		policy.reset();
		assertTrue(policy.check(null, null));
		increaseTime(DAY * 28L - 1L);
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
		setTime(DAY * 16L);
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		file.setLastModified(getTime());

		Policy policy = new MonthlyPolicy(true);
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		increaseTime(DAY * 15L - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		increaseTime(-1L);
		policy = new MonthlyPolicy(true);
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		file.delete();

		policy = new MonthlyPolicy();
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
		setTime(DAY * 31L);
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		file.setLastModified(0L);

		Policy policy = new MonthlyPolicy();
		assertFalse(policy.initCheck(file));

		policy = new MonthlyPolicy(false);
		assertFalse(policy.initCheck(file));

		policy = new MonthlyPolicy(true);
		assertFalse(policy.initCheck(file));

		file.delete();
	}

	/**
	 * Test String parameter.
	 */
	@Test
	public final void testStringParameter() {
		setTime(DAY * 16L);

		AbstractTimeBasedPolicy policy = new MonthlyPolicy("true");
		assertEquals(DAY * 31L, getCalendar(policy).getTimeInMillis());

		policy = new MonthlyPolicy("1");
		assertEquals(DAY * 31L, getCalendar(policy).getTimeInMillis());

		policy = new MonthlyPolicy("false");
		assertEquals(DAY * 16L + DAY * 31L, getCalendar(policy).getTimeInMillis());
	}

}
