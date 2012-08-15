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
 * Tests for hourly policy.
 * 
 * @see HourlyPolicy
 */
public class HourlyPolicyTest extends AbstractTimeBasedTest {

	/**
	 * Test rolling after one hour.
	 */
	@Test
	public final void testRollingAfterOneHour() {
		setTime(HOUR / 2L);

		IPolicy policy = new HourlyPolicy();
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L);
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L);
		assertFalse(policy.check(null, null));

		policy.reset();
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L);
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L);
		assertFalse(policy.check(null, null));
	}

	/**
	 * Test rolling at first full hour.
	 */
	@Test
	public final void testRollingAtFirstFullHour() {
		setTime(HOUR / 2L);

		IPolicy policy = new HourlyPolicy(true);
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L);
		assertFalse(policy.check(null, null));

		policy.reset();
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L);
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L);
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
		setTime(HOUR / 2L);
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		file.setLastModified(getTime());

		IPolicy policy = new HourlyPolicy(true);
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L - 1L);
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		increaseTime(-1L);
		policy = new HourlyPolicy(true);
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		increaseTime(1L);
		assertFalse(policy.check(null, null));

		file.delete();

		policy = new HourlyPolicy();
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
		setTime(HOUR);
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		file.setLastModified(0L);

		IPolicy policy = new HourlyPolicy();
		assertFalse(policy.initCheck(file));

		policy = new HourlyPolicy(false);
		assertFalse(policy.initCheck(file));

		policy = new HourlyPolicy(true);
		assertFalse(policy.initCheck(file));

		file.delete();
	}

	/**
	 * Test String parameter.
	 */
	@Test
	public final void testStringParameter() {
		setTime(HOUR / 2L);

		AbstractTimeBasedPolicy policy = new HourlyPolicy("true");
		assertEquals(HOUR, getCalendar(policy).getTimeInMillis());

		policy = new HourlyPolicy("1");
		assertEquals(HOUR, getCalendar(policy).getTimeInMillis());

		policy = new HourlyPolicy("false");
		assertEquals(HOUR + HOUR / 2L, getCalendar(policy).getTimeInMillis());
	}

}
