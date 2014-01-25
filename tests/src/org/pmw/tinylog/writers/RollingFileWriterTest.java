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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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
import org.pmw.tinylog.labellers.CountLabeller;
import org.pmw.tinylog.labellers.ProcessIdLabeller;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.util.ConfigurationCreator;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.LogEntryBuilder;
import org.pmw.tinylog.util.WritingThread;

/**
 * Tests for the rolling file logging writer.
 * 
 * @see RollingFileWriter
 */
public class RollingFileWriterTest extends AbstractTest {

	/**
	 * Test all constructors.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testCreateInstance() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 10);
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(10, writer.getNumberOfBackups());
		assertFalse(writer.isBuffered());
		assertThat(writer.getLabeller(), instanceOf(CountLabeller.class));
		assertThat(writer.getPolicies(), hasSize(1));
		assertThat(writer.getPolicies().get(0), instanceOf(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 10, true);
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(10, writer.getNumberOfBackups());
		assertTrue(writer.isBuffered());
		assertThat(writer.getLabeller(), instanceOf(CountLabeller.class));
		assertThat(writer.getPolicies(), hasSize(1));
		assertThat(writer.getPolicies().get(0), instanceOf(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, new ProcessIdLabeller());
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertFalse(writer.isBuffered());
		assertThat(writer.getLabeller(), instanceOf(ProcessIdLabeller.class));
		assertThat(writer.getPolicies(), hasSize(1));
		assertThat(writer.getPolicies().get(0), instanceOf(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, true, new ProcessIdLabeller());
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertTrue(writer.isBuffered());
		assertThat(writer.getLabeller(), instanceOf(ProcessIdLabeller.class));
		assertThat(writer.getPolicies(), hasSize(1));
		assertThat(writer.getPolicies().get(0), instanceOf(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, new Policy[0]);
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertFalse(writer.isBuffered());
		assertThat(writer.getLabeller(), instanceOf(CountLabeller.class));
		assertThat(writer.getPolicies(), hasSize(1));
		assertThat(writer.getPolicies().get(0), instanceOf(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, true, new Policy[0]);
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertTrue(writer.isBuffered());
		assertThat(writer.getLabeller(), instanceOf(CountLabeller.class));
		assertThat(writer.getPolicies(), hasSize(1));
		assertThat(writer.getPolicies().get(0), instanceOf(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, new ProcessIdLabeller(), new SizePolicy(1024), new DailyPolicy());
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertFalse(writer.isBuffered());
		assertThat(writer.getLabeller(), instanceOf(ProcessIdLabeller.class));
		assertThat(writer.getPolicies(), hasSize(2));
		assertThat(writer.getPolicies().get(0), instanceOf(SizePolicy.class));
		assertThat(writer.getPolicies().get(1), instanceOf(DailyPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, true, new ProcessIdLabeller(), new SizePolicy(1024), new DailyPolicy());
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertTrue(writer.isBuffered());
		assertThat(writer.getLabeller(), instanceOf(ProcessIdLabeller.class));
		assertThat(writer.getPolicies(), hasSize(2));
		assertThat(writer.getPolicies().get(0), instanceOf(SizePolicy.class));
		assertThat(writer.getPolicies().get(1), instanceOf(DailyPolicy.class));
		file.delete();
	}

	/**
	 * Test required log entry values.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testRequiredLogEntryValue() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0);
		Set<LogEntryValue> requiredLogEntryValues = writer.getRequiredLogEntryValues();
		assertThat(requiredLogEntryValues, contains(LogEntryValue.RENDERED_LOG_ENTRY));

		file.delete();
	}

	/**
	 * Test simple writing (non-existing log file and neither policies nor a labeller).
	 * 
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testSimpleWriting() throws Exception {
		File file = FileHelper.createTemporaryFile(null);
		file.delete();

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0);
		writer.init(ConfigurationCreator.getDummyConfiguration());
		assertEquals(file.getAbsolutePath(), writer.getFilename());
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
	 * Test if unbuffered rolling file writer writes log entries immediately.
	 * 
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testUnbufferedWriting() throws Exception {
		File file = FileHelper.createTemporaryFile(null);

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0, false);
		writer.init(ConfigurationCreator.getDummyConfiguration());
		assertFalse(writer.isBuffered());

		writer.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());

		BufferedReader reader = new BufferedReader(new FileReader(file));
		assertEquals("Hello", reader.readLine());
		reader.close();

		writer.close();
	}

	/**
	 * Test if buffered rolling file writer caches log entries.
	 * 
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testBufferedWriting() throws Exception {
		File file = FileHelper.createTemporaryFile(null);

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0, true);
		writer.init(ConfigurationCreator.getDummyConfiguration());
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
	 * Test writing with threading.
	 * 
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testMultiThreadedWriting() throws Exception {
		File file = FileHelper.createTemporaryFile(null);

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 1);
		writer.init(ConfigurationCreator.getDummyConfiguration());
		assertEquals(file.getAbsolutePath(), writer.getFilename());

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
	 * Test rolling while writing.
	 * 
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testRollingWhileWriting() throws Exception {
		testRollingWhileWriting(false);
		testRollingWhileWriting(true);
	}

	/**
	 * Test rolling while opening.
	 * 
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testRollingWhileOpening() throws Exception {
		File file = FileHelper.createTemporaryFile(null, "123");
		File backup = new File(file.getAbsolutePath() + ".0");

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 100, new StartupPolicy());
		writer.init(ConfigurationCreator.getDummyConfiguration());
		writer.close();

		assertEquals("", FileHelper.read(file));
		assertEquals("123", FileHelper.read(backup));

		file.delete();
		backup.delete();
	}

	/**
	 * Test if exception will be thrown if file can't be opened.
	 * 
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testOpenFileFails() throws Exception {
		File file = FileHelper.createTemporaryFile(null);

		File folder = file.getAbsoluteFile().getParentFile();
		RollingFileWriter writer = new RollingFileWriter(folder.getAbsolutePath(), 0, new Policy() {

			@Override
			public boolean initCheck(final File logFile) {
				return true;
			}

			@Override
			public boolean check(final String logEntry) {
				return true;
			}

			@Override
			public void reset() {
				// Do nothing
			}

		});
		try {
			writer.init(ConfigurationCreator.getDummyConfiguration()); // A folder can't be open as file
			fail("IOException expected");
		} catch (IOException ex) {
			// Expected
		}

		file.delete();
	}

	/**
	 * Test if exception will be thrown if writing fails.
	 * 
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testWritingFails() throws Exception {
		File file = FileHelper.createTemporaryFile(null);
		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0);
		writer.init(ConfigurationCreator.getDummyConfiguration());

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

	private void testRollingWhileWriting(final boolean buffered) throws Exception {
		File file = FileHelper.createTemporaryFile(null, "12");
		File backup = new File(file.getAbsolutePath() + ".0");

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 100, buffered, new SizePolicy(3));
		writer.init(ConfigurationCreator.getDummyConfiguration());
		writer.write(new LogEntryBuilder().renderedLogEntry("3").create());
		writer.write(new LogEntryBuilder().renderedLogEntry("4").create());
		writer.write(new LogEntryBuilder().renderedLogEntry("5").create());
		writer.close();

		assertEquals("45", FileHelper.read(file));
		assertEquals("123", FileHelper.read(backup));

		file.delete();
		backup.delete();
	}

}
