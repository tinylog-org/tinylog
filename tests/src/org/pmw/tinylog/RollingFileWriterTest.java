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

package org.pmw.tinylog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;
import org.pmw.tinylog.policies.SizePolicy;

/**
 * Tests for the rolling file logging writer.
 * 
 * @see org.pmw.tinylog.RollingFileWriter
 */
public class RollingFileWriterTest {

	/**
	 * Test writing.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testWriting() throws IOException {
		File file = File.createTempFile("test", "tmp");
		file.delete();
		file.deleteOnExit();
		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0);
		writer.write(ELoggingLevel.INFO, "Hello\n");
		writer.write(ELoggingLevel.INFO, "World\n");
		writer.close();

		PrintStream defaultPrintStream = System.err;
		SilentOutputStream outputStream = new SilentOutputStream();
		System.setErr(new PrintStream(outputStream));
		writer.write(ELoggingLevel.INFO, "Won't be written\n");
		System.setErr(defaultPrintStream);
		assertTrue(outputStream.isUsed());

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		assertEquals("World", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

		file.delete();
	}

	/**
	 * Test rolling by creating a new instance of the rolling file logging writer.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testRollingByStarting() throws IOException {
		File baseFile = File.createTempFile("test", ".tmp");
		baseFile.deleteOnExit();
		File backupFile1 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + ".0.tmp");
		backupFile1.deleteOnExit();
		File backupFile2 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + ".1.tmp");
		backupFile2.deleteOnExit();
		File backupFile3 = new File(baseFile.getPath().substring(0, baseFile.getPath().length() - 4) + ".2.tmp");
		backupFile3.deleteOnExit();

		RollingFileWriter writer = new RollingFileWriter(baseFile.getAbsolutePath(), 2);
		writer.write(ELoggingLevel.INFO, "1");
		writer.close();

		assertTrue(baseFile.exists());
		assertTrue(backupFile1.exists());
		assertFalse(backupFile2.exists());

		writer = new RollingFileWriter(baseFile.getAbsolutePath(), 2);
		writer.write(ELoggingLevel.INFO, "2");
		writer.close();

		assertTrue(baseFile.exists());
		assertTrue(backupFile1.exists());
		assertTrue(backupFile2.exists());
		assertFalse(backupFile3.exists());

		writer = new RollingFileWriter(baseFile.getAbsolutePath(), 2);
		writer.write(ELoggingLevel.INFO, "3");
		writer.close();

		assertTrue(baseFile.exists());
		assertTrue(backupFile1.exists());
		assertTrue(backupFile2.exists());
		assertFalse(backupFile3.exists());

		BufferedReader reader = new BufferedReader(new FileReader(baseFile));
		assertEquals("3", reader.readLine());
		reader.close();

		reader = new BufferedReader(new FileReader(backupFile1));
		assertEquals("2", reader.readLine());
		reader.close();

		reader = new BufferedReader(new FileReader(backupFile2));
		assertEquals("1", reader.readLine());
		reader.close();

		baseFile.delete();
		backupFile1.delete();
		backupFile2.delete();
		backupFile3.delete();
	}

	/**
	 * Test rolling after gaining maximum file size.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testRollingByFileSize() throws IOException {
		File baseFile = File.createTempFile("test", "");
		baseFile.deleteOnExit();
		File backupFile = new File(baseFile.getPath() + ".0");
		backupFile.deleteOnExit();

		RollingFileWriter writer = new RollingFileWriter(baseFile.getAbsolutePath(), 1, new SizePolicy(3));
		backupFile.delete();
		writer.write(ELoggingLevel.INFO, "1");
		writer.write(ELoggingLevel.INFO, "2");
		writer.write(ELoggingLevel.INFO, "3");
		writer.write(ELoggingLevel.INFO, "4");
		writer.close();

		BufferedReader reader = new BufferedReader(new FileReader(baseFile));
		assertEquals("4", reader.readLine());
		reader.close();

		reader = new BufferedReader(new FileReader(backupFile));
		assertEquals("123", reader.readLine());
		reader.close();

		baseFile.delete();
		backupFile.delete();
	}

}
