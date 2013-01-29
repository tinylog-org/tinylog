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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.pmw.tinylog.util.FileHelper;

/**
 * Tests for count labeller.
 * 
 * @see CountLabeller
 */
public class CountLabellerTest extends AbstractLabellerTest {

	/**
	 * Test labelling for log file with file extension.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testLabellingWithFileExtension() throws IOException {
		File baseFile = FileHelper.createTemporaryFile("tmp");
		File backupFile1 = getBackupFile(baseFile, "tmp", "0");
		File backupFile2 = getBackupFile(baseFile, "tmp", "1");
		File backupFile3 = getBackupFile(baseFile, "tmp", "2");

		Labeller labeller = new CountLabeller();
		assertSame(baseFile, labeller.getLogFile(baseFile));

		FileHelper.write(baseFile, "1");
		assertSame(baseFile, labeller.roll(baseFile, 2));
		assertFalse(baseFile.exists());
		assertTrue(backupFile1.exists());
		assertEquals("1", FileHelper.read(backupFile1));
		assertFalse(backupFile2.exists());
		assertFalse(backupFile3.exists());

		FileHelper.write(baseFile, "2");
		assertSame(baseFile, labeller.roll(baseFile, 2));
		assertFalse(baseFile.exists());
		assertTrue(backupFile1.exists());
		assertEquals("2", FileHelper.read(backupFile1));
		assertTrue(backupFile2.exists());
		assertEquals("1", FileHelper.read(backupFile2));
		assertFalse(backupFile3.exists());

		FileHelper.write(baseFile, "3");
		assertSame(baseFile, labeller.roll(baseFile, 2));
		assertFalse(baseFile.exists());
		assertTrue(backupFile1.exists());
		assertEquals("3", FileHelper.read(backupFile1));
		assertTrue(backupFile2.exists());
		assertEquals("2", FileHelper.read(backupFile2));
		assertFalse(backupFile3.exists());

		baseFile.delete();
		backupFile1.delete();
		backupFile2.delete();
	}

	/**
	 * Test labelling for log file without file extension.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testLabellingWithoutFileExtension() throws IOException {
		File baseFile = FileHelper.createTemporaryFile(null);
		File backupFile1 = getBackupFile(baseFile, null, "0");
		File backupFile2 = getBackupFile(baseFile, null, "1");

		Labeller labeller = new CountLabeller();
		assertSame(baseFile, labeller.getLogFile(baseFile));

		FileHelper.write(baseFile, "1");
		assertSame(baseFile, labeller.roll(baseFile, 1));
		assertTrue(backupFile1.exists());
		assertEquals("1", FileHelper.read(backupFile1));
		assertFalse(backupFile2.exists());

		FileHelper.write(baseFile, "2");
		assertSame(baseFile, labeller.roll(baseFile, 1));
		assertTrue(backupFile1.exists());
		assertEquals("2", FileHelper.read(backupFile1));
		assertFalse(backupFile2.exists());

		backupFile1.delete();
	}

	/**
	 * Test labelling without storing backups.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testLabellingWithoutBackups() throws IOException {
		File baseFile = FileHelper.createTemporaryFile("tmp");
		File backupFile = getBackupFile(baseFile, "tmp", "0");

		Labeller labeller = new CountLabeller();
		assertSame(baseFile, labeller.getLogFile(baseFile));
		baseFile.createNewFile();
		assertFalse(backupFile.exists());

		assertSame(baseFile, labeller.roll(baseFile, 0));
		baseFile.createNewFile();
		assertFalse(backupFile.exists());

		baseFile.delete();
	}

}
