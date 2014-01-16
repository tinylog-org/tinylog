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

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.mocks.SystemTimeMock;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.StringListOutputStream;

/**
 * Tests for timestamp labeller.
 * 
 * @see TimestampLabeller
 */
public class TimestampLabellerTest extends AbstractLabellerTest {

	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH-mm-ss";

	private SystemTimeMock systemTimeMock;

	/**
	 * Set time zone to UTC and set up the mock for {@link System} (to control time).
	 */
	@Before
	public final void init() {
		systemTimeMock = new SystemTimeMock();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * Tear down mock and reset time zone.
	 */
	@After
	public final void dispose() {
		systemTimeMock.tearDown();
		TimeZone.setDefault(null);
	}

	/**
	 * Test labelling for log file with file extension.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testLabellingWithFileExtension() throws IOException {
		File baseFile = FileHelper.createTemporaryFile("tmp");
		baseFile.delete();

		systemTimeMock.setCurrentTimeMillis(0L);
		File targetFile1 = getBackupFile(baseFile, "tmp", formatCurrentTime());

		TimestampLabeller labeller = new TimestampLabeller(TIMESTAMP_FORMAT);
		assertEquals(targetFile1, labeller.getLogFile(baseFile));
		targetFile1.createNewFile();
		targetFile1.setLastModified(systemTimeMock.currentTimeMillis());

		systemTimeMock.setCurrentTimeMillis(1000L);
		File targetFile2 = getBackupFile(baseFile, "tmp", formatCurrentTime());

		assertEquals(targetFile2, labeller.roll(targetFile1, 2));
		targetFile2.createNewFile();
		targetFile2.setLastModified(systemTimeMock.currentTimeMillis());
		assertTrue(targetFile1.exists());
		assertTrue(targetFile2.exists());

		systemTimeMock.setCurrentTimeMillis(2000L);
		File targetFile3 = getBackupFile(baseFile, "tmp", formatCurrentTime());

		assertEquals(targetFile3, labeller.roll(targetFile2, 2));
		targetFile3.createNewFile();
		targetFile3.setLastModified(systemTimeMock.currentTimeMillis());
		assertTrue(targetFile1.exists());
		assertTrue(targetFile2.exists());
		assertTrue(targetFile3.exists());

		systemTimeMock.setCurrentTimeMillis(3000L);
		File targetFile4 = getBackupFile(baseFile, "tmp", formatCurrentTime());

		assertEquals(targetFile4, labeller.roll(targetFile3, 2));
		targetFile4.createNewFile();
		targetFile4.setLastModified(systemTimeMock.currentTimeMillis());
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
	 *             Test failed
	 */
	@Test
	public final void testLabellingWithoutFileExtension() throws IOException {
		File baseFile = FileHelper.createTemporaryFile(null);
		baseFile.delete();

		systemTimeMock.setCurrentTimeMillis(0L);
		File targetFile1 = getBackupFile(baseFile, null, formatCurrentTime());

		TimestampLabeller labeller = new TimestampLabeller(TIMESTAMP_FORMAT);
		assertEquals(targetFile1, labeller.getLogFile(baseFile));
		targetFile1.createNewFile();
		targetFile1.setLastModified(systemTimeMock.currentTimeMillis());

		systemTimeMock.setCurrentTimeMillis(1000L);
		File targetFile2 = getBackupFile(baseFile, null, formatCurrentTime());

		assertEquals(targetFile2, labeller.roll(targetFile1, 1));
		targetFile2.createNewFile();
		targetFile2.setLastModified(systemTimeMock.currentTimeMillis());
		assertTrue(targetFile1.exists());
		assertTrue(targetFile2.exists());

		systemTimeMock.setCurrentTimeMillis(2000L);
		File targetFile3 = getBackupFile(baseFile, null, formatCurrentTime());

		assertEquals(targetFile3, labeller.roll(targetFile2, 1));
		targetFile3.createNewFile();
		targetFile3.setLastModified(systemTimeMock.currentTimeMillis());
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
	 *             Test failed
	 */
	@Test
	public final void testLabellingWithoutBackups() throws IOException {
		File baseFile = File.createTempFile("test", ".tmp");
		baseFile.delete();

		systemTimeMock.setCurrentTimeMillis(0L);
		File targetFile1 = getBackupFile(baseFile, "tmp", formatCurrentTime());
		targetFile1.deleteOnExit();

		TimestampLabeller labeller = new TimestampLabeller();
		assertEquals(targetFile1, labeller.getLogFile(baseFile));
		targetFile1.createNewFile();
		targetFile1.setLastModified(systemTimeMock.currentTimeMillis());

		systemTimeMock.setCurrentTimeMillis(1000L);
		File targetFile2 = getBackupFile(baseFile, "tmp", formatCurrentTime());
		targetFile2.setLastModified(systemTimeMock.currentTimeMillis());

		assertTrue(targetFile1.exists());
		assertEquals(targetFile2, labeller.roll(targetFile1, 0));
		assertFalse(targetFile1.exists());

		baseFile.delete();
		targetFile1.delete();
		targetFile2.delete();
	}

	/**
	 * Test deleting if backup file is in use.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testDeletingOfBackupFileFails() throws IOException {
		File baseFile = FileHelper.createTemporaryFile("tmp");

		File backupFile = getBackupFile(baseFile, "tmp", formatCurrentTime());
		backupFile.createNewFile();
		FileInputStream stream = new FileInputStream(backupFile);

		TimestampLabeller labeller = new TimestampLabeller();
		File currentFile = labeller.getLogFile(baseFile);

		StringListOutputStream errorStream = getSystemErrorStream();
		assertFalse(errorStream.hasLines());
		labeller.roll(currentFile, 0);
		assertTrue(errorStream.hasLines());
		assertThat(errorStream.nextLine(), anyOf(containsString("delete"), containsString("remove")));
		errorStream.clear();

		stream.close();
		backupFile.delete();
	}

	private String formatCurrentTime() {
		return new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ROOT).format(new Date());
	}

}
