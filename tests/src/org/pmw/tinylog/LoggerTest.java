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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.util.StoreWriter.LogEntry;

import static org.hamcrest.Matchers.containsString;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.pmw.tinylog.hamcrest.RegexMatcher.contains;
import static org.pmw.tinylog.hamcrest.RegexMatcher.matches;

/**
 * Tests for the logger.
 * 
 * @see Logger
 */
public class LoggerTest extends AbstractTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Test getter for logging level.
	 */
	@Test
	public final void testLoggingLevel() {
		Configurator.defaultConfig().level(LoggingLevel.TRACE).activate();
		assertEquals(LoggingLevel.TRACE, Logger.getLoggingLevel());

		Configurator.currentConfig().level(LoggingLevel.OFF).activate();
		assertEquals(LoggingLevel.OFF, Logger.getLoggingLevel());

		Configurator.currentConfig().level(LoggingLevel.ERROR).activate();
		assertEquals(LoggingLevel.ERROR, Logger.getLoggingLevel());

		Configurator.currentConfig().level(null).activate();
		assertEquals(LoggingLevel.OFF, Logger.getLoggingLevel());
	}

	/**
	 * Test getter for logging level for particular packages.
	 */
	@Test
	public final void testLoggingLevelForPackages() {
		Configurator.defaultConfig().level("a", LoggingLevel.TRACE).level("a.b", LoggingLevel.INFO).activate();
		assertEquals(LoggingLevel.TRACE, Logger.getLoggingLevel("a"));
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("a.b"));
		assertEquals(LoggingLevel.TRACE, Logger.getLoggingLevel("a.c"));
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("a.b.d"));
		assertEquals(LoggingLevel.TRACE, Logger.getLoggingLevel("a.c.d"));
	}

	/**
	 * Test getter for locale.
	 */
	@Test
	public final void testLocale() {
		Configurator.defaultConfig().locale(Locale.US).activate();
		assertEquals(Locale.US, Logger.getLocale());

		Configurator.currentConfig().locale(Locale.GERMANY).activate();
		assertEquals(Locale.GERMANY, Logger.getLocale());

		Configurator.currentConfig().locale(null).activate();
		assertEquals(Locale.getDefault(), Logger.getLocale());
	}

	/**
	 * Test trace methods.
	 */
	@Test
	public final void testTrace() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.TRACE).formatPattern("{message}").maxStackTraceElements(0).activate();

		Logger.trace(new StringBuilder("Hello!"));
		assertEquals(new LogEntry(LoggingLevel.TRACE, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Logger.trace("Hello!");
		assertEquals(new LogEntry(LoggingLevel.TRACE, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Logger.trace("Hello {0}!", "World");
		assertEquals(new LogEntry(LoggingLevel.TRACE, "Hello World!" + NEW_LINE), writer.consumeLogEntry());

		Logger.trace(new Exception());
		assertEquals(new LogEntry(LoggingLevel.TRACE, "java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());

		Logger.trace(new Exception(), "Test");
		assertEquals(new LogEntry(LoggingLevel.TRACE, "Test: java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());

		Configurator.currentConfig().level(LoggingLevel.DEBUG).activate();
		Logger.trace("Hello!");
		assertNull(writer.consumeLogEntry());
	}

	/**
	 * Test debug methods.
	 */
	@Test
	public final void testDebug() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.DEBUG).formatPattern("{message}").maxStackTraceElements(0).activate();

		Logger.debug(new StringBuilder("Hello!"));
		assertEquals(new LogEntry(LoggingLevel.DEBUG, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Logger.debug("Hello!");
		assertEquals(new LogEntry(LoggingLevel.DEBUG, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Logger.debug("Hello {0}!", "World");
		assertEquals(new LogEntry(LoggingLevel.DEBUG, "Hello World!" + NEW_LINE), writer.consumeLogEntry());

		Logger.debug(new Exception());
		assertEquals(new LogEntry(LoggingLevel.DEBUG, "java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());

		Logger.debug(new Exception(), "Test");
		assertEquals(new LogEntry(LoggingLevel.DEBUG, "Test: java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());

		Configurator.currentConfig().level(LoggingLevel.INFO).activate();
		Logger.debug("Hello!");
		assertNull(writer.consumeLogEntry());

		Configurator.currentConfig().level(LoggingLevel.TRACE).activate();
		Logger.debug("Hello!");
		assertEquals(new LogEntry(LoggingLevel.DEBUG, "Hello!" + NEW_LINE), writer.consumeLogEntry());
	}

	/**
	 * Test info methods.
	 */
	@Test
	public final void testInfo() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{message}").maxStackTraceElements(0).activate();

		Logger.info(new StringBuilder("Hello!"));
		assertEquals(new LogEntry(LoggingLevel.INFO, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Logger.info("Hello!");
		assertEquals(new LogEntry(LoggingLevel.INFO, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Logger.info("Hello {0}!", "World");
		assertEquals(new LogEntry(LoggingLevel.INFO, "Hello World!" + NEW_LINE), writer.consumeLogEntry());

		Logger.info(new Exception());
		assertEquals(new LogEntry(LoggingLevel.INFO, "java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());

		Logger.info(new Exception(), "Test");
		assertEquals(new LogEntry(LoggingLevel.INFO, "Test: java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());

		Configurator.currentConfig().level(LoggingLevel.WARNING).activate();
		Logger.info("Hello!");
		assertNull(writer.consumeLogEntry());

		Configurator.currentConfig().level(LoggingLevel.DEBUG).activate();
		Logger.info("Hello!");
		assertEquals(new LogEntry(LoggingLevel.INFO, "Hello!" + NEW_LINE), writer.consumeLogEntry());
	}

	/**
	 * Test warning methods.
	 */
	@Test
	public final void testWarning() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.WARNING).formatPattern("{message}").maxStackTraceElements(0).activate();

		Logger.warn(new StringBuilder("Hello!"));
		assertEquals(new LogEntry(LoggingLevel.WARNING, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Logger.warn("Hello!");
		assertEquals(new LogEntry(LoggingLevel.WARNING, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Logger.warn("Hello {0}!", "World");
		assertEquals(new LogEntry(LoggingLevel.WARNING, "Hello World!" + NEW_LINE), writer.consumeLogEntry());

		Logger.warn(new Exception());
		assertEquals(new LogEntry(LoggingLevel.WARNING, "java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());

		Logger.warn(new Exception(), "Test");
		assertEquals(new LogEntry(LoggingLevel.WARNING, "Test: java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());

		Configurator.currentConfig().level(LoggingLevel.ERROR).activate();
		Logger.warn("Hello!");
		assertNull(writer.consumeLogEntry());

		Configurator.currentConfig().level(LoggingLevel.INFO).activate();
		Logger.warn("Hello!");
		assertEquals(new LogEntry(LoggingLevel.WARNING, "Hello!" + NEW_LINE), writer.consumeLogEntry());
	}

	/**
	 * Test error methods.
	 */
	@Test
	public final void testError() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.ERROR).formatPattern("{message}").maxStackTraceElements(0).activate();

		Logger.error(new StringBuilder("Hello!"));
		assertEquals(new LogEntry(LoggingLevel.ERROR, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Logger.error("Hello!");
		assertEquals(new LogEntry(LoggingLevel.ERROR, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Logger.error("Hello {0}!", "World");
		assertEquals(new LogEntry(LoggingLevel.ERROR, "Hello World!" + NEW_LINE), writer.consumeLogEntry());

		Logger.error(new Exception());
		assertEquals(new LogEntry(LoggingLevel.ERROR, "java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());

		Logger.error(new Exception(), "Test");
		assertEquals(new LogEntry(LoggingLevel.ERROR, "Test: java.lang.Exception" + NEW_LINE), writer.consumeLogEntry());

		Configurator.currentConfig().level(LoggingLevel.OFF).activate();
		Logger.error("Hello!");
		assertNull(writer.consumeLogEntry());

		Configurator.currentConfig().level(LoggingLevel.WARNING).activate();
		Logger.error("Hello!");
		assertEquals(new LogEntry(LoggingLevel.ERROR, "Hello!" + NEW_LINE), writer.consumeLogEntry());
	}

	/**
	 * Test output method with stack trace deep.
	 * 
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testOutputWithStackTraceDeep() throws InterruptedException {
		int strackTraceDeep = Logger.DEEP_OF_STACK_TRACE - 1;
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).activate();

		/* Test logging of class */

		Configurator.currentConfig().level(LoggingLevel.DEBUG).formatPattern("{class}").activate();

		Logger.output(strackTraceDeep, LoggingLevel.INFO, null, "Hello!", new Object[0]);
		assertEquals(new LogEntry(LoggingLevel.INFO, "org.pmw.tinylog.LoggerTest" + NEW_LINE), writer.consumeLogEntry());

		/* Test logging of plain texts */

		Configurator.currentConfig().level(LoggingLevel.INFO).formatPattern("{message}").maxStackTraceElements(1).activate();

		/* Test logging of plain texts */

		Logger.output(strackTraceDeep, LoggingLevel.DEBUG, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());

		Logger.output(strackTraceDeep, LoggingLevel.INFO, null, "Hello {0}!", new Object[] { "World" });
		assertEquals(new LogEntry(LoggingLevel.INFO, "Hello World!" + NEW_LINE), writer.consumeLogEntry());

		/* Test logging of exceptions */

		Logger.output(strackTraceDeep, LoggingLevel.WARNING, new Exception(), null, new Object[0]);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLevel());
		assertThat(logEntry.getText(), matches("java\\.lang\\.Exception" + NEW_LINE
				+ "\tat org.pmw.tinylog.LoggerTest.testOutputWithStackTraceDeep\\(LoggerTest.java:\\d*\\)" + NEW_LINE + "\t\\.\\.\\." + NEW_LINE));

		Logger.output(strackTraceDeep, LoggingLevel.ERROR, new Exception(), "Test", new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLevel());
		assertThat(logEntry.getText(), matches("Test\\: java\\.lang\\.Exception" + NEW_LINE
				+ "\tat org.pmw.tinylog.LoggerTest.testOutputWithStackTraceDeep\\(LoggerTest.java:\\d*\\)" + NEW_LINE + "\t\\.\\.\\." + NEW_LINE));

		/* Test different logging level of an particular package */

		Configurator.currentConfig().level("org.pmw.tinylog", LoggingLevel.DEBUG).activate();

		Logger.output(strackTraceDeep, LoggingLevel.DEBUG, null, "Hello!", new Object[0]);
		assertEquals(new LogEntry(LoggingLevel.DEBUG, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		/* Test failure of creating log entry */

		Configurator.currentConfig().level("org.pmw.tinylog", null).maxStackTraceElements(0).activate();

		Logger.output(strackTraceDeep, LoggingLevel.INFO, null, "Hello {0}!", new Object[] { new EvilObject() });
		logEntry = writer.consumeLogEntry();
		assertThat(logEntry.getText(), containsString("java.lang.RuntimeException"));

		/* Test using writing thread */

		Configurator.currentConfig().writingThread(null).activate();
		WritingThread writingThread = findWritingThread();
		assertNotNull(writingThread);

		Logger.output(strackTraceDeep, LoggingLevel.INFO, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());
		writingThread.shutdown();
		writingThread.join();
		assertEquals(new LogEntry(LoggingLevel.INFO, "Hello!" + NEW_LINE), writer.consumeLogEntry());
	}

	/**
	 * Test output method with stack trace element.
	 * 
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testOutputWithStackTraceElement() throws InterruptedException {
		StackTraceElement stackTraceElement = new StackTraceElement("com.test.MyClass", "?", "?", -1);
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).activate();

		/* Test logging of class */

		Configurator.currentConfig().level(LoggingLevel.DEBUG).formatPattern("{class}").activate();

		Logger.output(stackTraceElement, LoggingLevel.INFO, null, "Hello!", new Object[0]);
		assertEquals(new LogEntry(LoggingLevel.INFO, "com.test.MyClass" + NEW_LINE), writer.consumeLogEntry());

		/* Test logging of plain texts */

		Configurator.currentConfig().level(LoggingLevel.INFO).formatPattern("{message}").maxStackTraceElements(1).activate();

		Logger.output(stackTraceElement, LoggingLevel.DEBUG, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());

		Logger.output(stackTraceElement, LoggingLevel.INFO, null, "Hello {0}!", new Object[] { "World" });
		assertEquals(new LogEntry(LoggingLevel.INFO, "Hello World!" + NEW_LINE), writer.consumeLogEntry());

		/* Test logging of exceptions */

		Logger.output(stackTraceElement, LoggingLevel.WARNING, new Exception(), null, new Object[0]);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLevel());
		assertThat(logEntry.getText(), matches("java\\.lang\\.Exception" + NEW_LINE
				+ "\tat org.pmw.tinylog.LoggerTest.testOutputWithStackTraceElement\\(LoggerTest.java:\\d*\\)" + NEW_LINE + "\t\\.\\.\\." + NEW_LINE));

		Logger.output(stackTraceElement, LoggingLevel.ERROR, new Exception(), "Test", new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLevel());
		assertThat(logEntry.getText(), matches("Test\\: java\\.lang\\.Exception" + NEW_LINE
				+ "\tat org.pmw.tinylog.LoggerTest.testOutputWithStackTraceElement\\(LoggerTest.java:\\d*\\)" + NEW_LINE + "\t\\.\\.\\." + NEW_LINE));

		/* Test different logging level of an particular package */

		Configurator.currentConfig().level("com.test", LoggingLevel.DEBUG).activate();

		Logger.output(stackTraceElement, LoggingLevel.DEBUG, null, "Hello!", new Object[0]);
		assertEquals(new LogEntry(LoggingLevel.DEBUG, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		/* Test failure of creating log entry */

		Configurator.currentConfig().level("com.test", null).maxStackTraceElements(0).activate();

		Logger.output(stackTraceElement, LoggingLevel.INFO, null, "Hello {0}!", new Object[] { new EvilObject() });
		logEntry = writer.consumeLogEntry();
		assertThat(logEntry.getText(), containsString("java.lang.RuntimeException"));

		/* Test using writing thread */

		Configurator.currentConfig().writingThread(null).activate();
		WritingThread writingThread = findWritingThread();
		assertNotNull(writingThread);

		Logger.output(stackTraceElement, LoggingLevel.INFO, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());
		writingThread.shutdown();
		writingThread.join();
		assertEquals(new LogEntry(LoggingLevel.INFO, "Hello!" + NEW_LINE), writer.consumeLogEntry());
	}

	private WritingThread findWritingThread() {
		for (Thread thread : Thread.getAllStackTraces().keySet()) {
			if (thread instanceof WritingThread) {
				return (WritingThread) thread;
			}
		}
		return null;
	}

	/**
	 * Test a full log entry with all possible patterns.
	 */
	@Test
	public final void testFullLogEntry() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO)
				.formatPattern("{thread}#{class}#{method}#{file}#{line}#{level}#{date:yyyy}#{message}").activate();

		int lineNumber = new Throwable().getStackTrace()[0].getLineNumber() + 1;
		Logger.info("Hello");
		assertEquals(
				new LogEntry(LoggingLevel.INFO, MessageFormat.format("{0}#{1}#testFullLogEntry#LoggerTest.java#{2}#{3}#{4}#Hello{5}", Thread
						.currentThread().getName(), LoggerTest.class.getName(), lineNumber, LoggingLevel.INFO, new SimpleDateFormat("yyyy")
						.format(new Date()), NEW_LINE)), writer.consumeLogEntry());
	}

	/**
	 * Test outputting complex exceptions.
	 */
	@Test
	public final void testExceptions() {
		StoreWriter writer = new StoreWriter();

		Configurator.defaultConfig().writer(writer).level(LoggingLevel.ERROR).formatPattern("{message}").maxStackTraceElements(1).activate();

		Logger.error(new Exception("Test"));
		LogEntry logEntry = writer.consumeLogEntry();
		assertThat(logEntry.getText(), matches("java\\.lang\\.Exception\\: Test" + NEW_LINE
				+ "\tat org.pmw.tinylog.LoggerTest.testExceptions\\(LoggerTest.java:\\d*\\)" + NEW_LINE + "\t\\.\\.\\." + NEW_LINE));

		Configurator.currentConfig().maxStackTraceElements(-1).activate();

		Logger.error(new RuntimeException(new NullPointerException()));
		logEntry = writer.consumeLogEntry();
		assertThat(logEntry.getText(), contains("java\\.lang\\.RuntimeException.*" + NEW_LINE
				+ "\tat org.pmw.tinylog.LoggerTest.testExceptions\\(LoggerTest.java:\\d*\\)" + NEW_LINE));
		assertThat(logEntry.getText(), contains("Caused by: java\\.lang\\.NullPointerException" + NEW_LINE
				+ "\tat org.pmw.tinylog.LoggerTest.testExceptions\\(LoggerTest.java:\\d*\\)" + NEW_LINE));
	}

	private static final class EvilObject {

		@Override
		public String toString() {
			throw new RuntimeException();
		}

	}

}
