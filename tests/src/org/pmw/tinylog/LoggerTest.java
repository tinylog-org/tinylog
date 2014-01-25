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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.pmw.tinylog.hamcrest.RegexMatcher.contains;
import static org.pmw.tinylog.hamcrest.RegexMatcher.matches;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;

import org.junit.Test;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.util.StringListOutputStream;
import org.pmw.tinylog.writers.LogEntry;
import org.pmw.tinylog.writers.LogEntryValue;

/**
 * Tests for the logger.
 * 
 * @see Logger
 */
public class LoggerTest extends AbstractTest {

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
	 * Test trace methods.
	 */
	@Test
	public final void testTrace() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.TRACE).activate();

		Logger.trace(new StringBuilder("Hello!"));
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.TRACE, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Logger.trace("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.TRACE, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Logger.trace("Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.TRACE, logEntry.getLoggingLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Exception exception = new Exception();

		Logger.trace(exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.TRACE, logEntry.getLoggingLevel());
		assertEquals(exception, logEntry.getException());

		Logger.trace(exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.TRACE, logEntry.getLoggingLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

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
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.DEBUG).activate();

		Logger.debug(new StringBuilder("Hello!"));
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.DEBUG, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Logger.debug("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.DEBUG, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Logger.debug("Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.DEBUG, logEntry.getLoggingLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Exception exception = new Exception();

		Logger.debug(exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.DEBUG, logEntry.getLoggingLevel());
		assertEquals(exception, logEntry.getException());

		Logger.debug(exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.DEBUG, logEntry.getLoggingLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		Configurator.currentConfig().level(LoggingLevel.INFO).activate();
		Logger.debug("Hello!");
		assertNull(writer.consumeLogEntry());
	}

	/**
	 * Test info methods.
	 */
	@Test
	public final void testInfo() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).activate();

		Logger.info(new StringBuilder("Hello!"));
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Logger.info("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Logger.info("Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Exception exception = new Exception();

		Logger.info(exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals(exception, logEntry.getException());

		Logger.info(exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		Configurator.currentConfig().level(LoggingLevel.WARNING).activate();
		Logger.info("Hello!");
		assertNull(writer.consumeLogEntry());
	}

	/**
	 * Test warning methods.
	 */
	@Test
	public final void testWarning() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.WARNING).activate();

		Logger.warn(new StringBuilder("Hello!"));
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Logger.warn("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Logger.warn("Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLoggingLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Exception exception = new Exception();

		Logger.warn(exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLoggingLevel());
		assertEquals(exception, logEntry.getException());

		Logger.warn(exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLoggingLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		Configurator.currentConfig().level(LoggingLevel.ERROR).activate();
		Logger.warn("Hello!");
		assertNull(writer.consumeLogEntry());
	}

	/**
	 * Test error methods.
	 */
	@Test
	public final void testError() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.ERROR).activate();

		Logger.error(new StringBuilder("Hello!"));
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Logger.error("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Logger.error("Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Exception exception = new Exception();

		Logger.error(exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals(exception, logEntry.getException());

		Logger.error(exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		Configurator.currentConfig().level(LoggingLevel.OFF).activate();
		Logger.error("Hello!");
		assertNull(writer.consumeLogEntry());
	}

	/**
	 * Test output method with stack trace deep.
	 * 
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testOutputWithStackTraceDeep() throws InterruptedException {
		/* Test logging of class */

		StoreWriter writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.CLASS);
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.DEBUG).activate();

		Logger.output(Logger.DEEP_OF_STACK_TRACE, LoggingLevel.INFO, null, "Hello!", new Object[0]);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());

		/* Test logging of plain texts */

		writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).activate();

		Logger.output(Logger.DEEP_OF_STACK_TRACE, LoggingLevel.DEBUG, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());

		Logger.output(Logger.DEEP_OF_STACK_TRACE, LoggingLevel.INFO, null, "Hello {0}!", new Object[] { "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		/* Test logging of exceptions */

		Exception exception = new Exception();

		Logger.output(Logger.DEEP_OF_STACK_TRACE, LoggingLevel.WARNING, exception, null, new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLoggingLevel());
		assertEquals(exception, logEntry.getException());

		Logger.output(Logger.DEEP_OF_STACK_TRACE, LoggingLevel.ERROR, exception, "Test", new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		/* Test different logging level of an particular package */

		Configurator.currentConfig().level("org.pmw.tinylog", LoggingLevel.DEBUG).activate();

		Logger.output(Logger.DEEP_OF_STACK_TRACE, LoggingLevel.DEBUG, null, "Hello!", new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.DEBUG, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		/* Test failure of creating log entry */

		Configurator.currentConfig().level("org.pmw.tinylog", null).activate();

		StringListOutputStream errorStream = getSystemErrorStream();
		assertFalse(errorStream.hasLines());
		Logger.output(Logger.DEEP_OF_STACK_TRACE, LoggingLevel.INFO, null, "Hello {0}!", new Object[] { new EvilObject() });
		assertNull(writer.consumeLogEntry());
		assertTrue(errorStream.hasLines());
		errorStream.clear();

		/* Test using writing thread */

		Configurator.currentConfig().writingThread(null).activate();
		WritingThread writingThread = findWritingThread();
		assertNotNull(writingThread);

		Logger.output(Logger.DEEP_OF_STACK_TRACE, LoggingLevel.INFO, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());
		writingThread.shutdown();
		writingThread.join();
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());
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
		StoreWriter writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.CLASS);
		Configurator.defaultConfig().writer(writer).activate();

		/* Test logging of class */

		Configurator.currentConfig().level(LoggingLevel.DEBUG).activate();

		Logger.output(stackTraceElement, LoggingLevel.INFO, null, "Hello!", new Object[0]);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("com.test.MyClass", logEntry.getClassName());

		/* Test logging of plain texts */

		writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.MESSAGE);
		Configurator.currentConfig().writer(writer).level(LoggingLevel.INFO).activate();

		Logger.output(stackTraceElement, LoggingLevel.DEBUG, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());

		Logger.output(stackTraceElement, LoggingLevel.INFO, null, "Hello {0}!", new Object[] { "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		/* Test logging of exceptions */

		writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
		Configurator.currentConfig().writer(writer).level(LoggingLevel.INFO).activate();

		Exception exception = new Exception();
		Logger.output(stackTraceElement, LoggingLevel.WARNING, exception, null, new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLoggingLevel());
		assertEquals(exception, logEntry.getException());

		Logger.output(stackTraceElement, LoggingLevel.ERROR, exception, "Test", new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		/* Test different logging level of an particular package */

		Configurator.currentConfig().level("com.test", LoggingLevel.DEBUG).activate();

		Logger.output(stackTraceElement, LoggingLevel.DEBUG, null, "Hello!", new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.DEBUG, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		/* Test failure of creating log entry */

		Configurator.currentConfig().level("com.test", null).activate();

		StringListOutputStream errorStream = getSystemErrorStream();
		assertFalse(errorStream.hasLines());
		Logger.output(stackTraceElement, LoggingLevel.INFO, null, "Hello {0}!", new Object[] { new EvilObject() });
		assertNull(writer.consumeLogEntry());
		assertTrue(errorStream.hasLines());
		errorStream.clear();

		/* Test using writing thread */

		Configurator.currentConfig().writingThread(null).activate();
		WritingThread writingThread = findWritingThread();
		assertNotNull(writingThread);

		Logger.output(stackTraceElement, LoggingLevel.INFO, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());
		writingThread.shutdown();
		writingThread.join();
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());
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
		StoreWriter writer = new StoreWriter(EnumSet.allOf(LogEntryValue.class));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO)
				.formatPattern("{thread}#{thread_id}#{class}#{package}#{class_name}#{method}#{file}#{line}#{level}#{date:yyyy}#{message}").activate();

		int lineNumber = new Throwable().getStackTrace()[0].getLineNumber() + 1;
		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(new Date().getTime(), logEntry.getDate().getTime(), 10 * 1000d /* delta of 10 seconds */);
		assertEquals(EnvironmentHelper.getProcessId().toString(), logEntry.getProcessId());
		assertEquals(Thread.currentThread(), logEntry.getThread());
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals("testFullLogEntry", logEntry.getMethodName());
		assertEquals("LoggerTest.java", logEntry.getFilename());
		assertEquals(lineNumber, logEntry.getLineNumber());
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello", logEntry.getMessage());
		assertNull(logEntry.getException());

		String renderedLogEntry = MessageFormat.format("{0}#{1}#{2}#{3}#{4}#testFullLogEntry#LoggerTest.java#{5}#{6}#{7}#Hello{8}", Thread.currentThread()
				.getName(), Thread.currentThread().getId(), LoggerTest.class.getName(), LoggerTest.class.getPackage().getName(), LoggerTest.class
				.getSimpleName(), lineNumber, LoggingLevel.INFO, new SimpleDateFormat("yyyy").format(new Date()), EnvironmentHelper.getNewLine());
		assertEquals(renderedLogEntry, logEntry.getRenderedLogEntry());
	}

	/**
	 * Test outputting complex exceptions.
	 */
	@Test
	public final void testExceptions() {
		String newLine = EnvironmentHelper.getNewLine();
		StoreWriter writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.EXCEPTION, LogEntryValue.RENDERED_LOG_ENTRY);

		Configurator.defaultConfig().writer(writer).level(LoggingLevel.ERROR).formatPattern("{message}").maxStackTraceElements(1).activate();

		Exception exception = new Exception("Test");
		Logger.error(exception);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(exception, logEntry.getException());
		assertThat(logEntry.getRenderedLogEntry(), matches("java\\.lang\\.Exception\\: Test" + newLine
				+ "\tat org.pmw.tinylog.LoggerTest.testExceptions\\(LoggerTest.java:\\d*\\)" + newLine + "\t\\.\\.\\." + newLine));

		Configurator.currentConfig().maxStackTraceElements(-1).activate();

		exception = new RuntimeException(new NullPointerException());
		Logger.error(exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(exception, logEntry.getException());
		assertThat(logEntry.getRenderedLogEntry(), contains("java\\.lang\\.RuntimeException.*" + newLine
				+ "\tat org.pmw.tinylog.LoggerTest.testExceptions\\(LoggerTest.java:\\d*\\)" + newLine));
		assertThat(logEntry.getRenderedLogEntry(), contains("Caused by: java\\.lang\\.NullPointerException" + newLine
				+ "\tat org.pmw.tinylog.LoggerTest.testExceptions\\(LoggerTest.java:\\d*\\)" + newLine));
	}

	private static final class EvilObject {

		@Override
		public String toString() {
			throw new RuntimeException();
		}

	}

}
