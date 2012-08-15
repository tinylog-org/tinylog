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
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Test;

/**
 * Tests for size policy.
 * 
 * @see SizePolicy
 */
public class SizePolicyTest {

	/**
	 * Test rolling with inexistent log file.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testRollingWithInexistingLogFile() throws IOException {
		File file = File.createTempFile("test", ".tmp");
		file.delete();

		IPolicy policy = new SizePolicy(10);
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, "0123456789"));
		assertFalse(policy.check(null, "0"));

		policy.reset();
		assertTrue(policy.check(null, "0"));
		assertTrue(policy.check(null, "123456789"));
		assertFalse(policy.check(null, "0"));
	}

	/**
	 * Test rolling with existent log file.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testRollingWithExistingLogFile() throws IOException {
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		FileWriter writer = new FileWriter(file);
		writer.write("01234");
		writer.close();

		IPolicy policy = new SizePolicy(10);
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, "56789"));
		assertFalse(policy.check(null, "0"));

		file.delete();
	}

	/**
	 * Test String parameter.
	 */
	@Test
	public final void testStringParameter() {
		SizePolicy policy = new SizePolicy("1024");
		assertEquals(1024L, getMaxSize(policy));

		policy = new SizePolicy("32KB");
		assertEquals(32L * 1024L, getMaxSize(policy));

		policy = new SizePolicy("2 MB");
		assertEquals(2L * 1024L * 1024L, getMaxSize(policy));

		policy = new SizePolicy("4GB");
		assertEquals(4L * 1024L * 1024L * 1024L, getMaxSize(policy));

		try {
			policy = new SizePolicy("-1");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException ex) {
			assertEquals(IllegalArgumentException.class, ex.getClass());
		}

		try {
			policy = new SizePolicy("abc");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException ex) {
			assertEquals(IllegalArgumentException.class, ex.getClass());
		}
	}

	/**
	 * Test exception for maxSize = 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testForNull() {
		new SizePolicy(0);
	}

	/**
	 * Test exception for maxSize = -1.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testForMinus() {
		new SizePolicy(-1);
	}

	/**
	 * Get the max size field of a {@link SizePolicy}.
	 * 
	 * @param sizePolicy
	 *            Policy to get the max size field for
	 * @return Value of the max size field
	 */
	protected static final long getMaxSize(final SizePolicy sizePolicy) {
		try {
			Field field = SizePolicy.class.getDeclaredField("maxSize");
			field.setAccessible(true);
			return field.getLong(sizePolicy);
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

}
