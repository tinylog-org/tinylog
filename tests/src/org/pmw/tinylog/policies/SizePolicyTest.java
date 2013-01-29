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

import java.io.File;
import java.io.IOException;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.util.FileHelper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for size policy.
 * 
 * @see SizePolicy
 */
public class SizePolicyTest extends AbstractTest {

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

		Policy policy = new SizePolicy(10);
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
	 *             Test failed
	 */
	@Test
	public final void testRollingWithExistingLogFile() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		FileHelper.write(file, "01234");

		Policy policy = new SizePolicy(10);
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, "56789"));
		assertFalse(policy.check(null, "0"));

		file.delete();
	}

	/**
	 * Test exception for maxSize = 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testForZero() {
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
	 * Test String parameter for value in bytes.
	 */
	@Test
	public final void testStringParameterForBytes() {
		Policy policy = new SizePolicy("1024");
		assertTrue(policy.check(null, createString(512)));
		assertTrue(policy.check(null, createString(512)));
		assertFalse(policy.check(null, createString(1)));
	}

	/**
	 * Test String parameter for value in KB.
	 */
	@Test
	public final void testStringParameterForKB() {
		String filler = createString(1024); // 1 KB
		Policy policy = new SizePolicy("32KB");
		for (int i = 0; i < 32; ++i) {
			assertTrue(policy.check(null, filler));
		}
		assertFalse(policy.check(null, createString(1)));
	}

	/**
	 * Test String parameter for value in MB.
	 */
	@Test
	public final void testStringParameterForMB() {
		String filler = createString(1024); // 1 KB
		Policy policy = new SizePolicy("2 MB");
		for (int i = 0; i < 2048; ++i) {
			assertTrue(policy.check(null, filler));
		}
		assertFalse(policy.check(null, createString(1)));
	}

	/**
	 * Test String parameter for value in GB.
	 */
	@Test
	public final void testStringParameterForGB() {
		final int size = 1024 * 1024; // 1 MB

		new MockUp<String>() { // Speed up test

			private final byte[] buffer = new byte[size]; // 1 MB

			@SuppressWarnings("unused")
			@Mock
			public byte[] getBytes() {
				return buffer;
			}

		};

		String filler = createString(size); // 1 MB
		Policy policy = new SizePolicy("4GB");
		for (int i = 0; i < 4 * 1024; ++i) {
			assertTrue(policy.check(null, filler));
		}
		assertFalse(policy.check(null, createString(1)));
	}

	/**
	 * Test exception for "-1".
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testStringParameterForMinueOne() {
		new SizePolicy("-1");
	}

	/**
	 * Test exception for "abc".
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testStringParameterForInvalidString() {
		new SizePolicy("abc");
	}

	private static String createString(final int size) {
		StringBuilder builder = new StringBuilder(size);
		for (int i = 0; i < size; ++i) {
			builder.append(i % 10);
		}
		return builder.toString();
	}

}
