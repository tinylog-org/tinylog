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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
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
import org.pmw.tinylog.mocks.SleepHandledThreadMock;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.writers.FileWriter;
import org.pmw.tinylog.writers.LogcatWriter;
import org.pmw.tinylog.writers.LoggingWriter;

/**
 * Tests for configuration observer.
 * 
 * @see ConfigurationObserver
 */
@RunWith(Parameterized.class)
public class ConfigurationObserverTest extends AbstractTest {

	private static final Configuration DEFAULT_CONFIGURATION = Configurator.defaultConfig().create();

	private SleepHandledThreadMock threadMock;
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
		threadMock = new SleepHandledThreadMock();
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
		classLoaderMock.tearDown();
		classLoaderMock.close();
		threadMock.tearDown();
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

		threadMock.disable();
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
			threadMock.waitForSleep();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getLevel(), currentConfiguration.getLevel());
			assertEquals(DEFAULT_CONFIGURATION.getFormatPattern(), currentConfiguration.getFormatPattern());
			assertEquals(DEFAULT_CONFIGURATION.getLocale(), currentConfiguration.getLocale());
			assertEquals(DEFAULT_CONFIGURATION.getMaxStackTraceElements(), currentConfiguration.getMaxStackTraceElements());
			assertThat(currentConfiguration.getWriter(), instanceOf(DEFAULT_CONFIGURATION.getWriter().getClass()));
			assertSame(DEFAULT_CONFIGURATION.getWritingThread(), currentConfiguration.getWritingThread());
		} finally {
			threadMock.disable();
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
		threadMock.waitForSleep();

		try {
			/* Test new logging level */

			FileHelper.write(file, "tinylog.level=ERROR");
			threadMock.awake();
			threadMock.waitForSleep();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(LoggingLevel.ERROR, currentConfiguration.getLevel());

			/* Test remove logging level */

			FileHelper.write(file, "");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getLevel(), currentConfiguration.getLevel());

			/* Test new custom logging level */

			FileHelper.write(file, "tinylog.level@" + ConfigurationObserverTest.class.getName() + "=TRACE");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(LoggingLevel.TRACE, currentConfiguration.getLevel(ConfigurationObserverTest.class.getName()));

			/* Test remove custom logging level */

			FileHelper.write(file, "");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getLevel(), currentConfiguration.getLevel());
		} finally {
			threadMock.disable();
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
		threadMock.waitForSleep();

		try {
			/* Test new format pattern */

			FileHelper.write(file, "tinylog.format={thread} -> {message}");
			threadMock.awake();
			threadMock.waitForSleep();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals("{thread} -> {message}", currentConfiguration.getFormatPattern());

			/* Test remove format pattern */

			FileHelper.write(file, "");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getFormatPattern(), currentConfiguration.getFormatPattern());
		} finally {
			threadMock.disable();
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
		threadMock.waitForSleep();

		try {
			/* Test new locale */

			FileHelper.write(file, "tinylog.locale=de_US");
			threadMock.awake();
			threadMock.waitForSleep();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(new Locale("de", "US"), currentConfiguration.getLocale());

			/* Test remove locale */

			FileHelper.write(file, "");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getLocale(), currentConfiguration.getLocale());
		} finally {
			threadMock.disable();
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
		threadMock.waitForSleep();

		try {
			/* Test new max stack trace elements number */

			FileHelper.write(file, "tinylog.stacktrace=42");
			threadMock.awake();
			threadMock.waitForSleep();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(42, currentConfiguration.getMaxStackTraceElements());

			/* Test remove max stack trace elements number */

			FileHelper.write(file, "");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(DEFAULT_CONFIGURATION.getMaxStackTraceElements(), currentConfiguration.getMaxStackTraceElements());
		} finally {
			threadMock.disable();
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
		observer.start();
		threadMock.waitForSleep();

		try {
			/* Test new writer without arguments */

			FileHelper.write(file, "tinylog.writer=logcat");
			threadMock.awake();
			threadMock.waitForSleep();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertThat(currentConfiguration.getWriter(), instanceOf(LogcatWriter.class));

			/* Test remove writer */

			FileHelper.write(file, "");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertThat(currentConfiguration.getWriter(), instanceOf(DEFAULT_CONFIGURATION.getWriter().getClass()));

			/* Test new writer with arguments */

			FileHelper.write(file, "tinylog.writer=file", "tinylog.writer.filename=log1.txt");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			LoggingWriter writer = currentConfiguration.getWriter();
			assertThat(writer, instanceOf(FileWriter.class));
			FileWriter fileWriter = (FileWriter) writer;
			assertEquals("log1.txt", fileWriter.getFilename());
			fileWriter.close();
			new File(fileWriter.getFilename()).delete();

			/* Argument of writer has changed */

			FileHelper.write(file, "tinylog.writer=file", "tinylog.writer.filename=log2.txt");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			writer = currentConfiguration.getWriter();
			assertThat(writer, instanceOf(FileWriter.class));
			fileWriter = (FileWriter) writer;
			assertEquals("log2.txt", fileWriter.getFilename());
			fileWriter.close();
			new File(fileWriter.getFilename()).delete();
		} finally {
			threadMock.disable();
			observer.shutdown();
			observer.join();
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
			for (int i = 0; i < 100; ++i) { // Make sure that configuration observer and writing thread can run
				threadMock.awake();
				threadMock.waitForSleep();
			}
			Configuration currentConfiguration = Logger.getConfiguration().create();
			WritingThread writingThread = currentConfiguration.getWritingThread();
			assertNotNull(writingThread);
			assertEquals(9, writingThread.getPriority());

			/* Test remove writing thread */

			FileHelper.write(file, "");
			for (int i = 0; i < 100; ++i) { // Make sure that configuration observer and writing thread can run
				threadMock.awake();
				threadMock.waitForSleep();
			}
			currentConfiguration = Logger.getConfiguration().create();
			assertSame(DEFAULT_CONFIGURATION.getWritingThread(), currentConfiguration.getWritingThread());
		} finally {
			threadMock.disable();
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
		threadMock.waitForSleep();

		try {
			/* Without system properties */

			FileHelper.write(file, "tinylog.level=ERROR");
			threadMock.awake();
			threadMock.waitForSleep();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(LoggingLevel.ERROR, currentConfiguration.getLevel());

			/* Overriding properties by system properties. */

			System.setProperty("tinylog.level", "TRACE");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(LoggingLevel.TRACE, currentConfiguration.getLevel());

			/* Without system properties */

			System.clearProperty("tinylog.level");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(LoggingLevel.ERROR, currentConfiguration.getLevel());
		} finally {
			threadMock.disable();
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
		threadMock.awake();
		threadMock.waitForSleep();
		observer.shutdown();
		threadMock.disable();
		observer.join();

		Configuration currentConfiguration = Logger.getConfiguration().create();
		assertThat(getErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("open"), containsString(".properties")));
		assertEquals(DEFAULT_CONFIGURATION.getLevel(), currentConfiguration.getLevel());
		assertEquals(DEFAULT_CONFIGURATION.getFormatPattern(), currentConfiguration.getFormatPattern());
		assertEquals(DEFAULT_CONFIGURATION.getLocale(), currentConfiguration.getLocale());
		assertEquals(DEFAULT_CONFIGURATION.getMaxStackTraceElements(), currentConfiguration.getMaxStackTraceElements());
		assertThat(currentConfiguration.getWriter(), instanceOf(DEFAULT_CONFIGURATION.getWriter().getClass()));
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
		MockUp<Properties> mock = new MockUp<Properties>() {

			@Mock
			public void load(final InputStream inStream) throws IOException {
				throw new IOException();
			}

		};

		try {
			observer.start();
			threadMock.awake();
			threadMock.waitForSleep();
			observer.shutdown();
			threadMock.disable();
			observer.join();

			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertThat(getErrorStream().nextLine(), allOf(containsString("ERROR"), containsString("read"), containsString(IOException.class.getName())));
			assertEquals(DEFAULT_CONFIGURATION.getLevel(), currentConfiguration.getLevel());
			assertEquals(DEFAULT_CONFIGURATION.getFormatPattern(), currentConfiguration.getFormatPattern());
			assertEquals(DEFAULT_CONFIGURATION.getLocale(), currentConfiguration.getLocale());
			assertEquals(DEFAULT_CONFIGURATION.getMaxStackTraceElements(), currentConfiguration.getMaxStackTraceElements());
			assertThat(currentConfiguration.getWriter(), instanceOf(DEFAULT_CONFIGURATION.getWriter().getClass()));
			assertSame(DEFAULT_CONFIGURATION.getWritingThread(), currentConfiguration.getWritingThread());
		} finally {
			mock.tearDown();
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
