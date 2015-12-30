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

package org.tinylog.writers;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.tinylog.hamcrest.ClassMatchers.type;
import static org.tinylog.hamcrest.CollectionMatchers.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.tinylog.Configuration;
import org.tinylog.labelers.CountLabeler;
import org.tinylog.labelers.Labeler;
import org.tinylog.labelers.ProcessIdLabeler;
import org.tinylog.labelers.TimestampLabeler;
import org.tinylog.policies.DailyPolicy;
import org.tinylog.policies.Policy;
import org.tinylog.policies.SizePolicy;
import org.tinylog.policies.StartupPolicy;
import org.tinylog.util.ConfigurationCreator;
import org.tinylog.util.FileHelper;
import org.tinylog.util.LogEntryBuilder;
import org.tinylog.util.LoopWritingThread;
import org.tinylog.util.PropertiesBuilder;

import mockit.Mock;
import mockit.MockUp;

/**
 * Tests for the rolling file writer.
 *
 * @see RollingFileWriter
 */
public class RollingFileWriterTest extends AbstractWriterTest {

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
		assertThat(writer.getLabeler(), type(CountLabeler.class));
		assertThat(writer.getPolicies(), types(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 10, true);
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(10, writer.getNumberOfBackups());
		assertTrue(writer.isBuffered());
		assertThat(writer.getLabeler(), type(CountLabeler.class));
		assertThat(writer.getPolicies(), types(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, new ProcessIdLabeler());
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertFalse(writer.isBuffered());
		assertThat(writer.getLabeler(), type(ProcessIdLabeler.class));
		assertThat(writer.getPolicies(), types(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, true, new ProcessIdLabeler());
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertTrue(writer.isBuffered());
		assertThat(writer.getLabeler(), type(ProcessIdLabeler.class));
		assertThat(writer.getPolicies(), types(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, new Policy[0]);
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertFalse(writer.isBuffered());
		assertThat(writer.getLabeler(), type(CountLabeler.class));
		assertThat(writer.getPolicies(), types(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, true, new Policy[0]);
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertTrue(writer.isBuffered());
		assertThat(writer.getLabeler(), type(CountLabeler.class));
		assertThat(writer.getPolicies(), types(StartupPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, new ProcessIdLabeler(), new SizePolicy(1024), new DailyPolicy());
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertFalse(writer.isBuffered());
		assertThat(writer.getLabeler(), type(ProcessIdLabeler.class));
		assertThat(writer.getPolicies(), types(SizePolicy.class, DailyPolicy.class));
		file.delete();

		file = FileHelper.createTemporaryFile(null);
		writer = new RollingFileWriter(file.getAbsolutePath(), 42, true, new ProcessIdLabeler(), new SizePolicy(1024), new DailyPolicy());
		assertEquals(file.getAbsolutePath(), writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
		assertTrue(writer.isBuffered());
		assertThat(writer.getLabeler(), type(ProcessIdLabeler.class));
		assertThat(writer.getPolicies(), types(SizePolicy.class, DailyPolicy.class));
		file.delete();
	}

	/**
	 * Test calling of rolling listeners.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testListeners() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		LifeCycleListener listener1 = new LifeCycleListener();
		LifeCycleListener listener2 = new LifeCycleListener();

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 1, new SizePolicy(1));
		writer.addListener(listener1);
		writer.addListener(listener2);
		assertEquals(0, listener1.startups);
		assertEquals(0, listener2.startups);
		assertEquals(0, listener1.rolls);
		assertEquals(0, listener2.rolls);
		assertEquals(0, listener1.shutdowns);
		assertEquals(0, listener2.shutdowns);

		writer.init(ConfigurationCreator.getDummyConfiguration());
		assertEquals(1, listener1.startups);
		assertEquals(1, listener2.startups);
		assertEquals(0, listener1.rolls);
		assertEquals(0, listener2.rolls);
		assertEquals(0, listener1.shutdowns);
		assertEquals(0, listener2.shutdowns);
		assertEquals(file, listener1.currentFile);
		assertNull(listener1.backupFile);
		assertEquals(file, listener2.currentFile);
		assertNull(listener2.backupFile);

		writer.write(new LogEntryBuilder().renderedLogEntry("..").create());
		assertNotNull(listener2.backupFile);
		assertEquals(1, listener1.startups);
		assertEquals(1, listener2.startups);
		assertEquals(1, listener1.rolls);
		assertEquals(1, listener2.rolls);
		assertEquals(0, listener1.shutdowns);
		assertEquals(0, listener2.shutdowns);
		assertEquals(file, listener1.currentFile);
		assertNotEquals(listener1.currentFile, listener1.backupFile);
		assertNotNull(listener1.backupFile);
		assertEquals(file, listener2.currentFile);
		assertNotNull(listener2.backupFile);
		assertNotEquals(listener2.currentFile, listener2.backupFile);

		writer.removeListener(listener2);

		writer.write(new LogEntryBuilder().renderedLogEntry("..").create());
		assertEquals(file, listener1.currentFile);
		assertNotNull(listener1.backupFile);
		assertEquals(file, listener2.currentFile);
		assertNotNull(listener2.backupFile);
		assertEquals(1, listener1.startups);
		assertEquals(1, listener2.startups);
		assertEquals(2, listener1.rolls);
		assertEquals(1, listener2.rolls);
		assertEquals(0, listener1.shutdowns);
		assertEquals(0, listener2.shutdowns);
		assertEquals(file, listener1.currentFile);
		assertNotNull(listener1.backupFile);
		assertNotEquals(listener1.currentFile, listener1.backupFile);

		writer.close();
		assertEquals(1, listener1.startups);
		assertEquals(1, listener2.startups);
		assertEquals(2, listener1.rolls);
		assertEquals(1, listener2.rolls);
		assertEquals(1, listener1.shutdowns);
		assertEquals(0, listener2.shutdowns);
		assertEquals(file, listener1.currentFile);
		assertNull(listener1.backupFile);

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
	 * Test simple writing (non-existing log file and neither policies nor a labeler).
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

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			assertEquals("Hello", reader.readLine());
			assertEquals("World", reader.readLine());
			assertNull(reader.readLine());
		}

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

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			assertEquals("Hello", reader.readLine());
		}

		writer.close();
	}

	/**
	 * Test if buffered rolling file writer writes log entries after close.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testBufferedWriting() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0, true);
		writer.init(ConfigurationCreator.getDummyConfiguration());
		assertTrue(writer.isBuffered());

		writer.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			assertNull(reader.readLine());
		}

		writer.close();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			assertEquals("Hello", reader.readLine());
		}
	}

	/**
	 * Test if buffered rolling file writer writes log entries after flush.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testFlush() throws IOException {
		File file = FileHelper.createTemporaryFile(null);

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0, true);
		writer.init(ConfigurationCreator.getDummyConfiguration());
		assertTrue(writer.isBuffered());

		writer.write(new LogEntryBuilder().renderedLogEntry("Hello\n").create());

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			assertNull(reader.readLine());
		}

		writer.flush();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			assertEquals("Hello", reader.readLine());
		}

		writer.close();
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

		List<LoopWritingThread> threads = new ArrayList<>();
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
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				assertEquals(LoopWritingThread.LINE, line);
				++readLines;
			}
		}

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

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0, new CountLabeler());
		writer.init(null);
		writer.close();

		assertTrue(file.exists());

		folder.delete();
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
			public void init(final Configuration configuration) {
				// Do nothing
			}

			@Override
			public boolean check(final File logFile) {
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

	/**
	 * Test reading rolling file writer from properties.
	 *
	 * @throws IOException
	 *             Failed to create log file
	 */
	@Test
	public final void testFromProperties() throws IOException {
		List<Writer> writers = createFromProperties(new PropertiesBuilder().set("tinylog.writer", "rollingfile").create());
		assertThat(writers, empty());
		assertEquals("LOGGER ERROR: Missing required property \"tinylog.writer.filename\"", getErrorStream().nextLine());
		assertEquals("LOGGER ERROR: Failed to initialize rollingfile writer", getErrorStream().nextLine());

		testFromProperties(null); // Default
		testFromProperties(false); // Non buffered
		testFromProperties(true); // Buffered
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

	private void testFromProperties(final Boolean buffered) throws IOException {
		File file = FileHelper.createTemporaryFile("log");
		boolean expectBuffered = Boolean.TRUE.equals(buffered);

		PropertiesBuilder defaultPropertiesBuilder = new PropertiesBuilder();
		defaultPropertiesBuilder.set("tinylog.writer", "rollingfile");
		defaultPropertiesBuilder.set("tinylog.writer.filename", file.getAbsolutePath());
		if (Boolean.TRUE.equals(buffered)) {
			defaultPropertiesBuilder.set("tinylog.writer.buffered", "true");
		} else if (Boolean.FALSE.equals(buffered)) {
			defaultPropertiesBuilder.set("tinylog.writer.buffered", "false");
		}

		PropertiesBuilder propertiesBuilder = defaultPropertiesBuilder.copy();
		List<Writer> writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, empty());
		assertEquals("LOGGER ERROR: Missing required property \"tinylog.writer.backups\"", getErrorStream().nextLine());
		assertEquals("LOGGER ERROR: Failed to initialize rollingfile writer", getErrorStream().nextLine());

		propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.backups", "1");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(RollingFileWriter.class));
		RollingFileWriter rollingFileWriter = (RollingFileWriter) writers.get(0);
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(1, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		Labeler labeler = rollingFileWriter.getLabeler();
		assertThat(labeler, type(CountLabeler.class));
		List<? extends Policy> policies = rollingFileWriter.getPolicies();
		assertThat(policies, types(StartupPolicy.class));

		propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.backups", "2").set("tinylog.writer.label", "pid");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(RollingFileWriter.class));
		rollingFileWriter = (RollingFileWriter) writers.get(0);
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(2, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		labeler = rollingFileWriter.getLabeler();
		assertThat(labeler, type(ProcessIdLabeler.class));
		policies = rollingFileWriter.getPolicies();
		assertThat(policies, types(StartupPolicy.class));

		propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.backups", "3").set("tinylog.writer.policies", "daily");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(RollingFileWriter.class));
		rollingFileWriter = (RollingFileWriter) writers.get(0);
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(3, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		labeler = rollingFileWriter.getLabeler();
		assertThat(labeler, type(CountLabeler.class));
		policies = rollingFileWriter.getPolicies();
		assertThat(policies, types(DailyPolicy.class));

		propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.backups", "4").set("tinylog.writer.label", "timestamp")
				.set("tinylog.writer.policies", "startup, daily");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(RollingFileWriter.class));
		rollingFileWriter = (RollingFileWriter) writers.get(0);
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(4, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		labeler = rollingFileWriter.getLabeler();
		assertThat(labeler, type(TimestampLabeler.class));
		policies = rollingFileWriter.getPolicies();
		assertThat(policies, types(StartupPolicy.class, DailyPolicy.class));

		int instances = LifeCycleListener.instances;
		propertiesBuilder = defaultPropertiesBuilder.copy().set("tinylog.writer.backups", "5").set("tinylog.writer.listeners", LifeCycleListener.class.getName());
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(RollingFileWriter.class));
		rollingFileWriter = (RollingFileWriter) writers.get(0);
		assertEquals(file.getAbsolutePath(), rollingFileWriter.getFilename());
		assertEquals(5, rollingFileWriter.getNumberOfBackups());
		assertEquals(expectBuffered, rollingFileWriter.isBuffered());
		labeler = rollingFileWriter.getLabeler();
		assertThat(labeler, type(CountLabeler.class));
		policies = rollingFileWriter.getPolicies();
		assertThat(policies, types(StartupPolicy.class));
		assertEquals(instances + 1, LifeCycleListener.instances);

		file.delete();
	}

	/**
	 * Rolling listener for testing life cycle.
	 */
	public static final class LifeCycleListener implements RollingListener {

		private static volatile int instances;

		private int startups;
		private int rolls;
		private int shutdowns;

		private File currentFile;
		private File backupFile;

		/** */
		public LifeCycleListener() {
			++instances;
		}

		@Override
		public void startup(final File file) throws Exception {
			++startups;
			currentFile = file;
			backupFile = null;
		}

		@Override
		public void rolled(final File backup, final File file) throws Exception {
			++rolls;
			currentFile = file;
			backupFile = backup;
		}

		@Override
		public void shutdown(final File file) throws Exception {
			++shutdowns;
			currentFile = file;
			backupFile = null;
		}

	}

}
