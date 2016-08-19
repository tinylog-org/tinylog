/*
 * Copyright 2014 Martin Winandy
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

package org.pmw.tinylog;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests the environment helper.
 *
 * @see EnvironmentHelper
 */
public class EnvironmentHelperTest extends AbstractCoreTest {

	/**
	 * Test if the class is a valid utility class.
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(EnvironmentHelper.class);
	}

	/**
	 * Test if an Android Runtime will be detected on Android.
	 */
	@Test
	public final void testDetectingAndroidRuntime() {
		String runtime = System.getProperty("java.runtime.name");
		try {
			System.setProperty("java.runtime.name", "Android Runtime");
			assertTrue(EnvironmentHelper.isAndroid());

			System.setProperty("java.runtime.name", "android runtime");
			assertTrue(EnvironmentHelper.isAndroid());
		} finally {
			System.setProperty("java.runtime.name", runtime);
		}
	}

	/**
	 * Test if an Android Runtime will be not detected on common Java runtime environments.
	 */
	@Test
	public final void testDetectingCommonJavaRuntime() {
		String runtime = System.getProperty("java.runtime.name");
		try {
			System.setProperty("java.runtime.name", "Java(TM) SE Runtime Environment");
			assertFalse(EnvironmentHelper.isAndroid());

			System.setProperty("java.runtime.name", "OpenJDK Runtime Environment");
			assertFalse(EnvironmentHelper.isAndroid());
		} finally {
			System.setProperty("java.runtime.name", runtime);
		}
	}

	/**
	 * Test if common Windows versions will be detected.
	 */
	@Test
	public final void testDetectingWindows() {
		String os = System.getProperty("os.name");
		try {
			System.setProperty("os.name", "Windows NT");
			assertTrue(EnvironmentHelper.isWindows());

			System.setProperty("os.name", "Windows 7");
			assertTrue(EnvironmentHelper.isWindows());

			System.setProperty("os.name", "Windows 8.1");
			assertTrue(EnvironmentHelper.isWindows());
		} finally {
			System.setProperty("os.name", os);
		}
	}

	/**
	 * Test if non-Windows operating systems will be detected.
	 */
	@Test
	public final void testDetectingNonWindowsSystems() {
		String os = System.getProperty("os.name");
		try {
			System.setProperty("os.name", "Linux");
			assertFalse(EnvironmentHelper.isWindows());

			System.setProperty("os.name", "Mac OS X");
			assertFalse(EnvironmentHelper.isWindows());

			System.setProperty("os.name", "SunOS");
			assertFalse(EnvironmentHelper.isWindows());
		} finally {
			System.setProperty("os.name", os);
		}
	}

	/**
	 * Test if the line separator is equals to the line separator of the OS.
	 */
	@Test
	public final void testNewLine() {
		assertEquals(System.getProperty("line.separator"), EnvironmentHelper.getNewLine());
	}

	/**
	 * Test making nonexistent directories.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testMakingDirectories() throws IOException {
		TemporaryFolder folder = new TemporaryFolder();
		folder.create();

		try {
			File parentFolder = new File(folder.getRoot(), "parent");
			File subFolder = new File(parentFolder, "sub");
			File testFile = new File(subFolder, "test.log");

			assertFalse(parentFolder.exists());
			assertFalse(subFolder.exists());
			assertFalse(testFile.exists());

			EnvironmentHelper.makeDirectories(testFile);

			assertTrue(parentFolder.exists());
			assertTrue(subFolder.exists());
			assertFalse(testFile.exists());
		} finally {
			folder.delete();
		}
	}

}
