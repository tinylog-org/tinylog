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

package org.pmw.tinylog.labellers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import mockit.Mockit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.util.MockSystem;

/**
 * Tests for timestamp labeller.
 * 
 * @see TimestampLabeller
 */
public class TimestampLabellerTest {

	private MockSystem mockSystem;

	/**
	 * Set up the mock for {@link System}.
	 */
	@Before
	public final void init() {
		mockSystem = new MockSystem();
		Mockit.setUpMocks(mockSystem);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * Tear down the mock for {@link System}.
	 */
	@After
	public final void dispose() {
		TimeZone.setDefault(null);
		Mockit.tearDownMocks(System.class);
	}

	/**
	 * Test labelling for log file with file extension.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testLabellingWithFileExtension() throws IOException {
		File baseFile = File.createTempFile("test", ".tmp");
		baseFile.delete();

		mockSystem.setCurrentTimeMillis(0L);
		File targetFile1 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + "." + formatCurrentTime() + ".tmp");
		targetFile1.deleteOnExit();

		Labeller labeller = new TimestampLabeller("yyyy-MM-dd HH-mm-ss");
		assertEquals(targetFile1, labeller.getLogFile(baseFile));
		targetFile1.createNewFile();
		targetFile1.setLastModified(mockSystem.currentTimeMillis());

		mockSystem.setCurrentTimeMillis(1000L);
		File targetFile2 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + "." + formatCurrentTime() + ".tmp");
		targetFile2.deleteOnExit();

		assertEquals(targetFile2, labeller.roll(targetFile1, 2));
		targetFile2.createNewFile();
		targetFile2.setLastModified(mockSystem.currentTimeMillis());
		assertTrue(targetFile1.exists());
		assertTrue(targetFile2.exists());

		mockSystem.setCurrentTimeMillis(2000L);
		File targetFile3 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + "." + formatCurrentTime() + ".tmp");
		targetFile3.deleteOnExit();

		assertEquals(targetFile3, labeller.roll(targetFile2, 2));
		targetFile3.createNewFile();
		targetFile3.setLastModified(mockSystem.currentTimeMillis());
		assertTrue(targetFile1.exists());
		assertTrue(targetFile2.exists());
		assertTrue(targetFile3.exists());

		mockSystem.setCurrentTimeMillis(3000L);
		File targetFile4 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + "." + formatCurrentTime() + ".tmp");
		targetFile4.deleteOnExit();

		assertEquals(targetFile4, labeller.roll(targetFile3, 2));
		targetFile4.createNewFile();
		targetFile4.setLastModified(mockSystem.currentTimeMillis());
		assertFalse(targetFile1.exists());
		assertTrue(targetFile2.exists());
		assertTrue(targetFile3.exists());
		assertTrue(targetFile4.exists());

		baseFile.delete();
		targetFile1.delete();
		targetFile2.delete();
		targetFile3.delete();
		targetFile4.delete();
	}

	/**
	 * Test labelling for log file without file extension.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testLabellingWithoutFileExtension() throws IOException {
		File baseFile = File.createTempFile("test", "");
		baseFile.delete();

		mockSystem.setCurrentTimeMillis(0L);
		File targetFile1 = new File(baseFile.getPath() + "." + formatCurrentTime());
		targetFile1.deleteOnExit();

		Labeller labeller = new TimestampLabeller("yyyy-MM-dd HH-mm-ss");
		assertEquals(targetFile1, labeller.getLogFile(baseFile));
		targetFile1.createNewFile();
		targetFile1.setLastModified(mockSystem.currentTimeMillis());

		mockSystem.setCurrentTimeMillis(1000L);
		File targetFile2 = new File(baseFile.getPath() + "." + formatCurrentTime());
		targetFile2.deleteOnExit();

		assertEquals(targetFile2, labeller.roll(targetFile1, 1));
		targetFile2.createNewFile();
		targetFile2.setLastModified(mockSystem.currentTimeMillis());
		assertTrue(targetFile1.exists());
		assertTrue(targetFile2.exists());

		mockSystem.setCurrentTimeMillis(2000L);
		File targetFile3 = new File(baseFile.getPath() + "." + formatCurrentTime());
		targetFile3.deleteOnExit();

		assertEquals(targetFile3, labeller.roll(targetFile2, 1));
		targetFile3.createNewFile();
		targetFile3.setLastModified(mockSystem.currentTimeMillis());
		assertFalse(targetFile1.exists());
		assertTrue(targetFile2.exists());
		assertTrue(targetFile3.exists());

		baseFile.delete();
		targetFile1.delete();
		targetFile2.delete();
		targetFile3.delete();
	}

	/**
	 * Test labelling without storing backups.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testLabellingWithoutBackups() throws IOException {
		mockSystem.setCurrentTimeMillis(0L);

		File baseFile = File.createTempFile("test", ".tmp");
		baseFile.delete();
		File targetFile1 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + "." + formatCurrentTime() + ".tmp");
		targetFile1.deleteOnExit();

		Labeller labeller = new TimestampLabeller();
		assertEquals(targetFile1, labeller.getLogFile(baseFile));
		targetFile1.createNewFile();
		targetFile1.setLastModified(mockSystem.currentTimeMillis());

		mockSystem.setCurrentTimeMillis(1000L);
		File targetFile2 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + "." + formatCurrentTime() + ".tmp");
		targetFile2.deleteOnExit();
		targetFile2.setLastModified(mockSystem.currentTimeMillis());

		assertTrue(targetFile1.exists());
		assertEquals(targetFile2, labeller.roll(targetFile1, 0));
		assertFalse(targetFile1.exists());

		baseFile.delete();
		targetFile1.delete();
		targetFile2.delete();
	}

	private String formatCurrentTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.ROOT).format(new Date());
	}
}
