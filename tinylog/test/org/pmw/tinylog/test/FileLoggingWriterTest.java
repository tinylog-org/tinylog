package org.pmw.tinylog.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.pmw.tinylog.ELoggingLevel;
import org.pmw.tinylog.FileLoggingWriter;

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
		FileLoggingWriter writer = new FileLoggingWriter(file);
		writer.write(ELoggingLevel.INFO, "Hello\n");
		writer.write(ELoggingLevel.INFO, "World\n");
		writer.close();
		writer.write(ELoggingLevel.INFO, "Won't be written\n");

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		assertEquals("World", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

		file.delete();
	}
}
