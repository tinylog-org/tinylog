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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.not;
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
import static org.pmw.tinylog.hamcrest.ArrayMatchers.containsCollectionWithSizes;
import static org.pmw.tinylog.hamcrest.ArrayMatchers.distinctContentInArray;
import static org.pmw.tinylog.hamcrest.ArrayMatchers.sameContentInArray;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.sameContent;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.sameTypes;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.types;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.Configurator.WritingThreadData;
import org.pmw.tinylog.mocks.ClassLoaderMock;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.NullWriter;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.RollingFileWriter;
import org.pmw.tinylog.writers.Writer;

import mockit.Mock;
import mockit.MockUp;

/**
 * Tests for configurator.
 *
 * @see Configurator
 */
public class ConfiguratorTest extends AbstractTinylogTest {

	private ClassLoaderMock classLoaderMock;

	/**
	 * Set up mock for class loader.
	 */
	@Before
	public final void init() {
		classLoaderMock = new ClassLoaderMock(ConfigurationObserverTest.class.getClassLoader());
	}

	/**
	 * Shutdown observer threads and tear down mock.
	 */
	@After
	public final void dispose() {
		Configurator.shutdownConfigurationObserver(true);
		classLoaderMock.close();
	}

	/**
	 * Test creating configurations based on the default configuration.
	 */
	@Test
	public final void testDefault() {
		Configuration defaultConfiguration = Configurator.defaultConfig().create();
		assertEquals(Level.INFO, defaultConfiguration.getLevel());
		assertFalse(defaultConfiguration.hasCustomLevels());
		assertThat(defaultConfiguration.getFormatPattern(), containsString("{message}"));
		assertEquals(Locale.getDefault(), defaultConfiguration.getLocale());
		assertThat(defaultConfiguration.getWriters(), not(empty()));
		assertNull(defaultConfiguration.getWritingThread());
		assertThat(defaultConfiguration.getMaxStackTraceElements(), greaterThanOrEqualTo(-1));

		Configuration configuration = Configurator.defaultConfig().writer(null).formatPattern("TEST").create();
		assertNotSame(defaultConfiguration, configuration);
		assertEquals(defaultConfiguration.getLevel(), configuration.getLevel());
		assertEquals(defaultConfiguration.hasCustomLevels(), configuration.hasCustomLevels());
		assertEquals("TEST", configuration.getFormatPattern());
		assertEquals(defaultConfiguration.getLocale(), configuration.getLocale());
		assertThat(configuration.getWriters(), empty());
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
		assertEquals(defaultConfiguration.hasCustomLevels(), configuration.hasCustomLevels());
		assertEquals(defaultConfiguration.getFormatPattern(), configuration.getFormatPattern());
		assertEquals(defaultConfiguration.getLocale(), configuration.getLocale());
		assertThat(configuration.getWriters(), sameTypes(defaultConfiguration.getWriters()));
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

		file = FileHelper.createTemporaryFile("properties", "tinylog.format=TEST4");
		URL url = file.toURI().toURL();
		System.setProperty("tinylog.configuration", url.toString());
		configuration = Configurator.init().create();
		System.clearProperty("tinylog.configuration");
		assertEquals("TEST4", configuration.getFormatPattern());
		file.delete();

		file = FileHelper.createTemporaryFile("properties", "tinylog.format=TEST5");
		url = file.toURI().toURL();
		System.setProperty("tinylog.configuration", url.toString());
		System.setProperty("tinylog.configuration.observe", "true");
		configuration = Configurator.init().create();
		assertEquals("TEST5", configuration.getFormatPattern());
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
		Configurator.init();
		String line = getErrorStream().nextLine();
		assertThat(line, allOf(containsString("ERROR"), containsString(FileNotFoundException.class.getName()), containsString("invalid.properties")));
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

		new MockUp<Properties>() {
			@Mock
			public void load(final InputStream inStream) throws IOException {
				throw new IOException();
			}
		};

		Configurator.init();
		String line = getErrorStream().nextLine();
		assertThat(line, allOf(containsString("ERROR"), containsString(IOException.class.getName()), containsString(file.getName())));
	}

	
	/**
	 * Test initialization with an invalid URL.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testInitWithInvalidURL() throws IOException {
		System.setProperty("tinylog.configuration", "xy:/");
		
		Configurator.init();
		String line = getErrorStream().nextLine();
		assertThat(line, allOf(containsString("ERROR"), containsString(MalformedURLException.class.getName()), containsString("xy:/")));

		System.clearProperty("tinylog.configuration");
	}

	/**
	 * Test initialization with a not accessible URL.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testInitWithNotAccessibleURL() throws IOException {
		new MockUp<URL>() {
			@Mock
			public InputStream openStream() throws UnknownHostException {
				throw new UnknownHostException(); // Throw it throw mock for performance reasons
			}
		};
		
		System.setProperty("tinylog.configuration", "http://test.invalid");
		
		Configurator.init();
		String line = getErrorStream().nextLine();
		assertThat(line, allOf(containsString("ERROR"), containsString(UnknownHostException.class.getName()), containsString("http://test.invalid")));

		System.clearProperty("tinylog.configuration");
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
	 * Test loading the configuration from an input stream.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testLoadFromStream() throws IOException {		
		InputStream stream = new ByteArrayInputStream("tinylog.format=TEST".getBytes());
		Configuration configuration = Configurator.fromStream(stream).create();
		assertEquals("TEST", configuration.getFormatPattern());
		
		try {
			configuration = Configurator.fromStream(null).create();
			fail("FileNotFoundException expected");
		} catch (FileNotFoundException ex) {
			// Expected
		}
		assertEquals("TEST", configuration.getFormatPattern());
	}

	/**
	 * Test loading the configuration from a URL.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testLoadFromURL() throws IOException {
		classLoaderMock.set("my/package/tinylog.properties", "tinylog.format=TEST");
		URL url = getClass().getClassLoader().getResource("my/package/tinylog.properties");
		Configuration configuration = Configurator.fromURL(url).create();
		assertEquals("TEST", configuration.getFormatPattern());

		classLoaderMock.remove("my/package/tinylog.properties");
		try {
			configuration = Configurator.fromURL(url).create();
			fail("FileNotFoundException expected");
		} catch (FileNotFoundException ex) {
			// Expected
		}
		assertEquals("TEST", configuration.getFormatPattern());
	}

	/**
	 * Test loading the configuration from a map.
	 */
	@Test
	public final void testLoadFromMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("tinylog.format", null);
		map.put("tinylog.writer", "rollingfile");
		map.put("tinylog.writer.filename", "test.log");
		map.put("tinylog.writer.backups", 42);

