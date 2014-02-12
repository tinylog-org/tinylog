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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.pmw.tinylog.util.FileHelper;

/**
 * Tests for monthly policy.
 * 
 * @see MonthlyPolicy
 */
public class MonthlyPolicyTest extends AbstractTimeBasedTest {

	/**
	 * Test rolling after at 1st of next month.
	 */
	@Test
	public final void testRollingAtFirstOfNextMonth() {
		setTime(DAY * 30 + DAY / 2); // 31th January 1970 12:00

		Policy policy = new MonthlyPolicy();
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY / 2L); // 31th January 1970 24:00
		assertFalse(policy.check((String) null));

		policy.reset();
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 27); // 27th February 1970 24:00
		assertTrue(policy.check((String) null));
		increaseTime(DAY); // 28th February 1970 24:00
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
		setTime(DAY * 30); // 31th January 1970
		File file = FileHelper.createTemporaryFile(null);
		file.setLastModified(getTime());

		Policy policy = new MonthlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
		assertTrue(policy.check((String) null));
		increaseTime(DAY - 1L); // 31th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 31th January 1970 24:00
		assertFalse(policy.check((String) null));

		increaseTime(-1L); // 31th January 1970 23:59:59,999
		policy = new MonthlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 31th January 1970 24:00
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
		setTime(DAY * 30); // 31th January 1970
		File file = FileHelper.createTemporaryFile(null);
		file.setLastModified(getTime());

		Policy policy = new MonthlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));

		increaseTime(DAY); // 1st February 1970

		policy = new MonthlyPolicy();
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

		Policy policy = new MonthlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
	}

}
