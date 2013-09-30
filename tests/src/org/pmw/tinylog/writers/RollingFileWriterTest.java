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

package org.pmw.tinylog.writers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.StringListOutputStream;

/**
 * Tests for the rolling file logging writer.
 * 
 * @see RollingFileWriter
 */
public class RollingFileWriterTest extends AbstractTest {

	/**
	 * Test simple writing (non-existing log file and neither policies nor a labeller).
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testSimpleWriting() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		file.delete();

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0);
		writer.init();
		writer.write(LoggingLevel.INFO, "Hello\n");
		writer.write(LoggingLevel.INFO, "World\n");
		writer.close();

		StringListOutputStream errorStream = getSystemErrorStream();
		assertFalse(errorStream.hasLines());
		writer.write(LoggingLevel.INFO, "Won't be written\n");
		assertTrue(errorStream.hasLines());
		errorStream.clear();

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		assertEquals("World", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

		file.delete();
	}

	/**
	 * Test rolling while writing.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRollingWhileWriting() throws IOException {
		File file = FileHelper.createTemporaryFile(null, "12");
		File backup = new File(file.getAbsolutePath() + ".0");

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 100, new SizePolicy(3));
		writer.init();
		writer.write(LoggingLevel.INFO, "3");
		writer.write(LoggingLevel.INFO, "4");
		writer.write(LoggingLevel.INFO, "5");
		writer.close();

		assertEquals("45", FileHelper.read(file));
		assertEquals("123", FileHelper.read(backup));

		file.delete();
		backup.delete();
	}

	/**
	 * Test rolling while opening.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRollingWhileOpening() throws IOException {
		File file = FileHelper.createTemporaryFile(null, "123");
		File backup = new File(file.getAbsolutePath() + ".0");

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 100, new StartupPolicy());
		writer.init();
		writer.close();

		assertEquals("", FileHelper.read(file));
		assertEquals("123", FileHelper.read(backup));

		file.delete();
		backup.delete();
	}

}
