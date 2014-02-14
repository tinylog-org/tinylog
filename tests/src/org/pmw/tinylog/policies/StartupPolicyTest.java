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
 * Tests for startup policy.
 * 
 * @see StartupPolicy
 */
public class StartupPolicyTest extends AbstractPolicyTest {

	/**
	 * Test rolling.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRolling() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		Policy policy = new StartupPolicy();
		policy.init(null);
		assertFalse(policy.check(file));
		assertTrue(policy.check((String) null));
		assertTrue(policy.check((String) null));
		policy.reset();
		assertTrue(policy.check((String) null));

		file.delete();

		policy = new StartupPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
		assertTrue(policy.check((String) null));
		assertTrue(policy.check((String) null));
		policy.reset();
		assertTrue(policy.check((String) null));
	}

	/**
	 * Test reading startup policy from properties.
	 */
	@Test
	public final void testFromProperties() {
		Policy policy = createFromProperties("startup");
		assertNotNull(policy);
		assertEquals(StartupPolicy.class, policy.getClass());
	}

}
