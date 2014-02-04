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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import mockit.Mock;
import mockit.MockUp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.Configurator.WritingThreadData;
import org.pmw.tinylog.mocks.ClassLoaderMock;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.NullWriter;
import org.pmw.tinylog.util.StringListOutputStream;

/**
 * Tests for configurator.
 * 
 * @see Configurator
 */
public class ConfiguratorTest extends AbstractTest {

	private ClassLoaderMock classLoaderMock;

	/**
	 * Set up mock for class loader.
	 */
	@Before
	public final void init() {
		classLoaderMock = new ClassLoaderMock((URLClassLoader) ConfigurationObserverTest.class.getClassLoader());
	}

	/**
	 * Shutdown observer threads and tear down mock.
	 */
	@After
	public final void dispose() {
		Configurator.shutdownConfigurationObserver(true);
		classLoaderMock.tearDown();
		classLoaderMock.close();
	}

	/**
	 * Test creating configurations based on the default configuration.
	 */
	@Test
	public final void testDefault() {
		Configuration defaultConfiguration = Configurator.defaultConfig().create();
		assertEquals(LoggingLevel.INFO, defaultConfiguration.getLevel());
		assertFalse(defaultConfiguration.hasCustomLoggingLevels());
		assertThat(defaultConfiguration.getFormatPattern(), containsString("{message}"));
		assertEquals(Locale.getDefault(), defaultConfiguration.getLocale());
		assertNotNull(defaultConfiguration.getWriter());
		assertNull(defaultConfiguration.getWritingThread());
		assertThat(defaultConfiguration.getMaxStackTraceElements(), greaterThanOrEqualTo(-1));

		Configuration configuration = Configurator.defaultConfig().writer(null).formatPattern("TEST").create();
		assertNotSame(defaultConfiguration, configuration);
		assertEquals(defaultConfiguration.getLevel(), configuration.getLevel());
		assertEquals(defaultConfiguration.hasCustomLoggingLevels(), configuration.hasCustomLoggingLevels());
		assertEquals("TEST", configuration.getFormatPattern());
		assertEquals(defaultConfiguration.getLocale(), configuration.getLocale());
		assertNull(configuration.getWriter());
		assertEquals(defaultConfiguration.getMaxStackTraceElements(), configuration.getMaxStackTraceElements());
	}

	/**
	 * Test creating configurations based on the current configuration.
	 */
	@Test
	public final void testCurrent() {
		Configuration defaultConfiguration = Configurator.defaultConfig().create();

		Configuration configuration = Configurator.currentConfig().create();
		assertEquals(defaultConfiguration.getLevel(), configuration.getLevel());
		assertEquals(defaultConfiguration.hasCustomLoggingLevels(), configuration.hasCustomLoggingLevels());
		assertEquals(defaultConfiguration.getFormatPattern(), configuration.getFormatPattern());
		assertEquals(defaultConfiguration.getLocale(), configuration.getLocale());
		assertThat(configuration.getWriter(), instanceOf(defaultConfiguration.getWriter().getClass()));
		assertSame(defaultConfiguration.getWritingThread(), configuration.getWritingThread());
		assertEquals(defaultConfiguration.getMaxStackTraceElements(), configuration.getMaxStackTraceElements());

		Configurator.currentConfig().formatPattern("TEST").create();
		assertNotEquals("TEST", configuration.getFormatPattern());

		assertTrue(Configurator.currentConfig().formatPattern("TEST").activate());
		configuration = Configurator.currentConfig().create();
		assertEquals("TEST", configuration.getFormatPattern());
	}

