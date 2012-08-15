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
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.policies.HourlyPolicy;
import org.pmw.tinylog.policies.MonthlyPolicy;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.policies.WeeklyPolicy;
import org.pmw.tinylog.policies.YearlyPolicy;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import org.pmw.tinylog.writers.LoggingWriter;
import org.pmw.tinylog.writers.RollingFileWriter;

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
	@Before
	@After
	public final void clean() {
		Enumeration<Object> keys = System.getProperties().keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith("tinylog")) {
				System.clearProperty(key);
			}
		}
	}

	/**
	 * Test reading logging level.
	 */
	@Test
	public final void testLevel() {
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
	 * Test reading special logging levels for packages.
	 */
	@Test
	public final void testPackageLevels() {
		Logger.setLoggingLevel(ELoggingLevel.INFO);

		System.setProperty("tinylog.level:a.b", "WARNING");
		PropertiesLoader.reload();
		assertEquals(ELoggingLevel.WARNING, Logger.getLoggingLevel("a.b"));

		System.setProperty("tinylog.level:a.b.c", "TRACE");
		PropertiesLoader.reload();
		assertEquals(ELoggingLevel.TRACE, Logger.getLoggingLevel("a.b.c"));

		System.setProperty("tinylog.level:org.pmw.tinylog", "ERROR");
		PropertiesLoader.reload();
		assertEquals(ELoggingLevel.ERROR, Logger.getLoggingLevel("org.pmw.tinylog"));

		System.setProperty("tinylog.level:org.pmw.tinylog", "invalid");
		PropertiesLoader.reload();
		assertEquals(ELoggingLevel.INFO, Logger.getLoggingLevel("org.pmw.tinylog"));

		Logger.resetAllLoggingLevel();
	}

	/**
	 * Test reading logging format.
	 */
	@Test
	public final void testFormat() {
		StoreWriter writer = new StoreWriter();
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
	 */
	@Test
	public final void testLocale() {
		StoreWriter writer = new StoreWriter();
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
	 */
	@Test
	public final void testStackTrace() {
		StoreWriter writer = new StoreWriter();
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
	 * @throws IOException
	 *             Failed to create temp file
	 */
	@Test
	public final void testLoggingWriter() throws IOException {
		Logger.setLoggingFormat(null);
		Logger.setWriter(null);
		Logger.setLoggingLevel(ELoggingLevel.TRACE);

		LoggingWriter writer = Logger.getWriter();
		assertNull(writer);

		System.setProperty("tinylog.writer", "console");
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleWriter.class, writer.getClass());

		System.setProperty("tinylog.writer", "null");
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNull(writer);

		System.setProperty("tinylog.writer", "file");
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNull(writer);

		File file = File.createTempFile("test", "tmp");
		file.deleteOnExit();
		System.setProperty("tinylog.writer", "file");
		System.setProperty("tinylog.writer.filename", file.getAbsolutePath());
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNotNull(writer);
		assertEquals(FileWriter.class, writer.getClass());
		file.delete();
		Logger.setWriter(null);

		System.setProperty("tinylog.writer", ConsoleWriter.class.getName());
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNotNull(writer);
		assertEquals(ConsoleWriter.class, writer.getClass());
		Logger.setWriter(null);

		file = File.createTempFile("test", "tmp");
		file.deleteOnExit();
		System.setProperty("tinylog.writer", FileWriter.class.getName());
		System.setProperty("tinylog.writer.filename", file.getAbsolutePath());
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNotNull(writer);
		assertEquals(FileWriter.class, writer.getClass());
		file.delete();
		Logger.setWriter(null);

		file = File.createTempFile("test", "tmp");
		file.deleteOnExit();
		System.setProperty("tinylog.writer", "rollingfile");
		System.setProperty("tinylog.writer.filename", file.getAbsolutePath());
		System.setProperty("tinylog.writer.maxBackups", "0");
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNotNull(writer);
		assertEquals(RollingFileWriter.class, writer.getClass());
		file.delete();
		Logger.setWriter(null);

		System.setProperty("tinylog.writer", String.class.getName());
		PropertiesLoader.reload();
		writer = Logger.getWriter();
		assertNull(writer);
	}

	/**
	 * Test reading policies.
	 */
	@Test
	public final void testLoadPolicies() {
		System.setProperty("tinylog.writer", "org.pmw.tinylog.PolicyWriter");

		System.setProperty("tinylog.writer.policies", "startup");
		PropertiesLoader.reload();
		PolicyWriter writer = (PolicyWriter) Logger.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(StartupPolicy.class, writer.getPolicies().get(0).getClass());
		Logger.setWriter(null);

		System.setProperty("tinylog.writer.policies", "startup: abc");
		PropertiesLoader.reload();
		writer = (PolicyWriter) Logger.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(StartupPolicy.class, writer.getPolicies().get(0).getClass());
		Logger.setWriter(null);

		System.setProperty("tinylog.writer.policies", StartupPolicy.class.getName());
		PropertiesLoader.reload();
		writer = (PolicyWriter) Logger.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(StartupPolicy.class, writer.getPolicies().get(0).getClass());
		Logger.setWriter(null);

		System.setProperty("tinylog.writer.policies", "size: 1MB");
		PropertiesLoader.reload();
		writer = (PolicyWriter) Logger.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(SizePolicy.class, writer.getPolicies().get(0).getClass());
		Logger.setWriter(null);

		System.setProperty("tinylog.writer.policies", "hourly");
		PropertiesLoader.reload();
		writer = (PolicyWriter) Logger.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(HourlyPolicy.class, writer.getPolicies().get(0).getClass());
		Logger.setWriter(null);

		System.setProperty("tinylog.writer.policies", "daily: 24:00");
		PropertiesLoader.reload();
		writer = (PolicyWriter) Logger.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(DailyPolicy.class, writer.getPolicies().get(0).getClass());
		Logger.setWriter(null);

		System.setProperty("tinylog.writer.policies", "weekly: monday");
		PropertiesLoader.reload();
		writer = (PolicyWriter) Logger.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(WeeklyPolicy.class, writer.getPolicies().get(0).getClass());
		Logger.setWriter(null);

		System.setProperty("tinylog.writer.policies", "monthly: true");
		PropertiesLoader.reload();
		writer = (PolicyWriter) Logger.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(MonthlyPolicy.class, writer.getPolicies().get(0).getClass());
		Logger.setWriter(null);

		System.setProperty("tinylog.writer.policies", "yearly: january");
		PropertiesLoader.reload();
		writer = (PolicyWriter) Logger.getWriter();
		assertNotNull(writer);
		assertEquals(1, writer.getPolicies().size());
		assertEquals(YearlyPolicy.class, writer.getPolicies().get(0).getClass());
		Logger.setWriter(null);

		System.setProperty("tinylog.writer.policies", String.class.getName());
		PropertiesLoader.reload();
		writer = (PolicyWriter) Logger.getWriter();
		assertNotNull(writer);
		assertEquals(0, writer.getPolicies().size());
		Logger.setWriter(null);
	}

	/**
	 * Test loading properties form file.
	 */
	@Test
	public final void testLoadFromFile() {
		PropertiesLoader.reload();
		PropertiesLoader.loadFromFile("./propertiesLoaderTest.properties");
		assertEquals(ELoggingLevel.ERROR, Logger.getLoggingLevel());
		assertEquals("Hello from file!", Logger.getLoggingFormat());
		assertEquals(Locale.US, Logger.getLocale());
		assertEquals(42, Logger.getMaxStackTraceElements());
		assertNull(Logger.getWriter());
	}

}