		Configuration configuration = Configurator.fromMap(map).create();

		assertEquals(Configurator.defaultConfig().create().getFormatPattern(), configuration.getFormatPattern());
		assertThat(configuration.getWriters(), types(RollingFileWriter.class));

		RollingFileWriter writer = (RollingFileWriter) configuration.getWriters().get(0);
		assertEquals("test.log", writer.getFilename());
		assertEquals(42, writer.getNumberOfBackups());
	}

	/**
	 * Test copying an configurator.
	 */
	@Test
	public final void testCopy() {
		Map<String, Level> packageLevels = Collections.singletonMap("a", Level.DEBUG);
		Writer writer = new NullWriter();
		WritingThreadData writingThreadData = new WritingThreadData(Thread.currentThread().getName(), Thread.NORM_PRIORITY);
		List<WriterDefinition> writerDefinitions = Collections.singletonList(new WriterDefinition(writer));
		Configurator configurator = new Configurator(Level.WARNING, packageLevels, "TEST", Locale.US, writerDefinitions, writingThreadData, 42);

		Configuration copy = configurator.copy().create();
		assertEquals(Level.WARNING, copy.getLevel());
		assertEquals(Level.DEBUG, copy.getLevel("a"));
		assertEquals("TEST", copy.getFormatPattern());
		assertEquals(Locale.US, copy.getLocale());
		assertThat(copy.getWriters(), sameContent(writer));
		assertEquals(Thread.currentThread().getName(), copy.getWritingThread().getNameOfThreadToObserve());
		assertEquals(Thread.NORM_PRIORITY, copy.getWritingThread().getPriority());
		assertEquals(42, copy.getMaxStackTraceElements());
	}

	/**
	 * Test setting logging levels.
	 */
	@Test
	public final void testLevel() {
		Configuration configuration = Configurator.defaultConfig().level(Level.TRACE).create();
		assertFalse(configuration.hasCustomLevels());
		assertEquals(Level.TRACE, configuration.getLevel());

		configuration = Configurator.defaultConfig().level(null).create();
		assertFalse(configuration.hasCustomLevels());
		assertEquals(Level.INFO, configuration.getLevel());

		configuration = Configurator.defaultConfig().level(Level.ERROR).level("a", Level.WARNING).create();
		assertTrue(configuration.hasCustomLevels());
		assertEquals(Level.WARNING, configuration.getLevel("a"));

		configuration = Configurator.defaultConfig().level(Level.ERROR).level("a", Level.WARNING).level("a", null).create();
		assertFalse(configuration.hasCustomLevels());
		assertEquals(Level.ERROR, configuration.getLevel("a"));

		configuration = Configurator.defaultConfig().level(Level.ERROR).level("a", Level.WARNING).resetCustomLevels().create();
		assertFalse(configuration.hasCustomLevels());
		assertEquals(Level.ERROR, configuration.getLevel("a"));

		configuration = Configurator.defaultConfig().level(Level.ERROR).level(ConfiguratorTest.class.getPackage(), Level.WARNING).create();
		assertTrue(configuration.hasCustomLevels());
		assertEquals(Level.WARNING, configuration.getLevel(ConfiguratorTest.class.getPackage().getName()));

		configuration = Configurator.defaultConfig().level(Level.ERROR).level(ConfiguratorTest.class, Level.WARNING).create();
		assertTrue(configuration.hasCustomLevels());
		assertEquals(Level.WARNING, configuration.getLevel(ConfiguratorTest.class.getName()));
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
	public final void testWriters() {
		Configurator configurator = Configurator.defaultConfig();
		NullWriter nullWriter1 = new NullWriter();
		NullWriter nullWriter2 = new NullWriter();
		NullWriter nullWriter3 = new NullWriter();

		Configuration configuration = configurator.writer(nullWriter1).create();
		assertThat(configuration.getWriters(), sameContent(nullWriter1));

		configuration = configurator.writer(null).create();
		assertThat(configuration.getWriters(), empty());

		configuration = configurator.writer(nullWriter1).addWriter(nullWriter2).create();
		assertThat(configuration.getWriters(), sameContent(nullWriter1, nullWriter2));

		configuration = configurator.addWriter(nullWriter3).create();
		assertThat(configuration.getWriters(), sameContent(nullWriter1, nullWriter2, nullWriter3));

		try {
			configuration = configurator.addWriter(null).create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(nullWriter1, nullWriter2, nullWriter3));
		}

		configuration = configurator.removeWriter(nullWriter2).create();
		assertThat(configuration.getWriters(), sameContent(nullWriter1, nullWriter3));

		configuration = configurator.removeAllWriters().create();
		assertThat(configuration.getWriters(), empty());

		assertFalse(Configurator.defaultConfig().writer(new NullWriter() {

			@Override
			public void init(final Configuration configuration) {
				throw new IllegalArgumentException();
			}

		}).activate());
		assertEquals("LOGGER ERROR: Failed to activate configuration (" + IllegalArgumentException.class.getName() + ")", getErrorStream().nextLine());
	}

	/**
	 * Test setting writers with specified severity level.
	 */
	@Test
	public final void testWritersWithLevel() {
		Configurator configurator = Configurator.defaultConfig();
		NullWriter nullWriter1 = new NullWriter();
		NullWriter nullWriter2 = new NullWriter();
		NullWriter nullWriter3 = new NullWriter();

		Configuration configuration = configurator.writer(nullWriter1, Level.INFO).create();
		assertThat(configuration.getWriters(), sameContent(nullWriter1));
		assertThat(configuration.getEffectiveWriters(Level.TRACE), emptyArray());
		assertThat(configuration.getEffectiveWriters(Level.DEBUG), emptyArray());
		assertThat(configuration.getEffectiveWriters(Level.INFO), sameContentInArray(nullWriter1));
		assertThat(configuration.getEffectiveWriters(Level.WARNING), sameContentInArray(nullWriter1));
		assertThat(configuration.getEffectiveWriters(Level.ERROR), sameContentInArray(nullWriter1));

		try {
			configuration = configurator.writer(null, Level.ERROR).create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(nullWriter1));
		}

		try {
			configuration = configurator.writer(new NullWriter(), (Level) null).create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(nullWriter1));
		}

		configuration = configurator.writer(nullWriter1, Level.DEBUG).addWriter(nullWriter2, Level.WARNING).create();
		assertThat(configuration.getWriters(), sameContent(nullWriter1, nullWriter2));
		assertThat(configuration.getEffectiveWriters(Level.TRACE), emptyArray());
		assertThat(configuration.getEffectiveWriters(Level.DEBUG), sameContentInArray(nullWriter1));
		assertThat(configuration.getEffectiveWriters(Level.INFO), sameContentInArray(nullWriter1));
		assertThat(configuration.getEffectiveWriters(Level.WARNING), sameContentInArray(nullWriter1, nullWriter2));
		assertThat(configuration.getEffectiveWriters(Level.ERROR), sameContentInArray(nullWriter1, nullWriter2));

		configuration = configurator.addWriter(nullWriter3, Level.INFO).create();
		assertThat(configuration.getWriters(), sameContent(nullWriter1, nullWriter2, nullWriter3));
		assertThat(configuration.getEffectiveWriters(Level.TRACE), emptyArray());
		assertThat(configuration.getEffectiveWriters(Level.DEBUG), sameContentInArray(nullWriter1));
		assertThat(configuration.getEffectiveWriters(Level.INFO), sameContentInArray(nullWriter1, nullWriter3));
		assertThat(configuration.getEffectiveWriters(Level.WARNING), sameContentInArray(nullWriter1, nullWriter2, nullWriter3));
		assertThat(configuration.getEffectiveWriters(Level.ERROR), sameContentInArray(nullWriter1, nullWriter2, nullWriter3));

		try {
			configuration = configurator.addWriter(null, Level.ERROR).create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(nullWriter1, nullWriter2, nullWriter3));
		}

		try {
			configuration = configurator.addWriter(new NullWriter(), (Level) null).create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(nullWriter1, nullWriter2, nullWriter3));
		}

		configuration = configurator.removeWriter(nullWriter2).create();
		assertThat(configuration.getWriters(), sameContent(nullWriter1, nullWriter3));

		configuration = configurator.removeAllWriters().create();
		assertThat(configuration.getWriters(), empty());
	}

	/**
	 * Test setting writers with specified format pattern.
	 */
	@Test
	public final void testWritersWithFormatPattern() {
		Configurator configurator = Configurator.defaultConfig();
		ConsoleWriter consoleWriter1 = new ConsoleWriter();
		ConsoleWriter consoleWriter2 = new ConsoleWriter();
		ConsoleWriter consoleWriter3 = new ConsoleWriter();

		Configuration configuration = configurator.writer(consoleWriter1, "123").create();
		assertThat(configuration.getEffectiveFormatTokens(Level.TRACE), containsCollectionWithSizes(1));
		assertThat(configuration.getEffectiveFormatTokens(Level.DEBUG), containsCollectionWithSizes(1));
		assertThat(configuration.getEffectiveFormatTokens(Level.INFO), containsCollectionWithSizes(1));
		assertThat(configuration.getEffectiveFormatTokens(Level.WARNING), containsCollectionWithSizes(1));
		assertThat(configuration.getEffectiveFormatTokens(Level.ERROR), containsCollectionWithSizes(1));

		try {
			configuration = configurator.writer(null, "").create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(consoleWriter1));
		}

		try {
			configuration = configurator.writer(new NullWriter(), (String) null).create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(consoleWriter1));
		}

		configuration = configurator.writer(consoleWriter1, "abc").addWriter(consoleWriter2, "xyz").create();
		assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter2));
		assertThat(configuration.getEffectiveFormatTokens(Level.TRACE), allOf(containsCollectionWithSizes(1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveFormatTokens(Level.DEBUG), allOf(containsCollectionWithSizes(1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveFormatTokens(Level.INFO), allOf(containsCollectionWithSizes(1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveFormatTokens(Level.WARNING), allOf(containsCollectionWithSizes(1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveFormatTokens(Level.ERROR), allOf(containsCollectionWithSizes(1, 1), distinctContentInArray()));

		configuration = configurator.addWriter(consoleWriter3, "123").create();
		assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter2, consoleWriter3));
		assertThat(configuration.getEffectiveFormatTokens(Level.TRACE), allOf(containsCollectionWithSizes(1, 1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveFormatTokens(Level.DEBUG), allOf(containsCollectionWithSizes(1, 1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveFormatTokens(Level.INFO), allOf(containsCollectionWithSizes(1, 1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveFormatTokens(Level.WARNING), allOf(containsCollectionWithSizes(1, 1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveFormatTokens(Level.ERROR), allOf(containsCollectionWithSizes(1, 1, 1), distinctContentInArray()));

		try {
			configuration = configurator.addWriter(null, "").create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter2, consoleWriter3));
		}

		try {
			configuration = configurator.addWriter(new NullWriter(), (String) null).create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter2, consoleWriter3));
		}

		configuration = configurator.removeWriter(consoleWriter2).create();
		assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter3));

		configuration = configurator.removeAllWriters().create();
		assertThat(configuration.getWriters(), empty());
	}

	/**
	 * Test setting writers with specified severity level and format pattern.
	 */
	@Test
	public final void testWritersWithLevelAndFormatPattern() {
		Configurator configurator = Configurator.defaultConfig();
		ConsoleWriter consoleWriter1 = new ConsoleWriter();
		ConsoleWriter consoleWriter2 = new ConsoleWriter();
		ConsoleWriter consoleWriter3 = new ConsoleWriter();

		Configuration configuration = configurator.writer(consoleWriter1, Level.INFO, "123").create();
		assertThat(configuration.getWriters(), sameContent(consoleWriter1));
		assertThat(configuration.getEffectiveWriters(Level.TRACE), emptyArray());
		assertThat(configuration.getEffectiveFormatTokens(Level.TRACE), emptyArray());
		assertThat(configuration.getEffectiveWriters(Level.DEBUG), emptyArray());
		assertThat(configuration.getEffectiveFormatTokens(Level.DEBUG), emptyArray());
		assertThat(configuration.getEffectiveWriters(Level.INFO), sameContentInArray(consoleWriter1));
		assertThat(configuration.getEffectiveFormatTokens(Level.INFO), containsCollectionWithSizes(1));
		assertThat(configuration.getEffectiveWriters(Level.WARNING), sameContentInArray(consoleWriter1));
		assertThat(configuration.getEffectiveFormatTokens(Level.WARNING), containsCollectionWithSizes(1));
		assertThat(configuration.getEffectiveWriters(Level.ERROR), sameContentInArray(consoleWriter1));
		assertThat(configuration.getEffectiveFormatTokens(Level.ERROR), containsCollectionWithSizes(1));

		try {
			configuration = configurator.writer(null, Level.ERROR, "").create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(consoleWriter1));
		}

		try {
			configuration = configurator.writer(new NullWriter(), null, "").create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(consoleWriter1));
		}

		try {
			configuration = configurator.writer(new NullWriter(), Level.ERROR, null).create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(consoleWriter1));
		}

		configuration = configurator.writer(consoleWriter1, Level.DEBUG, "abc").addWriter(consoleWriter2, Level.WARNING, "xyz").create();
		assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter2));
		assertThat(configuration.getEffectiveWriters(Level.TRACE), emptyArray());
		assertThat(configuration.getEffectiveFormatTokens(Level.TRACE), emptyArray());
		assertThat(configuration.getEffectiveWriters(Level.DEBUG), sameContentInArray(consoleWriter1));
		assertThat(configuration.getEffectiveFormatTokens(Level.DEBUG), containsCollectionWithSizes(1));
		assertThat(configuration.getEffectiveWriters(Level.INFO), sameContentInArray(consoleWriter1));
		assertThat(configuration.getEffectiveFormatTokens(Level.INFO), containsCollectionWithSizes(1));
		assertThat(configuration.getEffectiveWriters(Level.WARNING), sameContentInArray(consoleWriter1, consoleWriter2));
		assertThat(configuration.getEffectiveFormatTokens(Level.WARNING), allOf(containsCollectionWithSizes(1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveWriters(Level.ERROR), sameContentInArray(consoleWriter1, consoleWriter2));
		assertThat(configuration.getEffectiveFormatTokens(Level.ERROR), allOf(containsCollectionWithSizes(1, 1), distinctContentInArray()));

		configuration = configurator.addWriter(consoleWriter3, Level.INFO, "123").create();
		assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter2, consoleWriter3));
		assertThat(configuration.getEffectiveWriters(Level.TRACE), emptyArray());
		assertThat(configuration.getEffectiveFormatTokens(Level.TRACE), emptyArray());
		assertThat(configuration.getEffectiveWriters(Level.DEBUG), sameContentInArray(consoleWriter1));
		assertThat(configuration.getEffectiveFormatTokens(Level.DEBUG), containsCollectionWithSizes(1));
		assertThat(configuration.getEffectiveWriters(Level.INFO), sameContentInArray(consoleWriter1, consoleWriter3));
		assertThat(configuration.getEffectiveFormatTokens(Level.INFO), allOf(containsCollectionWithSizes(1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveWriters(Level.WARNING), sameContentInArray(consoleWriter1, consoleWriter2, consoleWriter3));
		assertThat(configuration.getEffectiveFormatTokens(Level.WARNING), allOf(containsCollectionWithSizes(1, 1, 1), distinctContentInArray()));
		assertThat(configuration.getEffectiveWriters(Level.ERROR), sameContentInArray(consoleWriter1, consoleWriter2, consoleWriter3));
		assertThat(configuration.getEffectiveFormatTokens(Level.ERROR), allOf(containsCollectionWithSizes(1, 1, 1), distinctContentInArray()));
		try {
			configuration = configurator.addWriter(null, Level.ERROR, "").create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter2, consoleWriter3));
		}

		try {
			configuration = configurator.addWriter(new NullWriter(), null, "").create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter2, consoleWriter3));
		}

		try {
			configuration = configurator.addWriter(new NullWriter(), Level.ERROR, null).create();
			fail("NullPointerException expected");
		} catch (NullPointerException ex) {
			assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter2, consoleWriter3));
		}

		configuration = configurator.removeWriter(consoleWriter2).create();
		assertThat(configuration.getWriters(), sameContent(consoleWriter1, consoleWriter3));

		configuration = configurator.removeAllWriters().create();
		assertThat(configuration.getWriters(), empty());
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
		Thread.sleep(100L); // Wait for shutdown of writing thread
		assertEquals(threadCount, Thread.activeCount());

		configurator = Configurator.defaultConfig().writingThread("!!! NONEXISTING THREAD !!!");
		configuration = configurator.create();
		assertEquals("LOGGER WARNING: Thread \"!!! NONEXISTING THREAD !!!\" could not be found, writing thread will not be used", getErrorStream().nextLine());
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
