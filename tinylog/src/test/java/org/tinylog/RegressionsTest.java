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

package org.tinylog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Test;
import org.tinylog.Configurator;
import org.tinylog.EnvironmentHelper;
import org.tinylog.Level;
import org.tinylog.LogEntry;
import org.tinylog.Logger;
import org.tinylog.labelers.TimestampLabeler;
import org.tinylog.policies.SizePolicy;
import org.tinylog.policies.StartupPolicy;
import org.tinylog.util.ConfigurationCreator;
import org.tinylog.util.FileHelper;
import org.tinylog.util.LogEntryBuilder;
import org.tinylog.util.StoreWriter;
import org.tinylog.writers.LogEntryValue;
import org.tinylog.writers.RollingFileWriter;

/**
 * Tests old fixed bugs to prevent regressions.
 */
public class RegressionsTest extends AbstractTest {

	/**
	 * Bug: Wrong class in log entry if there isn't set any special logging level for at least one package.
	 */
	@Test
	public final void testWrongClass() {
		StoreWriter writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.CLASS);
		Configurator.defaultConfig().writer(writer).level(Level.TRACE).activate();

		Configurator.currentConfig().level("org", Level.TRACE).activate();
		Logger.info("");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(RegressionsTest.class.getName(), logEntry.getClassName()); // Was already OK

		Configurator.currentConfig().level("org", null).activate();
		Logger.info("");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(RegressionsTest.class.getName(), logEntry.getClassName()); // Failed
	}

	/**
	 * Bug: If a log file is continued, the policy will start from scratch. This leads to a too late rollover.
	 *
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testContinueLogFile() throws Exception {
		File file = FileHelper.createTemporaryFile("tmp");

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0, new SizePolicy(10));
		writer.init(ConfigurationCreator.getDummyConfiguration());
		writer.write(new LogEntryBuilder().renderedLogEntry("12345").create());
		writer.close();

		writer = new RollingFileWriter(file.getAbsolutePath(), 0, new SizePolicy(10));
		writer.init(ConfigurationCreator.getDummyConfiguration());
		writer.write(new LogEntryBuilder().renderedLogEntry("123456").create());
		writer.close();

		assertEquals(6, file.length());
		file.delete();
	}

	/**
	 * Bug: IllegalArgumentException if there are curly brackets in the log message.
	 */
	@Test
	public final void testCurlyBracketsInText() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).activate();

		Logger.info("{TEST}"); // Failed (java.lang.IllegalArgumentException)

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("{TEST}", logEntry.getMessage());
	}

	/**
	 * Bug: Writer gets active logging level instead of the logging level of the log entry.
	 */
	@Test
	public final void testLoggingLevel() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(Level.INFO).activate();

		Logger.error("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello", logEntry.getMessage());
	}

	/**
	 * Bug: If all custom logging levels for packages are lower than the default package level, the custom logging
	 * levels will be ignored.
	 */
	@Test
	public final void testLowerCustomLoggingLevelsForPackages() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().level(Level.INFO).level(RegressionsTest.class.getPackage().getName(), Level.OFF).activate();
		Logger.info("should be ignored"); // Was output
		assertNull(writer.consumeLogEntry());
	}

	/**
	 * Bug: Timestamps need a locale but the locale isn't set at startup.
	 *
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testTimestampLabelerAtStartup() throws Exception {
		Configurator.defaultConfig().locale(null).activate();
		new RollingFileWriter(FileHelper.createTemporaryFile("txt").getName(), 0, new TimestampLabeler()); // Failed
	}

	/**
	 * Bug: Rolling fails for files without a parent path in timestamp labeler.
	 *
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testTimestampLabelerRolling() throws IOException {
		File file = FileHelper.createTemporaryFileInWorkspace("log");
		file = new File(file.getName());
		assertTrue(file.exists());

		TimestampLabeler labeler = new TimestampLabeler();
		labeler.init(ConfigurationCreator.getDummyConfiguration());
		file = labeler.getLogFile(file);
		labeler.roll(file, 10); // Failed

		file.delete();
	}

	/**
	 * Bug: Initialization of a rolling file writer with a timestamp labeler fails if there is no previous
	 * configuration.
	 *
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testTimestampLabelerWithoutPreviousLocale() throws Exception {
		resetLogger();
		File file = FileHelper.createTemporaryFile("log");
		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0, new TimestampLabeler(), new StartupPolicy());
		Configurator.defaultConfig().writer(writer).activate(); // Failed
		writer.close();
		file.delete();
	}

	/**
	 * Bug: Exceptions with an empty stack trace cause an {@link java.lang.ArrayIndexOutOfBoundsException
	 * ArrayIndexOutOfBoundsException}.
	 */
	@Test
	public final void testLogExceptionWithEmptyStackTrace() {
		StoreWriter writer = new StoreWriter(LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).formatPattern("{message}").activate();

		Exception exception = new Exception();
		exception.setStackTrace(new StackTraceElement[0]);
		Logger.error(exception); // Failed (java.lang.ArrayIndexOutOfBoundsException)

		LogEntry logEntry = writer.consumeLogEntry();
		assertNotNull(logEntry);
		assertEquals(exception.getClass().getName() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Bug: System properties not loaded without existing properties file in default package.
	 */
	@Test
	public final void testSystemProperties() {
		try {
			System.setProperty("tinylog.level", "trace");
			Configurator.init().activate();
			assertTrue(Logger.isTraceEnabled());
		} finally {
			System.clearProperty("tinylog.level");
		}
	}

	private static void resetLogger() throws Exception {
		Field field = Logger.class.getDeclaredField("configuration");
		field.setAccessible(true);
		field.set(null, null);
	}

}
