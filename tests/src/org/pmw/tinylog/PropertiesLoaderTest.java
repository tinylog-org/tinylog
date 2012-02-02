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
import java.util.Locale;

import org.junit.After;
import org.junit.Test;

/**
 * Test reading properties for the logger.
 * 
 * @see org.pmw.tinylog.PropertiesLoader
 */
public class PropertiesLoaderTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Clear properties.
	 */
	@After
	public final void init() {
		System.clearProperty("tinylog.level");
		System.clearProperty("tinylog.format");
		System.clearProperty("tinylog.locale");
		System.clearProperty("tinylog.stacktrace");
		System.clearProperty("tinylog.writer");
	}

	/**
	 * Test reading logging level.
	 * 
	 * @throws Exception
	 *             Failed to reread properties
	 */
	@Test
	public final void testLevel() throws Exception {
		System.setProperty("tinylog.level", "TRACE");
		PropertiesLoader.reload();
		assertEquals(ELoggingLevel.TRACE, Logger.getLoggingLevel());

		System.setProperty("tinylog.level", "error");
		PropertiesLoader.reload();
		assertEquals(ELoggingLevel.ERROR, Logger.getLoggingLevel());

		System.setProperty("tinylog.level", "invalid");
		PropertiesLoader.reload();
		assertEquals(ELoggingLevel.ERROR, Logger.getLoggingLevel());
	}

	/**
	 * Test reading logging format.
	 * 
	 * @throws Exception
	 *             Failed to reread properties
	 */
	@Test
	public final void testFormat() throws Exception {
		LoggingWriter writer = new LoggingWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.INFO);

		System.setProperty("tinylog.format", "My log entry");
		PropertiesLoader.reload();
		assertEquals("My log entry", Logger.getLoggingFormat());
		Logger.info("My message");
		assertEquals("My log entry" + NEW_LINE, writer.consumeMessage());

		System.setProperty("tinylog.format", "My log entry: {message}");
		PropertiesLoader.reload();
		assertEquals("My log entry: {message}", Logger.getLoggingFormat());
		Logger.info("My message");
		assertEquals("My log entry: My message" + NEW_LINE, writer.consumeMessage());

		System.setProperty("tinylog.format", "My log entry: {message");
		PropertiesLoader.reload();
		assertEquals("My log entry: {message", Logger.getLoggingFormat());
		Logger.info("My message");
		assertEquals("My log entry: {message" + NEW_LINE, writer.consumeMessage());
	}

	/**
	 * Test locale for message format.
	 * 
	 * @throws Exception
	 *             Failed to reread properties
	 */
	@Test
	public final void testLocale() throws Exception {
		LoggingWriter writer = new LoggingWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.INFO);
		Logger.setLoggingFormat("{message}");

		System.setProperty("tinylog.locale", "de");
		PropertiesLoader.reload();
		assertEquals(Locale.GERMAN, Logger.getLocale());
		Logger.info("{0}", 0.1);
		assertEquals("0,1" + NEW_LINE, writer.consumeMessage());

		System.setProperty("tinylog.locale", "de_DE");
		PropertiesLoader.reload();
		assertEquals(Locale.GERMANY, Logger.getLocale());
		Logger.info("{0}", 0.1);
		assertEquals("0,1" + NEW_LINE, writer.consumeMessage());

		System.setProperty("tinylog.locale", "en");
		PropertiesLoader.reload();
		assertEquals(Locale.ENGLISH, Logger.getLocale());
		Logger.info("{0}", 0.1);
		assertEquals("0.1" + NEW_LINE, writer.consumeMessage());

		System.setProperty("tinylog.locale", "en_US");
		PropertiesLoader.reload();
		assertEquals(Locale.US, Logger.getLocale());
		Logger.info("{0}", 0.1);
		assertEquals("0.1" + NEW_LINE, writer.consumeMessage());

		System.setProperty("tinylog.locale", "en_US_WIN");
		PropertiesLoader.reload();
		assertEquals(new Locale("en", "US", "WIN"), Logger.getLocale());
	}

	/**
	 * Test reading stack trace limit.
	 * 
	 * @throws Exception
	 *             Failed to reread properties
	 */
	@Test
	public final void testStackTrace() throws Exception {
		LoggingWriter writer = new LoggingWriter();
		Logger.setLoggingFormat("{message}");
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.ERROR);

		System.setProperty("tinylog.stacktrace", "0");
		PropertiesLoader.reload();
		assertEquals(0, Logger.getMaxStackTraceElements());
		Logger.error(new Exception());
		String entry = writer.consumeMessage();
		assertNotNull(entry);
		assertEquals(1, entry.split(NEW_LINE).length);

		System.setProperty("tinylog.stacktrace", "1");
		PropertiesLoader.reload();
		assertEquals(1, Logger.getMaxStackTraceElements());
		Logger.error(new Exception());
		entry = writer.consumeMessage();
		assertNotNull(entry);
		assertEquals(3, entry.split(NEW_LINE).length);

		System.setProperty("tinylog.stacktrace", "5");
		PropertiesLoader.reload();
		assertEquals(5, Logger.getMaxStackTraceElements());
		Logger.error(new Exception());
		entry = writer.consumeMessage();
		assertNotNull(entry);
		assertEquals(7, entry.split(NEW_LINE).length);

		System.setProperty("tinylog.stacktrace", "-1");
		PropertiesLoader.reload();
		assertEquals(Integer.MAX_VALUE, Logger.getMaxStackTraceElements());
		Logger.error(new Exception());
		entry = writer.consumeMessage();
		assertNotNull(entry);
		assertEquals(Thread.currentThread().getStackTrace().length, entry.split(NEW_LINE).length);

		Logger.setMaxStackTraceElements(1);
		System.setProperty("tinylog.stacktrace", "invalid");
		PropertiesLoader.reload();
		Logger.error(new Exception());
		entry = writer.consumeMessage();
		assertNotNull(entry);
		assertEquals(3, entry.split(NEW_LINE).length);
	}

	/**
	 * Test reading logging writer.
	 * 
	 * @throws Exception
	 *             Failed to reread properties
	 */
	@Test
	public final void testLoggingWriter() throws Exception {
		Logger.setLoggingFormat(null);
		Logger.setWriter(null);
		Logger.setLoggingLevel(ELoggingLevel.TRACE);

		ILoggingWriter writer = Logger.getWriter();
		assertNull(writer);

		System.setProperty("tinylog.writer", "console");
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleLoggingWriter.class, writer.getClass());

		System.setProperty("tinylog.writer", "null");
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNull(writer);

		System.setProperty("tinylog.writer", "file");
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNull(writer);

		File file = File.createTempFile("test", "tmp");
		System.setProperty("tinylog.writer", "file:" + file.getAbsolutePath());
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNotNull(writer);
		assertEquals(FileLoggingWriter.class, writer.getClass());

		System.setProperty("tinylog.writer", ConsoleLoggingWriter.class.getName());
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleLoggingWriter.class, writer.getClass());

		file = File.createTempFile("test", "tmp");
		System.setProperty("tinylog.writer", FileLoggingWriter.class.getName() + ":" + file.getAbsolutePath());
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNotNull(writer);
		assertEquals(FileLoggingWriter.class, writer.getClass());
	}

	/**
	 * Test loading properties form file.
	 */
	@Test
	public final void testLoadFromFile() {
		PropertiesLoader.reload();
		PropertiesLoader.loadFile("./propertiesLoaderTest.properties");
		assertEquals(ELoggingLevel.ERROR, Logger.getLoggingLevel());
		assertEquals("Hello from file!", Logger.getLoggingFormat());
		assertEquals(Locale.US, Logger.getLocale());
		assertEquals(42, Logger.getMaxStackTraceElements());
		assertNull(Logger.getWriter());
	}

}
