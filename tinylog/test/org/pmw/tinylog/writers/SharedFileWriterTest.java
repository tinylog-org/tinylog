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

import org.junit.Assume;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.pmw.tinylog.EnvironmentHelper;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.LogEntryBuilder;
import org.pmw.tinylog.util.LoopWritingThread;
import org.pmw.tinylog.util.PropertiesBuilder;

import mockit.Mock;
import mockit.MockUp;

/**
 * Tests for the shared file writer.
 *
 * @see SharedFileWriter
 */
public class SharedFileWriterTest extends AbstractWriterTest {

	private static final int NUMBER_OF_JVMS = 5;
	private static final int LOG_ENTRIES = 1000;

	/**
	 * Test required log entry values.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRequiredLogEntryValue() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath(), true);
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

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath(), true);
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
		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath(), true);
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

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath(), true);
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
		file.delete();

		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath, SharedFileWriterTest.class.getCanonicalName(), file.getAbsolutePath());
		processBuilder.redirectErrorStream(true);

		List<Process> processes = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_JVMS; ++i) {
			processes.add(processBuilder.start());
		}

		file.createNewFile();
		for (Process process : processes) {
			process.waitFor();
		}

		long readLines = 0L;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			assertEquals(LoopWritingThread.LINE, line);
			++readLines;
		}
		reader.close();

		assertEquals(NUMBER_OF_JVMS * LOG_ENTRIES, readLines);

		file.delete();
	}

	/**
	 * Main method for {@link #testMultiJvmWriting()}.
	 *
	 * @param arguments
	 *            Contains the file name for writer
	 * @throws IOException
	 *             Logging failed
	 */
	public static void main(final String[] arguments) throws IOException {
		String filename = arguments[0];

		File file = new File(filename);
		while (!file.exists()) {
			Thread.yield();
		}

		SharedFileWriter writer = new SharedFileWriter(filename, true);
		writer.init(null);
		for (int i = 0; i < LOG_ENTRIES; ++i) {
			writer.write(new LogEntryBuilder().renderedLogEntry(LoopWritingThread.LINE + "\n").create());
			try {
				Thread.sleep(1L);
			} catch (InterruptedException ex) {
				// No problem
			}
		}
		writer.close();
	}

	/**
	 * Test flushing.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testFlush() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath(), true);
		writer.init(null);

		writer.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());
		writer.flush();

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		reader.close();

		writer.close();
	}

	/**
	 * Test continuing of existing log file.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testAppending() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		FileHelper.write(file, "Hello\n");

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath(), true);
		assertTrue(writer.isAppending());
		writer.init(null);
		writer.write(new LogEntryBuilder().renderedLogEntry("World\n").create());
		writer.close();

		reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		assertEquals("World", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

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
		Assume.assumeTrue("Supported only on Windows", EnvironmentHelper.isWindows());

		File file = FileHelper.createTemporaryFile(null);

		/* Overwriting by first writer */

		FileHelper.write(file, "Hello World!");

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello World!", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath());
		assertFalse(writer.isAppending());
		writer.init(null);
		writer.close();

		reader = new BufferedReader(new FileReader(file));
		assertNull(reader.readLine());
		reader.close();

		/* But no overwriting by second writer */

		SharedFileWriter writer1 = new SharedFileWriter(file.getAbsolutePath());
		assertFalse(writer.isAppending());
		writer1.init(null);
		writer1.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());

		SharedFileWriter writer2 = new SharedFileWriter(file.getAbsolutePath());
		assertFalse(writer.isAppending());
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
	 * Test automatically changing of append mode if not supporting.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testOverwritingFallback() throws IOException {
		Assume.assumeTrue("Only relevant for non-Windows operating systems", !EnvironmentHelper.isWindows());

		File file = FileHelper.createTemporaryFile(null);
		FileHelper.write(file, "Hello World!");

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello World!", reader.readLine());
		assertNull(reader.readLine());
		reader.close();

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath(), false);
		writer.init(null);
		writer.close();

		assertTrue(writer.isAppending());
		assertEquals("LOGGER WARNING: Shared file writer supports starting new log files only on Windows. Therefore \"append\" will be set automatically to "
				+ "\"true\".", getErrorStream().nextLine());

		reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello World!", reader.readLine());
		reader.close();

		file.delete();
	}

	/**
	 * Test creating a log file in an non-existing folder.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testNonexistengDirectory() throws IOException {
		TemporaryFolder folder = new TemporaryFolder();
		folder.create();
		folder.delete();

		File file = new File(folder.getRoot(), "test.log");

		assertFalse(folder.getRoot().exists());

		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath(), true);
		writer.init(null);
		writer.close();

		assertTrue(file.exists());

		folder.delete();
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
		SharedFileWriter writer = new SharedFileWriter(folder.getAbsolutePath(), true);
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
		SharedFileWriter writer = new SharedFileWriter(file.getAbsolutePath(), true);
		writer.init(null);

		new MockUp<FileOutputStream>() {
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

		writer.close();
		file.delete();
	}

	/**
	 * Test reading shared file writer from properties.
	 *
	 * @throws IOException
	 *             Failed to create log file
	 */
	@Test
	public final void testFromProperties() throws IOException {
		File file = FileHelper.createTemporaryFile("log");

		PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "sharedfile");
		List<Writer> writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, empty());
		assertEquals("LOGGER ERROR: Missing required property \"tinylog.writer.filename\"", getErrorStream().nextLine());
		assertEquals("LOGGER ERROR: Failed to initialize sharedfile writer", getErrorStream().nextLine());

		propertiesBuilder.set("tinylog.writer.filename", file.getAbsolutePath());
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(SharedFileWriter.class));
		SharedFileWriter sharedFileWriter = (SharedFileWriter) writers.get(0);
		assertEquals(file.getAbsolutePath(), sharedFileWriter.getFilename());
		if (EnvironmentHelper.isWindows()) {
			assertFalse(sharedFileWriter.isAppending());
		} else {
			assertTrue(sharedFileWriter.isAppending());
			assertEquals("LOGGER WARNING: Shared file writer supports starting new log files only on Windows. Therefore \"append\" will be set automatically "
					+ "to \"true\".", getErrorStream().nextLine());
		}

		propertiesBuilder.set("tinylog.writer.append", "true");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(SharedFileWriter.class));
		sharedFileWriter = (SharedFileWriter) writers.get(0);
		assertEquals(file.getAbsolutePath(), sharedFileWriter.getFilename());
		assertTrue(sharedFileWriter.isAppending());

		file.delete();
	}

}
