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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.Properties;

import mockit.Mockit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.util.MockClassLoader;
import org.pmw.tinylog.util.MockSystem;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.writers.ConsoleWriter;

/**
 * Tests for configurator.
 * 
 * @see Configurator
 */
public class ConfiguratorTest extends AbstractTest {

	private static final int DEFAULT_MAX_STACK_TRACE_ELEMENTS = 40;
	private static final String DEFAULT_FORMAT_PATTERN = "{date} [{thread}] {class}.{method}()\n{level}: {message}";
	private static final String DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD = "main";
	private static final int DEFAULT_PRIORITY_FOR_WRITING_THREAD = 2;

	private MockSystem mockSystem;
	private MockClassLoader mockClassLoader;

	/**
	 * Set up the mock for {@link System}.
	 */
	@Before
	public final void init() {
		Configurator.defaultConfig().activate();
		mockSystem = new MockSystem();
		mockClassLoader = new MockClassLoader();
		Mockit.setUpMocks(mockSystem, mockClassLoader);
	}

	/**
	 * Tear down the mock for {@link System}.
	 */
	@After
	public final void dispose() {
		Mockit.tearDownMocks(URLClassLoader.class, System.class);
		Configurator.defaultConfig().activate();
	}

	/**
	 * Test creating a configuration, based on the default configuration as well as on current configuration.
	 */
	@Test
	public final void testDefaultAndCurrent() {
		Configuration configuration = Configurator.defaultConfig().create();
		testDefault(configuration);

		configuration = Configurator.defaultConfig().writer(null).formatPattern("TEST").create();
		assertEquals(LoggingLevel.INFO, configuration.getLevel());
		assertEquals(LoggingLevel.OFF, configuration.getLowestPackageLevel());
		assertEquals("TEST", configuration.getFormatPattern());
		assertEquals(Locale.getDefault(), configuration.getLocale());
		assertNull(configuration.getWriter());
		assertEquals(DEFAULT_MAX_STACK_TRACE_ELEMENTS, configuration.getMaxStackTraceElements());

		configuration = Configurator.currentConfig().create();
		testDefault(configuration);

		Configurator.defaultConfig().formatPattern("TEST").activate();

		configuration = Configurator.currentConfig().create();
		assertEquals("TEST", configuration.getFormatPattern());

		configuration = Configurator.defaultConfig().create();
		testDefault(configuration);
	}

	/**
	 * Test reloading the configuration.
	 */
	@Test
	public final void testReload() {
		mockClassLoader.setContent("tinylog.properties", "tinylog.format=TEST1\n");
		Configuration configuration = Configurator.reload().create();
		assertEquals("TEST1", configuration.getFormatPattern());

		System.setProperty("tinylog.format", "TEST2");
		configuration = Configurator.reload().create();
		assertEquals("TEST2", configuration.getFormatPattern());

	}

	/**
	 * Test loading the configuration form a resource in classpath.
	 * 
	 * @throws IOException
	 *             Failed to load the resource
	 */
	@Test
	public final void testLoadFromResource() throws IOException {
		mockClassLoader.setContent("my/package/tinylog.properties", "tinylog.format=TEST\n");
		Configuration configuration = Configurator.fromResource("/my/package/tinylog.properties").create();
		assertEquals("TEST", configuration.getFormatPattern());
	}

	/**
	 * Test loading the configuration form a file.
	 * 
	 * @throws IOException
	 *             Failed to write or load the properties file
	 */
	@Test
	public final void testLoadFromFile() throws IOException {
		File file = File.createTempFile("temp", ".properties");
		file.deleteOnExit();

		Properties properties = new Properties();
		properties.setProperty("tinylog.format", "TEST");
		properties.store(new FileWriter(file), null);

		Configuration configuration = Configurator.fromFile(file).create();
		assertEquals("TEST", configuration.getFormatPattern());

		file.delete();
	}