	/**
	 * Test initialization of configuration.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testInit() throws IOException {
		int threadCount = Thread.activeCount();

		classLoaderMock.set("tinylog.properties", "tinylog.format=TEST1");
		Configuration configuration = Configurator.init().create();
		assertEquals("TEST1", configuration.getFormatPattern());

		System.setProperty("tinylog.format", "TEST2");
		configuration = Configurator.init().create();
		assertEquals("TEST2", configuration.getFormatPattern());

		System.clearProperty("tinylog.format");
		configuration = Configurator.init().create();
		assertEquals("TEST1", configuration.getFormatPattern());

		classLoaderMock.remove("tinylog.properties");
		configuration = Configurator.init().create();
		assertThat(configuration.getFormatPattern(), containsString("{message}"));

		classLoaderMock.set("tinylog.properties", "tinylog.format=TEST1");
		File file = FileHelper.createTemporaryFile("properties", "tinylog.format=TEST2");
		System.setProperty("tinylog.configuration", file.getAbsolutePath());
		configuration = Configurator.init().create();
		assertEquals("TEST2", configuration.getFormatPattern());
		System.clearProperty("tinylog.configuration");
		file.delete();

		assertEquals(threadCount, Thread.activeCount());
		classLoaderMock.set("tinylog.properties", "tinylog.format=TEST1", "tinylog.configuration.observe=true");
		configuration = Configurator.init().create();
		assertEquals("TEST1", configuration.getFormatPattern());
		assertEquals(threadCount + 1, Thread.activeCount());

		file = FileHelper.createTemporaryFile("properties", "tinylog.format=TEST3");
		System.setProperty("tinylog.configuration", file.getAbsolutePath());
		System.setProperty("tinylog.configuration.observe", "true");
		configuration = Configurator.init().create();
		assertEquals("TEST3", configuration.getFormatPattern());
		assertEquals(threadCount + 1, Thread.activeCount());
		System.clearProperty("tinylog.configuration");
		System.clearProperty("tinylog.configuration.observe");
		Configurator.shutdownConfigurationObserver(true);
		assertEquals(threadCount, Thread.activeCount());
		file.delete();
	}

	/**
	 * Test initialization with a nonexistent properties file.
	 */
	@Test
	public final void testInitWithNonexistentFile() {
		System.setProperty("tinylog.configuration", "invalid.properties");

		try {
			Configurator.init();
			String line = getErrorStream().nextLine();
			assertThat(line, allOf(containsString("ERROR"), containsString(FileNotFoundException.class.getName()), containsString("invalid.properties")));
		} finally {
			System.clearProperty("tinylog.configuration");
		}
	}

	/**
	 * Test initialization if reading of properties file failed.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testInitIfReadingFailed() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		System.setProperty("tinylog.configuration", file.getAbsolutePath());

		MockUp<Properties> mock = new MockUp<Properties>() {

			@Mock
			public void load(final InputStream inStream) throws IOException {
				throw new IOException();
			}

		};

		try {
			Configurator.init();
			String line = getErrorStream().nextLine();
			assertThat(line, allOf(containsString("ERROR"), containsString(IOException.class.getName()), containsString(file.getName())));
		} finally {
			System.clearProperty("tinylog.configuration");
			mock.tearDown();
		}
	}

	/**
	 * Test loading the configuration form a resource from classpath.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testLoadFromResource() throws IOException {
		classLoaderMock.set("my/package/tinylog.properties", "tinylog.format=TEST");
		Configuration configuration = Configurator.fromResource("my/package/tinylog.properties").create();
		assertEquals("TEST", configuration.getFormatPattern());

		classLoaderMock.remove("my/package/tinylog.properties");
		try {
			configuration = Configurator.fromResource("my/package/tinylog.properties").create();
			fail("FileNotFoundException expected");
		} catch (FileNotFoundException ex) {
			// Expected
		}
		assertEquals("TEST", configuration.getFormatPattern());
	}

	/**
	 * Test loading the configuration from a file.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testLoadFromFile() throws IOException {
		File file = FileHelper.createTemporaryFile("properties", "tinylog.format=TEST");
		Configuration configuration = Configurator.fromFile(file).create();
		assertEquals("TEST", configuration.getFormatPattern());

		assertTrue(file.delete());
		try {
			configuration = Configurator.fromFile(file).create();
			fail("FileNotFoundException expected");
		} catch (FileNotFoundException ex) {
			// Expected
		}
		assertEquals("TEST", configuration.getFormatPattern());
	}

	/**
	 * Test copying an configurator.
	 */
	@Test
	public final void testCopy() {
		Map<String, LoggingLevel> packageLevels = Collections.singletonMap("a", LoggingLevel.DEBUG);
		NullWriter writer = new NullWriter();
		WritingThreadData writingThreadData = new WritingThreadData(Thread.currentThread().getName(), Thread.NORM_PRIORITY);
		Configurator configurator = new Configurator(LoggingLevel.WARNING, packageLevels, "TEST", Locale.US, writer, writingThreadData, 42);

		Configuration copy = configurator.copy().create();
		assertEquals(LoggingLevel.WARNING, copy.getLevel());
		assertEquals(LoggingLevel.DEBUG, copy.getLevel("a"));
		assertEquals("TEST", copy.getFormatPattern());
		assertEquals(Locale.US, copy.getLocale());
		assertSame(writer, copy.getWriter());
		assertEquals(Thread.currentThread().getName(), copy.getWritingThread().getNameOfThreadToObserve());
		assertEquals(Thread.NORM_PRIORITY, copy.getWritingThread().getPriority());
		assertEquals(42, copy.getMaxStackTraceElements());
	}

