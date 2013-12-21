/*
 * Copyright 2013 Martin Winandy
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
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import mockit.NonStrictExpectations;

import org.junit.Test;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.ProcessIdHelper;

/**
 * Tests for process ID labeller.
 * 
 * @see ProcessIdLabeller
 */
public class ProcessIdLabellerTest extends AbstractLabellerTest {

	/**
	 * Test if the labeller extract the right process ID (pid).
	 */
	@Test
	public final void testCurrentProcessId() {
		assertEquals(ProcessIdHelper.getProcessId(), new ProcessIdLabeller().getProcessId());
	}

	/**
	 * Test if the labeller extract the process ID (pid) if the name of {@link RuntimeMXBean} is without host name.
	 */
	@Test
	public final void testProcessIdOnly() {
		new NonStrictExpectations(ManagementFactory.getRuntimeMXBean()) {

			{
				ManagementFactory.getRuntimeMXBean().getName();
				returns("1234");
			}

		};

		assertEquals("1234", new ProcessIdLabeller().getProcessId());
	}

	/**
	 * Test if the labeller extract the process ID (pid) if the name of {@link RuntimeMXBean} includes the host name.
	 */
	@Test
	public final void testProcessIdWithHost() {
		new NonStrictExpectations(ManagementFactory.getRuntimeMXBean()) {

			{
				ManagementFactory.getRuntimeMXBean().getName();
				returns("1234@localhost");
			}

		};

		assertEquals("1234", new ProcessIdLabeller().getProcessId());
	}

	/**
	 * Test labelling for log file with file extension.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testLabellingWithFileExtension() throws IOException {
		File baseFile = FileHelper.createTemporaryFile("tmp");
		baseFile.delete();
		File realFile = getBackupFile(baseFile, "tmp", ProcessIdHelper.getProcessId());

		Labeller labeller = new ProcessIdLabeller();

		assertEquals(realFile, labeller.getLogFile(baseFile));

		assertEquals(realFile, labeller.roll(realFile, 0));

		realFile.createNewFile();
		assertEquals(realFile, labeller.roll(realFile, 0));
		assertFalse(realFile.exists());

		baseFile.delete();
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
		baseFile.delete();
		File realFile = getBackupFile(baseFile, null, ProcessIdHelper.getProcessId());

		Labeller labeller = new ProcessIdLabeller();

		assertEquals(realFile, labeller.getLogFile(baseFile));

		assertEquals(realFile, labeller.roll(realFile, 0));

		realFile.createNewFile();
		assertEquals(realFile, labeller.roll(realFile, 0));
		assertFalse(realFile.exists());

		baseFile.delete();
	}

	/**
	 * Test labelling without storing backups.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testLabellingWithoutBackups() throws IOException {
		File baseFile = File.createTempFile("test", ".tmp");
		baseFile.delete();

		File targetFile = getBackupFile(baseFile, "tmp", ProcessIdHelper.getProcessId());
		targetFile.createNewFile();

		Labeller labeller = new ProcessIdLabeller();
		assertEquals(targetFile, labeller.getLogFile(baseFile));
		assertTrue(targetFile.exists());
		assertEquals(targetFile, labeller.roll(targetFile, 0));
		assertFalse(targetFile.exists());
	}

	/**
	 * Test if labeller deletes the right old files.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testDeletingOldFiles() throws IOException {
		File baseFile = File.createTempFile("test", ".tmp");
		baseFile.delete();

		File backupFile1 = getBackupFile(baseFile, "tmp", "$OLD1$");
		backupFile1.createNewFile();
		backupFile1.setLastModified(1L);
		File backupFile2 = getBackupFile(baseFile, "tmp", "$OLD2$");
		backupFile2.createNewFile();
		backupFile2.setLastModified(2L);
		File backupFile3 = getBackupFile(baseFile, "tmp", "$OLD3$");
		backupFile3.createNewFile();
		backupFile3.setLastModified(0L);

		Labeller labeller = new ProcessIdLabeller();
		labeller.roll(labeller.getLogFile(baseFile), 1);

		assertFalse(backupFile1.exists());
		assertTrue(backupFile2.exists());
		assertFalse(backupFile3.exists());

		backupFile2.delete();
	}

}
