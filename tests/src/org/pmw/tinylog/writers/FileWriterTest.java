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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.LogEntryBuilder;
import org.pmw.tinylog.util.LoopWritingThread;
import org.pmw.tinylog.util.PropertiesBuilder;

/**
 * Tests for the file logging writer.
 * 
 * @see FileWriter
 */
public class FileWriterTest extends AbstractWriterTest {

	/**
	 * Test required log entry values.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRequiredLogEntryValue() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		FileWriter writer = new FileWriter(file.getAbsolutePath());
		Set<LogEntryValue> requiredLogEntryValues = writer.getRequiredLogEntryValues();
		assertThat(requiredLogEntryValues, contains(LogEntryValue.RENDERED_LOG_ENTRY));

		file.delete();
	}

	/**
	 * Test required log entry values.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testFilename() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		FileWriter writer = new FileWriter(file.getAbsolutePath());
		assertEquals(file.getAbsolutePath(), writer.getFilename());

		file.delete();
	}

	/**
	 * Test if unbuffered file writer writes log entries immediately.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testUnbufferedWriting() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		FileWriter writer = new FileWriter(file.getAbsolutePath(), false);
		writer.init(null);
		assertFalse(writer.isBuffered());

		writer.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		reader.close();

		writer.close();
	}

	/**
	 * Test if buffered file writer writes log entries after close.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testBufferedWriting() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		FileWriter writer = new FileWriter(file.getAbsolutePath(), true);
		writer.init(null);
		assertTrue(writer.isBuffered());

		writer.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertNull(reader.readLine());
		reader.close();

		writer.close();

		reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		reader.close();
	}

	/**
	 * Test if buffered file writer writes log entries after flush.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testFlush() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		FileWriter writer = new FileWriter(file.getAbsolutePath(), true);
		writer.init(null);
		assertTrue(writer.isBuffered());

		writer.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertNull(reader.readLine());
		reader.close();

		writer.flush();

		reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		reader.close();

		writer.close();
	}

	/**
	 * Test writing without threading.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testSingleThreadedWriting() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		FileWriter writer = new FileWriter(file.getAbsolutePath());
		writer.init(null);
		writer.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());
		writer.write(new LogEntryBuilder().renderedLogEntry("World\n").create());
		writer.close();

		try {
			writer.write(new LogEntryBuilder().renderedLogEntry("Won't be written\n").create());
			fail("Exception expected");
		} catch (IOException ex) {
			// Expected
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		assertEquals("World", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

		file.delete();
	}

	/**
	 * Test writing with threading.
	 * 
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Sleep failed
	 */
	@Test
	public final void testMultiThreadedWriting() throws IOException, InterruptedException {
		File file = FileHelper.createTemporaryFile(null);

		FileWriter writer = new FileWriter(file.getAbsolutePath());
		writer.init(null);

		List<LoopWritingThread> threads = new ArrayList<LoopWritingThread>();
		for (int i = 0; i < 5; ++i) {
			threads.add(new LoopWritingThread(writer));
		}

		for (LoopWritingThread thread : threads) {
			thread.start();
		}

		Thread.sleep(100L);

		for (LoopWritingThread thread : threads) {
			thread.shutdown();
		}

		for (LoopWritingThread thread : threads) {
			thread.join();
		}

		long writtenLines = 0L;
		for (LoopWritingThread thread : threads) {
			writtenLines += thread.getWrittenLines();
		}

		long readLines = 0L;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			assertEquals(LoopWritingThread.LINE, line);
			++readLines;
		}
		reader.close();

		assertNotEquals(0, readLines);
		assertEquals(writtenLines, readLines);

		writer.close();
		file.delete();
	}

	/**
	 * Test overwriting of existing log file.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testOverwriting() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		FileHelper.write(file, "Hello World!");

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello World!", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

		FileWriter writer = new FileWriter(file.getAbsolutePath());
		writer.init(null);
		writer.close();

		reader = new BufferedReader(new FileReader(file));
		assertNull(reader.readLine());
		reader.close();

		file.delete();
	}

	/**
	 * Test if exception will be thrown if file can't be opened.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testOpenFileFails() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		File folder = file.getAbsoluteFile().getParentFile();
		FileWriter writer = new FileWriter(folder.getAbsolutePath());
		try {
			writer.init(null); // A folder can't be open as file
			fail("IOException expected");
		} catch (IOException ex) {
			// Expected
		}

		file.delete();
	}

	/**
	 * Test if exception will be thrown if writing fails.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testWritingFails() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		FileWriter writer = new FileWriter(file.getAbsolutePath());
		writer.init(null);

		MockUp<FileOutputStream> mock = new MockUp<FileOutputStream>() {

			@Mock
			public void write(final byte[] b) throws IOException {
				throw new IOException();
			}

		};

		try {
			writer.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());
			fail("IOException expected");
		} catch (IOException ex) {
			// Expected
		}

		mock.tearDown();

		writer.close();
		file.delete();
	}

	/**
	 * Test reading file logging writer from properties.
	 * 
	 * @throws IOException
	 *             Failed to create log file
	 */
	@Test
	public final void testFromProperties() throws IOException {
		File file = FileHelper.createTemporaryFile("log");
		String filename = file.getAbsolutePath();

		PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "file");
		List<LoggingWriter> writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, empty());
		assertThat(getErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("tinylog.writer.filename")));
		assertThat(getErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("file writer")));

		propertiesBuilder.set("tinylog.writer.filename", filename);
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(FileWriter.class));
		FileWriter fileWriter = (FileWriter) writers.get(0);
		assertEquals(filename, fileWriter.getFilename());
		assertFalse(fileWriter.isBuffered());

		propertiesBuilder.set("tinylog.writer.buffered", "true");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(FileWriter.class));
		fileWriter = (FileWriter) writers.get(0);
		assertEquals(filename, fileWriter.getFilename());
		assertTrue(fileWriter.isBuffered());

		propertiesBuilder.set("tinylog.writer.buffered", "false");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(FileWriter.class));
		fileWriter = (FileWriter) writers.get(0);
		assertEquals(filename, fileWriter.getFilename());
		assertFalse(fileWriter.isBuffered());

		file.delete();
	}

}
