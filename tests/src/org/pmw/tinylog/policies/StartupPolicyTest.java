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

/**
 * Tests for startup policy.
 * 
 * @see StartupPolicy
 */
public class StartupPolicyTest {

	/**
	 * Test rolling.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testRolling() throws IOException {
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();

		IPolicy policy = new StartupPolicy();
		assertFalse(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		assertTrue(policy.check(null, null));
		policy.reset();
		assertTrue(policy.check(null, null));

		file.delete();

		policy = new StartupPolicy();
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		assertTrue(policy.check(null, null));
		policy.reset();
		assertTrue(policy.check(null, null));
	}

}
