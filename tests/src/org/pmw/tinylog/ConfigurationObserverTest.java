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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.mocks.ClassLoaderMock;
import org.pmw.tinylog.mocks.SleepHandledThreadMock;
import org.pmw.tinylog.util.FileHelper;

/**
 * Tests for configuration observer.
 * 
 * @see ConfigurationObserver
 */
public class ConfigurationObserverTest extends AbstractTest {

	private SleepHandledThreadMock threadMock;
	private ClassLoaderMock classLoaderMock;

	/**
	 * Set up mocks for thread and class loader.
	 */
	@Before
	public final void init() {
		threadMock = new SleepHandledThreadMock();
		classLoaderMock = new ClassLoaderMock((URLClassLoader) ConfigurationObserverTest.class.getClassLoader());
	}

	/**
	 * Tear down mocks.
	 */
	@After
	public final void dispose() {
		classLoaderMock.tearDown();
		classLoaderMock.close();
		threadMock.tearDown();
	}

	/**
	 * Test {@code ConfigurationObserver#createFileConfigurationObserver(Configurator, String)}.
	 * 
	 * @throws IOException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testFileConfigurationObserver() throws IOException, InterruptedException {
		Properties properties = new Properties();

		Properties systemProperties = System.getProperties();
		for (String key : systemProperties.stringPropertyNames()) {
			if (key.startsWith("tinylog.")) {
				properties.put(key, systemProperties.getProperty(key));
			}
		}

		File file = FileHelper.createTemporaryFile("properties");
		ConfigurationObserver observer = ConfigurationObserver
				.createFileConfigurationObserver(Configurator.defaultConfig(), properties, file.getAbsolutePath());
		testObserver(observer, file);
		file.delete();
	}

	/**
	 * Test {@code ConfigurationObserver#createResourceConfigurationObserver(Configurator, String)}.
	 * 
	 * @throws IOException
	 *             Test failed
	 * @throws NoSuchMethodException
	 *             Test failed
	 * @throws IllegalAccessException
	 *             Test failed
	 * @throws InvocationTargetException
	 *             Test failed
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testResourceConfigurationObserver() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
			InterruptedException {
		Properties properties = new Properties();

		Properties systemProperties = System.getProperties();
		for (String key : systemProperties.stringPropertyNames()) {
			if (key.startsWith("tinylog.")) {
				properties.put(key, systemProperties.getProperty(key));
			}
		}

		File file = classLoaderMock.set("config/tinylog.properties");
		ConfigurationObserver observer = ConfigurationObserver.createResourceConfigurationObserver(Configurator.defaultConfig(), properties,
				"config/tinylog.properties");
		testObserver(observer, file);
		file.delete();
	}

	private void testObserver(final ConfigurationObserver observer, final File file) throws IOException, InterruptedException {
		Configuration defaultConfiguration = Configurator.defaultConfig().create();
		String loggingFormat = defaultConfiguration.getFormatPattern();
		int maxStackTraceElements = defaultConfiguration.getMaxStackTraceElements();

		assertThat(observer.getPriority(), lessThan(Thread.NORM_PRIORITY));
		assertTrue(observer.isDaemon());

		int threadCount = Thread.activeCount();
		observer.start();
		assertTrue(observer.isAlive());
		assertEquals(threadCount + 1, Thread.activeCount());

		try {
			threadMock.waitForSleep();
			Configuration currentConfiguration = Logger.getConfiguration().create();
			assertEquals(loggingFormat, currentConfiguration.getFormatPattern());
			assertEquals(maxStackTraceElements, currentConfiguration.getMaxStackTraceElements());

			FileHelper.write(file, "tinylog.format=TEST");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals("TEST", currentConfiguration.getFormatPattern());
			assertEquals(maxStackTraceElements, currentConfiguration.getMaxStackTraceElements());

			FileHelper.write(file, "tinylog.stacktrace=42");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(loggingFormat, currentConfiguration.getFormatPattern());
			assertEquals(42, currentConfiguration.getMaxStackTraceElements());

			FileHelper.write(file, "");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals(loggingFormat, currentConfiguration.getFormatPattern());
			assertEquals(maxStackTraceElements, currentConfiguration.getMaxStackTraceElements());

			FileHelper.write(file, "tinylog.format=TEST", "tinylog.stacktrace=42");
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals("TEST", currentConfiguration.getFormatPattern());
			assertEquals(42, currentConfiguration.getMaxStackTraceElements());

			file.delete();
			threadMock.awake();
			threadMock.waitForSleep();
			currentConfiguration = Logger.getConfiguration().create();
			assertEquals("TEST", currentConfiguration.getFormatPattern());
			assertEquals(42, currentConfiguration.getMaxStackTraceElements());
		} finally {
			threadMock.disable();

			observer.shutdown();
			observer.join();
			assertFalse(observer.isAlive());
			assertEquals(threadCount, Thread.activeCount());
		}
	}

}