	/**
	 * Test setting logging levels.
	 */
	@Test
	public final void testLevel() {
		Configuration configuration = Configurator.defaultConfig().level(LoggingLevel.TRACE).create();
		assertEquals(LoggingLevel.TRACE, configuration.getLevel());

		configuration = Configurator.defaultConfig().level(null).create();
		assertEquals(LoggingLevel.OFF, configuration.getLevel());

		configuration = Configurator.defaultConfig().level(LoggingLevel.ERROR).level("a", LoggingLevel.WARNING).create();
		assertEquals(LoggingLevel.WARNING, configuration.getLowestPackageLevel());
		assertEquals(LoggingLevel.WARNING, configuration.getLevelOfPackage("a"));

		configuration = Configurator.defaultConfig().level(LoggingLevel.ERROR).level("a", LoggingLevel.WARNING).level("a", null).create();
		assertEquals(LoggingLevel.OFF, configuration.getLowestPackageLevel());
		assertEquals(LoggingLevel.ERROR, configuration.getLevelOfPackage("a"));

		configuration = Configurator.defaultConfig().level(LoggingLevel.ERROR).level("a", LoggingLevel.WARNING).resetAllLevelsForPackages().create();
		assertEquals(LoggingLevel.OFF, configuration.getLowestPackageLevel());
		assertEquals(LoggingLevel.ERROR, configuration.getLevelOfPackage("a"));
	}

	/**
	 * Test setting format patterns.
	 */
	@Test
	public final void testFormatPattern() {
		Configuration configuration = Configurator.defaultConfig().formatPattern("TEST").create();
		assertEquals("TEST", configuration.getFormatPattern());

		configuration = Configurator.defaultConfig().formatPattern(null).create();
		assertEquals(DEFAULT_FORMAT_PATTERN, configuration.getFormatPattern());
	}

	/**
	 * Test setting locales.
	 */
	@Test
	public final void testLocal() {
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
		Configuration configuration = Configurator.defaultConfig().writer(new StoreWriter()).create();
		assertNotNull(configuration.getWriter());
		assertEquals(StoreWriter.class, configuration.getWriter().getClass());

		configuration = Configurator.defaultConfig().writer(null).create();
		assertEquals(null, configuration.getWriter());
	}

	/**
	 * Test handling writing thread.
	 */
	@Test
	public final void testWritingThread() {
		int threadCount = Thread.activeCount();

		Configurator configurator = Configurator.defaultConfig().writingThread(true);
		Configuration configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals(DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD, configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(DEFAULT_PRIORITY_FOR_WRITING_THREAD, configuration.getWritingThread().getPriority());

		configurator = Configurator.defaultConfig().writingThread(true, Thread.MAX_PRIORITY);
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals(DEFAULT_THREAD_TO_OBSERVE_BY_WRITING_THREAD, configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(Thread.MAX_PRIORITY, configuration.getWritingThread().getPriority());

		configurator = Configurator.defaultConfig().writingThread(true, Thread.currentThread().getName());
		configuration = configurator.create();
		assertNotNull(configuration.getWritingThread());
		assertEquals(Thread.currentThread().getName(), configuration.getWritingThread().getNameOfThreadToObserve());
		assertEquals(DEFAULT_PRIORITY_FOR_WRITING_THREAD, configuration.getWritingThread().getPriority());

		assertEquals(threadCount, Thread.activeCount());
		configurator.activate();
		assertEquals(threadCount + 1, Thread.activeCount());

		configurator = Configurator.defaultConfig().writingThread(false, "TITLE", Thread.MAX_PRIORITY);
		configuration = configurator.create();
		assertEquals(null, configuration.getWritingThread());

		assertEquals(threadCount + 1, Thread.activeCount());
		configurator.activate();
		Thread.yield();
		assertEquals(threadCount, Thread.activeCount());

		// Start writing thread again in order to test the method shutdownWritingThread()

		assertEquals(threadCount, Thread.activeCount());
		Configurator.defaultConfig().writingThread(true, null).activate();
		assertEquals(threadCount + 1, Thread.activeCount());

		assertEquals(threadCount + 1, Thread.activeCount());
		Configurator.shutdownWritingThread(true);
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

	private void testDefault(final Configuration configuration) {
		assertEquals(LoggingLevel.INFO, configuration.getLevel());
		assertEquals(LoggingLevel.OFF, configuration.getLowestPackageLevel());
		assertEquals(DEFAULT_FORMAT_PATTERN, configuration.getFormatPattern());
		assertEquals(Locale.getDefault(), configuration.getLocale());
		assertNotNull(configuration.getWriter());
		assertEquals(ConsoleWriter.class, configuration.getWriter().getClass());
		assertNull(configuration.getWritingThread());
		assertEquals(DEFAULT_MAX_STACK_TRACE_ELEMENTS, configuration.getMaxStackTraceElements());
	}

}
