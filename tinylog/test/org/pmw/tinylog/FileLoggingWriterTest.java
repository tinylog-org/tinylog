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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Tests for the file logging writer.
 * 
 * @see org.pmw.tinylog.FileLoggingWriter
 */
public class FileLoggingWriterTest {

	/**
	 * Test writing.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testWriting() throws IOException {
		File file = File.createTempFile("test", "tmp");
		file.deleteOnExit();
		FileLoggingWriter writer = new FileLoggingWriter(file.getAbsolutePath());
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

}
