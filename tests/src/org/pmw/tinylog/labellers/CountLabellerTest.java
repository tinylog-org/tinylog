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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

/**
 * Tests for count labeller.
 * 
 * @see CountLabeller
 */
public class CountLabellerTest {

	/**
	 * Test labelling for log file with file extension.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testLabellingWithFileExtension() throws IOException {
		File baseFile = File.createTempFile("test", ".tmp");
		baseFile.deleteOnExit();
		File backupFile1 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + ".0.tmp");
		backupFile1.deleteOnExit();
		File backupFile2 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + ".1.tmp");
		backupFile2.deleteOnExit();
		File backupFile3 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + ".2.tmp");
		backupFile3.deleteOnExit();

		Labeller labeller = new CountLabeller();
		assertSame(baseFile, labeller.getLogFile(baseFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(baseFile));
		writer.write("1");
		writer.close();

		assertSame(baseFile, labeller.roll(baseFile, 2));
		writer = new BufferedWriter(new FileWriter(baseFile));
		writer.write("2");
		writer.close();
		assertTrue(backupFile1.exists());
		assertFalse(backupFile2.exists());
		assertFalse(backupFile3.exists());

		assertSame(baseFile, labeller.roll(baseFile, 2));
		writer = new BufferedWriter(new FileWriter(baseFile));
		writer.write("3");
		writer.close();
		assertTrue(backupFile1.exists());
		assertTrue(backupFile2.exists());
		assertFalse(backupFile3.exists());

		assertSame(baseFile, labeller.roll(baseFile, 2));
		assertFalse(baseFile.exists());
		assertTrue(backupFile1.exists());
		assertTrue(backupFile2.exists());
		assertFalse(backupFile3.exists());

		BufferedReader reader = new BufferedReader(new FileReader(backupFile1));
		assertEquals("3", reader.readLine());
		reader.close();

		reader = new BufferedReader(new FileReader(backupFile2));
		assertEquals("2", reader.readLine());
		reader.close();

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
		File baseFile = File.createTempFile("test", "");
		baseFile.deleteOnExit();
		File backupFile1 = new File(baseFile.getPath() + ".0");
		backupFile1.deleteOnExit();
		File backupFile2 = new File(baseFile.getPath() + ".1");
		backupFile2.deleteOnExit();

		Labeller labeller = new CountLabeller();
		assertSame(baseFile, labeller.getLogFile(baseFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(baseFile));
		writer.write("1");
		writer.close();

		assertSame(baseFile, labeller.roll(baseFile, 1));
		writer = new BufferedWriter(new FileWriter(baseFile));
		writer.write("2");
		writer.close();
		assertTrue(backupFile1.exists());
		assertFalse(backupFile2.exists());

		assertSame(baseFile, labeller.roll(baseFile, 1));
		assertTrue(backupFile1.exists());
		assertFalse(backupFile2.exists());

		BufferedReader reader = new BufferedReader(new FileReader(backupFile1));
		assertEquals("2", reader.readLine());
		reader.close();

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
		File baseFile = File.createTempFile("test", ".tmp");
		baseFile.deleteOnExit();
		File backupFile = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + ".0.tmp");
		backupFile.deleteOnExit();

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