	/**
	 * Test setting logging levels.
	 */
	@Test
	public final void testLevel() {
		Configuration configuration = Configurator.defaultConfig().level(LoggingLevel.TRACE).create();
		assertFalse(configuration.hasCustomLoggingLevels());
		assertEquals(LoggingLevel.TRACE, configuration.getLevel());

		configuration = Configurator.defaultConfig().level(null).create();
		assertFalse(configuration.hasCustomLoggingLevels());
		assertEquals(LoggingLevel.OFF, configuration.getLevel());

		configuration = Configurator.defaultConfig().level(LoggingLevel.ERROR).level("a", LoggingLevel.WARNING).create();
		assertTrue(configuration.hasCustomLoggingLevels());
		assertEquals(LoggingLevel.WARNING, configuration.getLevel("a"));

		configuration = Configurator.defaultConfig().level(LoggingLevel.ERROR).level("a", LoggingLevel.WARNING).level("a", null).create();
		assertFalse(configuration.hasCustomLoggingLevels());
		assertEquals(LoggingLevel.ERROR, configuration.getLevel("a"));

		configuration = Configurator.defaultConfig().level(LoggingLevel.ERROR).level("a", LoggingLevel.WARNING).resetCustomLevels().create();
		assertFalse(configuration.hasCustomLoggingLevels());
		assertEquals(LoggingLevel.ERROR, configuration.getLevel("a"));

		configuration = Configurator.defaultConfig().level(LoggingLevel.ERROR).level(ConfiguratorTest.class.getPackage(), LoggingLevel.WARNING).create();
		assertTrue(configuration.hasCustomLoggingLevels());
		assertEquals(LoggingLevel.WARNING, configuration.getLevel(ConfiguratorTest.class.getPackage().getName()));

		configuration = Configurator.defaultConfig().level(LoggingLevel.ERROR).level(ConfiguratorTest.class, LoggingLevel.WARNING).create();
		assertTrue(configuration.hasCustomLoggingLevels());
		assertEquals(LoggingLevel.WARNING, configuration.getLevel(ConfiguratorTest.class.getName()));
	}

	/**
	 * Test setting format patterns.
	 */
	@Test
	public final void testFormatPattern() {
		Configuration configuration = Configurator.defaultConfig().formatPattern("TEST").create();
		assertEquals("TEST", configuration.getFormatPattern());

		configuration = Configurator.defaultConfig().formatPattern(null).create();
		assertThat(configuration.getFormatPattern(), containsString("{message}"));
	}

	/**
	 * Test setting locales.
	 */
	@Test
	public final void testLocale() {
		Configuration configuration = Configurator.defaultConfig().locale(Locale.GERMANY).create();
		assertEquals(Locale.GERMANY, configuration.getLocale());

		configuration = Configurator.defaultConfig().locale(Locale.US).create();
		assertEquals(Locale.US, configuration.getLocale());

		configuration = Configurator.defaultConfig().locale(null).create();
		assertEquals(Locale.getDefault(), configuration.getLocale());
	}

