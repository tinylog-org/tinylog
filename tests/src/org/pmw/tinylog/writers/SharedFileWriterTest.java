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

package org.pmw.tinylog.writers;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.LogEntryBuilder;
import org.pmw.tinylog.util.WritingThread;

/**
 * Tests for the shared file logging writer.
 * 
 * @see SharedFileWriter
 */
public class SharedFileWriterTest extends AbstractTest {

	/**
	 * Test required log entry values.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRequiredLogEntryValue() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath());
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

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath());
		assertEquals(file.getAbsolutePath(), writer.getFilename());

		file.delete();
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
		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath());
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

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath());
		writer.init(null);

		List<WritingThread> threads = new ArrayList<WritingThread>();
		for (int i = 0; i < 5; ++i) {
			threads.add(new WritingThread(writer));
		}

		for (WritingThread thread : threads) {
			thread.start();
		}

		Thread.sleep(100L);

		for (WritingThread thread : threads) {
			thread.shutdown();
		}

		for (WritingThread thread : threads) {
			thread.join();
		}

		long writtenLines = 0L;
		for (WritingThread thread : threads) {
			writtenLines += thread.getWrittenLines();
		}

		long readLines = 0L;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			assertEquals(WritingThread.LINE, line);
			++readLines;
		}
		reader.close();

		assertNotEquals(0, readLines);
		assertEquals(writtenLines, readLines);

		writer.close();
		file.delete();
	}

	/**
	 * Test simultaneously writing from multiple JVMs.
	 * 
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testMultiJvmWriting() throws IOException, InterruptedException {
		File file = FileHelper.createTemporaryFile(null);

		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath, SharedFileWriterTest.class.getCanonicalName(), file.getAbsolutePath());
		processBuilder.redirectErrorStream(true);

		List<Process> processes = new ArrayList<>();
		for (int i = 0; i < 5; ++i) {
			processes.add(processBuilder.start());
		}

		for (Process process : processes) {
			process.waitFor();
		}

		long readLines = 0L;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			assertEquals(WritingThread.LINE, line);
			++readLines;
		}
		reader.close();

		assertEquals(5 * 10000, readLines);

		file.delete();
	}

	/**
	 * Main method for {@link #testMultiJvmWriting()}.
	 * 
	 * @param arguments
	 *            Contains the file name for logging writer
	 * @throws IOException
	 *             Logging failed
	 */
	public static void main(final String[] arguments) throws IOException {
		SharedFileWriter writer = new SharedFileWriter(arguments[0]);
		writer.init(null);
		for (int i = 0; i < 10000; ++i) {
			writer.write(new LogEntryBuilder().renderedLogEntry(WritingThread.LINE + "\n").create());
		}
		writer.close();
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

		/* Overwriting by first writer */

		FileHelper.write(file, "Hello World!");

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello World!", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath());
		writer.init(null);
		writer.close();

		reader = new BufferedReader(new FileReader(file));
		assertNull(reader.readLine());
		reader.close();

		/* But no overwriting by second writer */

		SharedFileWriter writer1 = new SharedFileWriter(file.getAbsolutePath());
		writer1.init(null);
		writer1.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());

		SharedFileWriter writer2 = new SharedFileWriter(file.getAbsolutePath());
		writer2.init(null);
		writer2.write(new LogEntryBuilder().renderedLogEntry("World\n").create());

		writer1.close();
		writer2.close();

		reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		assertEquals("World", reader.readLine());
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
		SharedFileWriter writer = new SharedFileWriter(folder.getAbsolutePath());
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
		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath());
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
}
