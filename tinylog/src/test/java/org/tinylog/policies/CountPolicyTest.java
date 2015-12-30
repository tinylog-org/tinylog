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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.tinylog.hamcrest.ClassMatchers.type;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.tinylog.util.ConfigurationCreator;
import org.tinylog.util.FileHelper;

/**
 * Tests for count policy.
 * 
 * @see CountPolicy
 */
public class CountPolicyTest extends AbstractPolicyTest {

	/**
	 * Test rolling with non-existent log file.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRollingWithNonExistingLogFile() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		file.delete();

		Policy policy = new CountPolicy(2);
		policy.init(ConfigurationCreator.getDummyConfiguration());
		assertTrue(policy.check(file));
		assertTrue(policy.check("1"));
		assertTrue(policy.check("2"));
		assertFalse(policy.check("3"));

		policy.reset();
		assertTrue(policy.check(file));
		assertTrue(policy.check("1"));
		assertTrue(policy.check("2"));
		assertFalse(policy.check("3"));
	}

	/**
	 * Test rolling with existent log file.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRollingWithExistingLogFile() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		FileHelper.write(file, "1", "2", "3", "");
		Policy policy = new CountPolicy(2);
		policy.init(ConfigurationCreator.getDummyConfiguration()); // 1 line per log entry
		assertFalse(policy.check(file));

		FileHelper.write(file, "1", "2", "");
		policy = new CountPolicy(3);
		policy.init(ConfigurationCreator.getDummyConfiguration()); // 1 line per log entry
		assertTrue(policy.check(file));
		assertTrue(policy.check("3"));
		assertFalse(policy.check("4"));

		FileHelper.write(file, "1a", "1b", "2a", "2b", "");
		policy = new CountPolicy(3);
		policy.init(ConfigurationCreator.getDefaultConfiguration()); // 2 lines per log entry
		assertTrue(policy.check(file));
		assertTrue(policy.check("3"));
		assertFalse(policy.check("4"));

		file.delete();
	}

	/**
	 * Test exception for maxSize = 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testForZero() {
		new CountPolicy(0);
	}

	/**
	 * Test exception for maxSize = -1.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testForMinus() {
		new CountPolicy(-1);
	}

	/**
	 * Test String parameter for numeric value.
	 */
	@Test
	public final void testStringParameterForNumber() {
		Policy policy = new CountPolicy("3");
		policy.init(ConfigurationCreator.getDummyConfiguration());
		assertTrue(policy.check("1"));
		assertTrue(policy.check("2"));
		assertTrue(policy.check("3"));
		assertFalse(policy.check("4"));
	}

	/**
	 * Test exception for "-1".
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testStringParameterForMinus() {
		new CountPolicy("-1");
	}

	/**
	 * Test exception for "abc".
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testStringParameterForInvalidString() {
		new CountPolicy("abc");
	}

	/**
	 * Test reading count policy from properties.
	 */
	@Test
	public final void testFromProperties() {
		Policy policy = createFromProperties("count: 3");
		assertThat(policy, type(CountPolicy.class));

		policy.init(ConfigurationCreator.getDummyConfiguration());
		assertTrue(policy.check("1"));
		assertTrue(policy.check("2"));
		assertTrue(policy.check("3"));
		assertFalse(policy.check("4"));
	}

}