	/**
	 * Test setting writers.
	 */
	@Test
	public final void testWriter() {
		Configuration configuration = Configurator.defaultConfig().writer(new NullWriter()).create();
		assertNotNull(configuration.getWriter());
		assertEquals(NullWriter.class, configuration.getWriter().getClass());

		configuration = Configurator.defaultConfig().writer(null).create();
		assertEquals(null, configuration.getWriter());

		StringListOutputStream errorStream = getErrorStream();
		assertFalse(errorStream.hasLines());
		assertFalse(Configurator.defaultConfig().writer(new NullWriter() {

			@Override
			public void init(final Configuration configuration) {
				throw new IllegalArgumentException();
			}

		}).activate());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString(IllegalArgumentException.class.getName()), containsString("activate")));
	}

	/**
	 * Test handling writing thread.
	 * 
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testWritingThread() throws InterruptedException {
		int threadCount = Thread.activeCount();

		Configurator configurator = Configurator.defaultConfig().writingThread(true);
		Configuration configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals("main", configuration.getWritingThread().getNameOfThreadToObserve());
		assertThat(configuration.getWritingThread().getPriority(), lessThan(Thread.NORM_PRIORITY));

		configurator = Configurator.defaultConfig().writingThread(Thread.MAX_PRIORITY);
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals("main", configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(Thread.MAX_PRIORITY, configuration.getWritingThread().getPriority());

		configurator = Configurator.defaultConfig().writingThread(Thread.currentThread().getName());
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals(Thread.currentThread().getName(), configuration.getWritingThread().getNameOfThreadToObserve());
		assertThat(configuration.getWritingThread().getPriority(), lessThan(Thread.NORM_PRIORITY));

		assertEquals(threadCount, Thread.activeCount());
		assertTrue(configurator.activate());
		assertEquals(threadCount + 1, Thread.activeCount());

		configurator = Configurator.defaultConfig().writingThread(false);
		configuration = configurator.create();
		assertEquals(null, configuration.getWritingThread());

		assertEquals(threadCount + 1, Thread.activeCount());
		assertTrue(configurator.activate());
		Thread.sleep(10L); // Wait for shutdown of writing thread
		assertEquals(threadCount, Thread.activeCount());

		configurator = Configurator.defaultConfig().writingThread("!!! NONEXISTING THREAD !!!");
		StringListOutputStream errorStream = getErrorStream();
		assertFalse(errorStream.hasLines());
		configuration = configurator.create();
		assertThat(errorStream.nextLine(), allOf(containsString("WARNING"), containsString("thread"), containsString("!!! NONEXISTING THREAD !!!")));
	}

	/**
	 * Test manual shutdown of writing thread.
	 * 
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testShutdownWritingThread() throws InterruptedException {
		int threadCount = Thread.activeCount();

		assertEquals(threadCount, Thread.activeCount());
		assertTrue(Configurator.defaultConfig().writingThread(null).activate());
		assertEquals(threadCount + 1, Thread.activeCount());
		Configurator.shutdownWritingThread(true);
		assertEquals(threadCount, Thread.activeCount());

		assertTrue(Configurator.defaultConfig().writingThread(null).activate());
		assertEquals(threadCount + 1, Thread.activeCount());
		Configurator.shutdownWritingThread(false);
		assertEquals(threadCount + 1, Thread.activeCount());
		Thread.sleep(100L); // Wait for shutdown of writing thread
		assertEquals(threadCount, Thread.activeCount());
	}

	/**
	 * Test setting limits of stack traces for exceptions.
	 */
	@Test
	public final void testMaxStackTraceElements() {
		Configuration configuration = Configurator.defaultConfig().maxStackTraceElements(100).create();
		assertEquals(100, configuration.getMaxStackTraceElements());

		configuration = Configurator.defaultConfig().maxStackTraceElements(0).create();
		assertEquals(0, configuration.getMaxStackTraceElements());

		configuration = Configurator.defaultConfig().maxStackTraceElements(-1).create();
		assertEquals(Integer.MAX_VALUE, configuration.getMaxStackTraceElements());
	}

	/**
	 * Tests for writing thread data.
	 * 
	 * @see WritingThreadData
	 */
	public static class WritingThreadDataTest {

		/**
		 * Test check if writing thread data covers an existing writing thread .
		 */
		@Test
		public final void testCovers() {
			Configurator.WritingThreadData writingThreadData = new Configurator.WritingThreadData(null, Thread.MIN_PRIORITY);
			assertTrue(writingThreadData.covers(new WritingThread(null, Thread.MIN_PRIORITY)));
			assertFalse(writingThreadData.covers(null));
			assertFalse(writingThreadData.covers(new WritingThread("", Thread.MIN_PRIORITY)));
			assertFalse(writingThreadData.covers(new WritingThread(null, Thread.MIN_PRIORITY + 1)));

			writingThreadData = new Configurator.WritingThreadData("main", Thread.MIN_PRIORITY);
			assertTrue(writingThreadData.covers(new WritingThread("main", Thread.MIN_PRIORITY)));
			assertFalse(writingThreadData.covers(null));
			assertFalse(writingThreadData.covers(new WritingThread(null, Thread.MIN_PRIORITY)));
			assertFalse(writingThreadData.covers(new WritingThread("main2", Thread.MIN_PRIORITY)));
			assertFalse(writingThreadData.covers(new WritingThread("main", Thread.MIN_PRIORITY + 1)));
		}

	}

}
