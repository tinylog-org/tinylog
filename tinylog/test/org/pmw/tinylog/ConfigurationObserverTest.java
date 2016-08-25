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

package org.pmw.tinylog;

import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.sameTypes;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.types;
import static org.pmw.tinylog.hamcrest.StringMatchers.matchesPattern;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import mockit.Mock;
import mockit.MockUp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.pmw.tinylog.mocks.ClassLoaderMock;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import org.pmw.tinylog.writers.Writer;

/**
 * Tests for configuration observer.
 *
 * @see ConfigurationObserver
 */
@RunWith(Parameterized.class)
public class ConfigurationObserverTest extends AbstractTinylogTest {

	private static final Configuration DEFAULT_CONFIGURATION = Configurator.defaultConfig().create();

	private ClassLoaderMock classLoaderMock;

	private final ConfigurationObserverInitializer initializer;
	private ConfigurationObserver observer;
	private File file;

	/**
	 * @param initializer
	 *            Wrapper class to create a configuration observer
	 */
	public ConfigurationObserverTest(final ConfigurationObserverInitializer initializer) {
		this.initializer = initializer;
	}

	/**
	 * Get the creation wrappers for all available configuration observers.
	 *
	 * @return Creation wrappers for all available configuration observers
	 */
	@Parameters(name = "{0}")
	public static Collection<?> getParameters() {
		final Properties properties = new Properties();

		Properties systemProperties = System.getProperties();
		for (String key : systemProperties.stringPropertyNames()) {
			if (key.startsWith("tinylog.")) {
				properties.put(key, systemProperties.getProperty(key));
			}
		}

		return Arrays.asList(new Object[] { new ConfigurationObserverInitializer() {

			@Override
			public void create(final ClassLoaderMock classLoaderMock) throws IOException {
				String path = "config/tinylog.properties";
				init(classLoaderMock.set(path), ConfigurationObserver.createResourceConfigurationObserver(Configurator.defaultConfig(), properties, path));
			}

			@Override
			public String toString() {
				return "resource";
			}

		} }, new Object[] { new ConfigurationObserverInitializer() {

			@Override
			public void create(final ClassLoaderMock classLoaderMock) throws IOException {
				File localFile = FileHelper.createTemporaryFile("properties");
				init(localFile, ConfigurationObserver.createFileConfigurationObserver(Configurator.defaultConfig(), properties, localFile.getAbsolutePath()));
			}

			@Override
			public String toString() {
				return "file";
			}

		} });
	}

	/**
	 * Set up mocks for thread and class loader.
	 *
	 * @throws IOException
	 *             Failed to initialize configuration observer
	 */
	@Before
	public final void init() throws IOException {
		classLoaderMock = new ClassLoaderMock((URLClassLoader) ConfigurationObserverTest.class.getClassLoader());

		initializer.create(classLoaderMock);
		observer = initializer.observer;
		file = initializer.file;
	}

	/**
	 * Tear down mocks.
	 */
	@After
	public final void dispose() {
		classLoaderMock.close();
		file.delete();
	}

