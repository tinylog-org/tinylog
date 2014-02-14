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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.pmw.tinylog.util.FileHelper;

/**
 * Tests for hourly policy.
 * 
 * @see HourlyPolicy
 */
public class HourlyPolicyTest extends AbstractTimeBasedPolicyTest {

	/**
	 * Test rolling at first full hour.
	 */
	@Test
	public final void testRollingAtFirstFullHour() {
		setTime(HOUR / 2L); // 00:30

		Policy policy = new HourlyPolicy();
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(HOUR / 2L); // 01:00
		assertFalse(policy.check((String) null));

		policy.reset();
		assertTrue(policy.check((String) null));
		increaseTime(HOUR / 2L); // 01:30
		assertTrue(policy.check((String) null));
		increaseTime(HOUR / 2L); // 02:00
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test continuing log files.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testContinueLogFile() throws IOException {
		setTime(HOUR / 2L); // 00:30
		File file = FileHelper.createTemporaryFile(null);
		file.setLastModified(getTime());

		Policy policy = new HourlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
		assertTrue(policy.check((String) null));
		increaseTime(HOUR / 2L - 1L); // 01:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 02:00
		assertFalse(policy.check((String) null));

		increaseTime(-1L); // 01:59:59,999
		policy = new HourlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 02:00
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test discontinuing log files.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testDiscontinueLogFile() throws IOException {
		setTime(HOUR / 2L); // 00:30
		File file = FileHelper.createTemporaryFile(null);
		file.setLastModified(getTime());

		Policy policy = new HourlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));

		increaseTime(HOUR); // 01:30

		policy = new HourlyPolicy();
		policy.init(null);
		assertFalse(policy.check(file));

		file.delete();
	}

	/**
	 * Test non-existing log files.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testNonExistingLogFile() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		file.delete();

		Policy policy = new HourlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
	}

	/**
	 * Test reading hourly policy from properties.
	 */
	@Test
	public final void testFromProperties() {
		Policy policy = createFromProperties("hourly");
		assertNotNull(policy);
		assertEquals(HourlyPolicy.class, policy.getClass());

		policy.init(null);
		increaseTime(HOUR - 1L); // 00:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 01:00
		assertFalse(policy.check((String) null));
	}

}
