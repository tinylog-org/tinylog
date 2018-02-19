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

package org.pmw.tinylog.labelers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.pmw.tinylog.hamcrest.ClassMatchers.type;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;
import org.pmw.tinylog.util.ConfigurationCreator;
import org.pmw.tinylog.util.FileHelper;

/**
 * Tests for count labeler.
 *
 * @see CountLabeler
 */
public class CountLabelerTest extends AbstractLabelerTest {

	/**
	 * Test labeling for log file with file extension.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testLabelingWithFileExtension() throws IOException {
		File baseFile = FileHelper.createTemporaryFile("tmp");
		File backupFile1 = getBackupFile(baseFile, "tmp", "0");
		File backupFile2 = getBackupFile(baseFile, "tmp", "1");
		File backupFile3 = getBackupFile(baseFile, "tmp", "2");

		CountLabeler labeler = new CountLabeler();
		labeler.init(ConfigurationCreator.getDummyConfiguration());
		assertSame(baseFile, labeler.getLogFile(baseFile, Integer.MAX_VALUE));

		FileHelper.write(baseFile, "1");
		assertSame(baseFile, labeler.roll(baseFile, 2));
		assertFalse(baseFile.exists());
		assertTrue(backupFile1.exists());
		assertEquals("1", FileHelper.read(backupFile1));
		assertFalse(backupFile2.exists());
		assertFalse(backupFile3.exists());

		FileHelper.write(baseFile, "2");
		assertSame(baseFile, labeler.roll(baseFile, 2));
		assertFalse(baseFile.exists());
		assertTrue(backupFile1.exists());
		assertEquals("2", FileHelper.read(backupFile1));
		assertTrue(backupFile2.exists());
		assertEquals("1", FileHelper.read(backupFile2));
		assertFalse(backupFile3.exists());

		FileHelper.write(baseFile, "3");
		assertSame(baseFile, labeler.roll(baseFile, 2));
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
	 * Test labeling for log file without file extension.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testLabelingWithoutFileExtension() throws IOException {
		File baseFile = FileHelper.createTemporaryFile(null);
		File backupFile1 = getBackupFile(baseFile, null, "0");
		File backupFile2 = getBackupFile(baseFile, null, "1");

		CountLabeler labeler = new CountLabeler();
		labeler.init(ConfigurationCreator.getDummyConfiguration());
		assertSame(baseFile, labeler.getLogFile(baseFile, Integer.MAX_VALUE));

		FileHelper.write(baseFile, "1");
		assertSame(baseFile, labeler.roll(baseFile, 1));
		assertTrue(backupFile1.exists());
		assertEquals("1", FileHelper.read(backupFile1));
		assertFalse(backupFile2.exists());

		FileHelper.write(baseFile, "2");
		assertSame(baseFile, labeler.roll(baseFile, 1));
		assertTrue(backupFile1.exists());
		assertEquals("2", FileHelper.read(backupFile1));
		assertFalse(backupFile2.exists());

		backupFile1.delete();
	}

	/**
	 * Test labeling without storing backups.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testLabelingWithoutBackups() throws IOException {
		File baseFile = FileHelper.createTemporaryFile("tmp");
		File backupFile = getBackupFile(baseFile, "tmp", "0");

		CountLabeler labeler = new CountLabeler();
		labeler.init(ConfigurationCreator.getDummyConfiguration());
		assertSame(baseFile, labeler.getLogFile(baseFile, Integer.MAX_VALUE));
		baseFile.createNewFile();
		assertFalse(backupFile.exists());

		assertSame(baseFile, labeler.roll(baseFile, 0));
		baseFile.createNewFile();
		assertFalse(backupFile.exists());

		baseFile.delete();
	}

	/**
	 * Test deleting redundant backups when getting the log file.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testDeletingBackupsAtStartUp() throws IOException {
		File baseFile = FileHelper.createTemporaryFile(null);
		File backupFile1 = getBackupFile(baseFile, null, "0");
		File backupFile2 = getBackupFile(baseFile, null, "1");
		File backupFile3 = getBackupFile(baseFile, null, "2");
		
		backupFile1.createNewFile();
		backupFile2.createNewFile();
		backupFile3.createNewFile();

		CountLabeler labeler = new CountLabeler();
		labeler.init(ConfigurationCreator.getDummyConfiguration());
		assertSame(baseFile, labeler.getLogFile(baseFile, 1));
		assertTrue(backupFile1.exists());
		assertFalse(backupFile2.exists());
		assertFalse(backupFile3.exists());

		backupFile1.delete();
	}

	/**
	 * Test renaming if file is in use.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRenamingOfOpenFile() throws IOException {
		File baseFile = FileHelper.createTemporaryFile("tmp");
		baseFile.createNewFile();
		File backupFile = getBackupFile(baseFile, "tmp", "0");
		backupFile.createNewFile();

		FileInputStream stream = new FileInputStream(backupFile);

		CountLabeler labeler = new CountLabeler();
		labeler.init(ConfigurationCreator.getDummyConfiguration());
		assertSame(baseFile, labeler.getLogFile(baseFile, Integer.MAX_VALUE));
		try {
			labeler.roll(baseFile, 2); // Works or fails depending on OS
		} catch (IOException ex) {
			assertEquals("Failed to rename \"" + backupFile + "\" to \"" + getBackupFile(baseFile, "tmp", "1") + "\"", ex.getMessage());
		}

		stream.close();
		baseFile.delete();
		backupFile.delete();
	}

	/**
	 * Test deleting if file is in use.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testDeletingOfOpenFile() throws IOException {
		File baseFile = FileHelper.createTemporaryFile("tmp");
		baseFile.createNewFile();

		FileInputStream stream = new FileInputStream(baseFile);

		CountLabeler labeler = new CountLabeler();
		labeler.init(ConfigurationCreator.getDummyConfiguration());
		assertSame(baseFile, labeler.getLogFile(baseFile, Integer.MAX_VALUE));

		try {
			labeler.roll(baseFile, 0); // Works or fails depending on OS
		} catch (IOException ex) {
			assertEquals("Failed to delete \"" + baseFile + "\"", ex.getMessage());
		}

		stream.close();
		baseFile.delete();
	}

	/**
	 * Test reading count labeler from properties.
	 */
	@Test
	public final void testFromProperties() {
		Labeler labeler = createFromProperties("count");
		assertThat(labeler, type(CountLabeler.class));
	}

}