	/**
	 * Test start and stop of configuration observer.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testStartAndStop() throws IOException, InterruptedException {
		assertThat(observer.getPriority(), lessThan(Thread.NORM_PRIORITY));
		assertTrue(observer.isDaemon());

		int threadCount = Thread.activeCount();
		assertNull(ConfigurationObserver.getActiveObserver());

		observer.start();

		assertSame(observer, ConfigurationObserver.getActiveObserver());
		assertTrue(observer.isAlive());
		assertEquals(threadCount + 1, Thread.activeCount());

		observer.shutdown();
		observer.join();

		assertNull(ConfigurationObserver.getActiveObserver());
		assertFalse(observer.isAlive());
		assertEquals(threadCount, Thread.activeCount());
	}

	/**
	 * Test reading of an empty configuration file.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testEmptyFile() throws IOException, InterruptedException {
		observer.start();

		try {
			/* Test if an empty file leads to the default configuration */
			waitForCompleteCycle();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getLevel(), currentConfiguration.getLevel());
			assertEquals(DEFAULT_CONFIGURATION.getFormatPattern(), currentConfiguration.getFormatPattern());
			assertEquals(DEFAULT_CONFIGURATION.getLocale(), currentConfiguration.getLocale());
			assertEquals(DEFAULT_CONFIGURATION.getMaxStackTraceElements(), currentConfiguration.getMaxStackTraceElements());
			assertThat(currentConfiguration.getWriters(), sameTypes(DEFAULT_CONFIGURATION.getWriters()));
			assertSame(DEFAULT_CONFIGURATION.getWritingThread(), currentConfiguration.getWritingThread());
		} finally {
			observer.shutdown();
			observer.join();
		}
	}

	/**
	 * Test reading of logging level.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testLoggingLevel() throws IOException, InterruptedException {
		observer.start();

		try {
			/* Test new logging level */

			FileHelper.write(file, "tinylog.level=ERROR");
			waitForCompleteCycle();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(Level.ERROR, currentConfiguration.getLevel());

			/* Test remove logging level */

			FileHelper.write(file, "");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getLevel(), currentConfiguration.getLevel());

			/* Test new custom logging level */

			FileHelper.write(file, "tinylog.level@" + ConfigurationObserverTest.class.getName() + "=TRACE");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(Level.TRACE, currentConfiguration.getLevel(ConfigurationObserverTest.class.getName()));

			/* Test remove custom logging level */

			FileHelper.write(file, "");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getLevel(), currentConfiguration.getLevel());
		} finally {
			observer.shutdown();
			observer.join();
		}
	}

	/**
	 * Test reading of format pattern.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testFormatPattern() throws IOException, InterruptedException {
		observer.start();

		try {
			/* Test new format pattern */

			FileHelper.write(file, "tinylog.format={thread} -> {message}");
			waitForCompleteCycle();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals("{thread} -> {message}", currentConfiguration.getFormatPattern());

			/* Test remove format pattern */

			FileHelper.write(file, "");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getFormatPattern(), currentConfiguration.getFormatPattern());
		} finally {
			observer.shutdown();
			observer.join();
		}
	}

	/**
	 * Test reading of locale.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testLocale() throws IOException, InterruptedException {
		observer.start();

		try {
			/* Test new locale */

			FileHelper.write(file, "tinylog.locale=de_US");
			waitForCompleteCycle();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(new Locale("de", "US"), currentConfiguration.getLocale());

			/* Test remove locale */

			FileHelper.write(file, "");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getLocale(), currentConfiguration.getLocale());
		} finally {
			observer.shutdown();
			observer.join();
		}
	}

	/**
	 * Test reading of max stack trace elements number.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testMaxStackTraceElements() throws IOException, InterruptedException {
		observer.start();

		try {
			/* Test new max stack trace elements number */

			FileHelper.write(file, "tinylog.stacktrace=42");
			waitForCompleteCycle();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(42, currentConfiguration.getMaxStackTraceElements());

			/* Test remove max stack trace elements number */

			FileHelper.write(file, "");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getMaxStackTraceElements(), currentConfiguration.getMaxStackTraceElements());
		} finally {
			observer.shutdown();
			observer.join();
		}
	}

	/**
	 * Test reading of writer.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testWriter() throws IOException, InterruptedException {
		File logFile1 = FileHelper.createTemporaryFile("log");
		File logFile2 = FileHelper.createTemporaryFile("log");
		observer.start();

		try {
			/* Test new writer without arguments */

			FileHelper.write(file, "tinylog.writer=console");
			waitForCompleteCycle();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertThat(currentConfiguration.getWriters(), types(ConsoleWriter.class));

			/* Test remove writer */

			FileHelper.write(file, "");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			assertThat(currentConfiguration.getWriters(), sameTypes(DEFAULT_CONFIGURATION.getWriters()));

			/* Test new writer with arguments */

			FileHelper.write(file, "tinylog.writer=file", "tinylog.writer.filename=" + logFile1.getAbsolutePath());
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			List<Writer> writers = currentConfiguration.getWriters();
			assertThat(writers, types(FileWriter.class));
			FileWriter fileWriter = (FileWriter) writers.get(0);
			assertEquals(logFile1.getAbsolutePath(), fileWriter.getFilename());
			fileWriter.close();

			/* Argument of writer has changed */

			FileHelper.write(file, "tinylog.writer=file", "tinylog.writer.filename=" + logFile2.getAbsolutePath());
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			writers = currentConfiguration.getWriters();
			assertThat(writers, types(FileWriter.class));
			fileWriter = (FileWriter) writers.get(0);
			assertEquals(logFile2.getAbsolutePath(), fileWriter.getFilename());
			fileWriter.close();
		} finally {
			observer.shutdown();
			observer.join();
			logFile2.delete();
			logFile1.delete();
		}
	}

	/**
	 * Test reading of multiple writers.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testMultipleWriters() throws IOException, InterruptedException {
		File logFile = FileHelper.createTemporaryFile("log");
		observer.start();

		try {
			/* Test new writers */

			FileHelper.write(file, "tinylog.writer=console", "tinylog.writer2=file", "tinylog.writer2.filename=" + logFile.getAbsolutePath());
			waitForCompleteCycle();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			List<Writer> writers = currentConfiguration.getWriters();
			assertThat(writers, types(ConsoleWriter.class, FileWriter.class));
			FileWriter fileWriter = (FileWriter) writers.get(1);
			assertEquals(logFile.getAbsolutePath(), fileWriter.getFilename());
			assertFalse(fileWriter.isBuffered());
			fileWriter.close();

			/* Test remove first writer */

			FileHelper.write(file, "tinylog.writer2=file", "tinylog.writer2.filename=" + logFile.getAbsolutePath());
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			writers = currentConfiguration.getWriters();
			assertThat(writers, types(FileWriter.class));
			fileWriter = (FileWriter) writers.get(0);
			assertEquals(logFile.getAbsolutePath(), fileWriter.getFilename());
			assertFalse(fileWriter.isBuffered());
			fileWriter.close();

			/* Test change existing writer */

			FileHelper.write(file, "tinylog.writer2=file", "tinylog.writer2.filename=" + logFile.getAbsolutePath(), "tinylog.writer2.buffered=true");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			writers = currentConfiguration.getWriters();
			assertThat(writers, types(FileWriter.class));
			fileWriter = (FileWriter) writers.get(0);
			assertEquals(logFile.getAbsolutePath(), fileWriter.getFilename());
			assertTrue(fileWriter.isBuffered());
			fileWriter.close();

			/* Test add additional writer */

			FileHelper.write(file, "tinylog.writer2=file", "tinylog.writer2.filename=" + logFile.getAbsolutePath(), "tinylog.writer2.buffered=true",
					"tinylog.writerNew=console");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			writers = currentConfiguration.getWriters();
			assertThat(writers, types(FileWriter.class, ConsoleWriter.class));
			fileWriter = (FileWriter) writers.get(0);
			assertEquals(logFile.getAbsolutePath(), fileWriter.getFilename());
			assertTrue(fileWriter.isBuffered());
			fileWriter.close();
		} finally {
			observer.shutdown();
			observer.join();
			file.delete();
		}
	}

	/**
	 * Test reading of writing thread.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testWritingThread() throws IOException, InterruptedException {
		observer.start();

		try {
			/* Test new writing thread */

			FileHelper.write(file, "tinylog.writingthread=true", "tinylog.writingthread.priority=9");
			waitForCompleteCycle();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			WritingThread writingThread = currentConfiguration.getWritingThread();
			assertNotNull(writingThread);
			assertEquals(9, writingThread.getPriority());

			/* Test remove writing thread */

			FileHelper.write(file, "");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			assertNull(currentConfiguration.getWritingThread());
		} finally {
			observer.shutdown();
			observer.join();
		}
	}

	/**
	 * Test overriding properties by system properties.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testSystemProperties() throws IOException, InterruptedException {
		observer.start();

		try {
			/* Without system properties */

			FileHelper.write(file, "tinylog.level=ERROR");
			waitForCompleteCycle();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(Level.ERROR, currentConfiguration.getLevel());

			/* Overriding properties by system properties. */

			System.setProperty("tinylog.level", "TRACE");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(Level.TRACE, currentConfiguration.getLevel());

			/* Without system properties */

			System.clearProperty("tinylog.level");
			waitForCompleteCycle();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(Level.ERROR, currentConfiguration.getLevel());
		} finally {
			observer.shutdown();
			observer.join();
		}
	}

	/**
	 * Test to read a nonexistent file.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testNonexistentFile() throws IOException, InterruptedException {
		file.delete();

		observer.start();
		waitForCompleteCycle();
		observer.shutdown();
		observer.join();

		Configuration currentConfiguration = Logger.getConfiguration().create();
		assertThat(getErrorStream().nextLine(), matchesPattern("LOGGER ERROR\\: Failed to open \\\".+\\\""));
		assertEquals(DEFAULT_CONFIGURATION.getLevel(), currentConfiguration.getLevel());
		assertEquals(DEFAULT_CONFIGURATION.getFormatPattern(), currentConfiguration.getFormatPattern());
		assertEquals(DEFAULT_CONFIGURATION.getLocale(), currentConfiguration.getLocale());
		assertEquals(DEFAULT_CONFIGURATION.getMaxStackTraceElements(), currentConfiguration.getMaxStackTraceElements());
		assertThat(currentConfiguration.getWriters(), sameTypes(DEFAULT_CONFIGURATION.getWriters()));
		assertSame(DEFAULT_CONFIGURATION.getWritingThread(), currentConfiguration.getWritingThread());

	}

	/**
	 * Test to read a existing file.
	 *
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testInvalidExistingFile() throws IOException, InterruptedException {
		new MockUp<Properties>() {
			@Mock
			public void load(final InputStream inStream) throws IOException {
				throw new IOException();
			}
		};

		observer.start();
		waitForCompleteCycle();
		observer.shutdown();
		observer.join();

		Configuration currentConfiguration = Logger.getConfiguration().create();
		assertEquals("LOGGER ERROR: Failed to read properties file (" + IOException.class.getName() + ")", getErrorStream().nextLine());
		assertEquals(DEFAULT_CONFIGURATION.getLevel(), currentConfiguration.getLevel());
		assertEquals(DEFAULT_CONFIGURATION.getFormatPattern(), currentConfiguration.getFormatPattern());
		assertEquals(DEFAULT_CONFIGURATION.getLocale(), currentConfiguration.getLocale());
		assertEquals(DEFAULT_CONFIGURATION.getMaxStackTraceElements(), currentConfiguration.getMaxStackTraceElements());
		assertThat(currentConfiguration.getWriters(), sameTypes(DEFAULT_CONFIGURATION.getWriters()));
		assertSame(DEFAULT_CONFIGURATION.getWritingThread(), currentConfiguration.getWritingThread());
	}

	private void waitForCompleteCycle() throws InterruptedException {
		while (observer.getState() == Thread.State.RUNNABLE) {
			Thread.sleep(10);
		}

		if (observer.getState() == Thread.State.TIMED_WAITING) {
			observer.interrupt();
			Thread.sleep(10);
			while (observer.getState() == Thread.State.RUNNABLE) {
				Thread.sleep(10);
			}
			Thread.sleep(10);
		}
	}

	private abstract static class ConfigurationObserverInitializer {

		private File file;
		private ConfigurationObserver observer;

		public abstract void create(final ClassLoaderMock classLoaderMock) throws IOException;

		protected void init(final File file, final ConfigurationObserver observer) {
			this.file = file;
			this.observer = observer;
		}

	}

}
