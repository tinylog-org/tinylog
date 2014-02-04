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
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.pmw.tinylog.hamcrest.RegexMatcher.contains;
import static org.pmw.tinylog.hamcrest.RegexMatcher.matches;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;
import org.pmw.tinylog.util.NullWriter;
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
	 * Test if the class is a valid utility class.
	 * 
	 * @see AbstractTest#testIfValidUtilityClass(Class)
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(Logger.class);
	}

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
	 * Test getter for custom logging level for specific packages and classes.
	 */
	@Test
	public final void testCustomLoggingLevel() {
		Configurator.defaultConfig().level("a", LoggingLevel.TRACE).level("a.b", LoggingLevel.INFO).activate();

		assertEquals(LoggingLevel.TRACE, Logger.getLoggingLevel("a"));
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("a.b"));
		assertEquals(LoggingLevel.TRACE, Logger.getLoggingLevel("a.c"));
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("a.b.d"));
		assertEquals(LoggingLevel.TRACE, Logger.getLoggingLevel("a.c.d"));

		Configurator.defaultConfig().level(LoggerTest.class.getPackage(), LoggingLevel.TRACE).activate();

		assertEquals(LoggingLevel.TRACE, Logger.getLoggingLevel(LoggerTest.class.getPackage()));
		assertEquals(LoggingLevel.TRACE, Logger.getLoggingLevel(LoggerTest.class));

		Configurator.defaultConfig().level(LoggerTest.class, LoggingLevel.TRACE).activate();

		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel(LoggerTest.class.getPackage()));
		assertEquals(LoggingLevel.TRACE, Logger.getLoggingLevel(LoggerTest.class));
	}

	/**
	 * Test trace methods.
	 */
	@Test
	public final void testTrace() {
		for (boolean mode : new boolean[] { false, true }) {
			Set<LogEntryValue> requiredLogEntryValue = EnumSet.of(LogEntryValue.LOGGING_LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
			if (mode) {
				/* Get stack trace from sun reflection */
				requiredLogEntryValue.add(LogEntryValue.CLASS);
			} else {
				/* Get stack trace from a throwable if necessary */
			}

			StoreWriter writer = new StoreWriter(requiredLogEntryValue);
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
	}

	/**
	 * Test debug methods.
	 */
	@Test
	public final void testDebug() {
		for (boolean mode : new boolean[] { false, true }) {
			Set<LogEntryValue> requiredLogEntryValue = EnumSet.of(LogEntryValue.LOGGING_LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
			if (mode) {
				/* Get stack trace from sun reflection */
				requiredLogEntryValue.add(LogEntryValue.CLASS);
			} else {
				/* Get stack trace from a throwable if necessary */
			}

			StoreWriter writer = new StoreWriter(requiredLogEntryValue);
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
	}

	/**
	 * Test info methods.
	 */
	@Test
	public final void testInfo() {
		for (boolean mode : new boolean[] { false, true }) {
			Set<LogEntryValue> requiredLogEntryValue = EnumSet.of(LogEntryValue.LOGGING_LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
			if (mode) {
				/* Get stack trace from sun reflection */
				requiredLogEntryValue.add(LogEntryValue.CLASS);
			} else {
				/* Get stack trace from a throwable if necessary */
			}

			StoreWriter writer = new StoreWriter(requiredLogEntryValue);
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
	}

	/**
	 * Test warning methods.
	 */
	@Test
	public final void testWarning() {
		for (boolean mode : new boolean[] { false, true }) {
			Set<LogEntryValue> requiredLogEntryValue = EnumSet.of(LogEntryValue.LOGGING_LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
			if (mode) {
				/* Get stack trace from sun reflection */
				requiredLogEntryValue.add(LogEntryValue.CLASS);
			} else {
				/* Get stack trace from a throwable if necessary */
			}

			StoreWriter writer = new StoreWriter(requiredLogEntryValue);
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
	}

	/**
	 * Test error methods.
	 */
	@Test
	public final void testError() {
		for (boolean mode : new boolean[] { false, true }) {
			Set<LogEntryValue> requiredLogEntryValue = EnumSet.of(LogEntryValue.LOGGING_LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
			if (mode) {
				/* Get stack trace from sun reflection */
				requiredLogEntryValue.add(LogEntryValue.CLASS);
			} else {
				/* Get stack trace from a throwable if necessary */
			}

			StoreWriter writer = new StoreWriter(requiredLogEntryValue);
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
	}

	/**
	 * Test output method with stack trace deep.
	 * 
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testOutputWithStackTraceDeep() throws InterruptedException {
		/* Test logging without writer */

		Configurator.defaultConfig().writer(null).activate();
		Logger.output(Logger.DEEP_OF_STACK_TRACE, null, null, null, null);

		/* Initialize writer */

		StoreWriter writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.CLASS);
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.DEBUG).activate();

		/* Test logging of class */

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

		StringListOutputStream errorStream = getErrorStream();
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

		/* Test logging without writer */

		Configurator.defaultConfig().writer(null).activate();
		Logger.output(stackTraceElement, null, null, null, null);

		/* Initialize writer */

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

		StringListOutputStream errorStream = getErrorStream();
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

	/**
	 * Test output method with class name.
	 * 
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testOutputWithClassName() throws InterruptedException {
		/* Test logging without writer */

		Configurator.defaultConfig().level(LoggerTest.class, LoggingLevel.TRACE).writer(null).activate();
		Logger.info("Hello!");

		/* Initialize writer */

		StoreWriter writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.CLASS);
		Configurator.defaultConfig().writer(writer).activate();

		/* Test logging of class */

		Configurator.currentConfig().level(LoggingLevel.DEBUG).activate();

		Logger.info("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());

		/* Test logging of plain texts */

		writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.CLASS, LogEntryValue.MESSAGE);
		Configurator.currentConfig().writer(writer).level(LoggingLevel.INFO).activate();

		Logger.debug("Hello!");
		assertNull(writer.consumeLogEntry());

		Logger.info("Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		/* Test logging of exceptions */

		writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.CLASS, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
		Configurator.currentConfig().writer(writer).level(LoggingLevel.INFO).activate();

		Exception exception = new Exception();
		Logger.warn(exception, null);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLoggingLevel());
		assertEquals(exception, logEntry.getException());

		Logger.error(exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		/* Test different logging level of an particular package */

		Configurator.currentConfig().level(LoggerTest.class.getPackage(), LoggingLevel.DEBUG).activate();

		Logger.debug("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.DEBUG, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		/* Test failure of creating log entry */

		Configurator.currentConfig().level(LoggerTest.class.getPackage(), null).activate();

		StringListOutputStream errorStream = getErrorStream();
		assertFalse(errorStream.hasLines());
		Logger.info("Hello {0}!", new EvilObject());
		assertNull(writer.consumeLogEntry());
		assertTrue(errorStream.hasLines());
		errorStream.clear();

		/* Test using writing thread */

		Configurator.currentConfig().writingThread(null).activate();
		WritingThread writingThread = findWritingThread();
		assertNotNull(writingThread);

		Logger.info("Hello!");
		assertNull(writer.consumeLogEntry());
		writingThread.shutdown();
		writingThread.join();
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());
	}

	/**
	 * Test a log entry with a process ID pattern.
	 */
	@Test
	public final void testLogEntryWithProcessId() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.PROCESS_ID, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{pid}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(EnvironmentHelper.getProcessId().toString(), logEntry.getProcessId());
		assertEquals(EnvironmentHelper.getProcessId() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a thread pattern.
	 */
	@Test
	public final void testLogEntryWithThread() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.THREAD, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{thread}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Thread.currentThread(), logEntry.getThread());
		assertEquals(Thread.currentThread().getName() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a thread ID pattern.
	 */
	@Test
	public final void testLogEntryWithThreadId() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.THREAD, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{thread_id}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Thread.currentThread(), logEntry.getThread());
		assertEquals(Thread.currentThread().getId() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a fully qualified class name pattern.
	 */
	@Test
	public final void testLogEntryWithFullyQualifiedClassName() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{class}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals(LoggerTest.class.getName() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a package name pattern.
	 */
	@Test
	public final void testLogEntryWithPackageName() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{package}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals(LoggerTest.class.getPackage().getName() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());

		Logger.output(new StackTraceElement("com.test.MyClass", "unknown", "unknown", -1), LoggingLevel.INFO, null, "Hello", new Object[0]);

		logEntry = writer.consumeLogEntry();
		assertEquals("com.test.MyClass", logEntry.getClassName());
		assertEquals("com.test" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());

		Logger.output(new StackTraceElement("MyClass", "unknown", "unknown", -1), LoggingLevel.INFO, null, "Hello", new Object[0]);

		logEntry = writer.consumeLogEntry();
		assertEquals("MyClass", logEntry.getClassName());
		assertEquals("" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a class name pattern.
	 */
	@Test
	public final void testLogEntryWithClassName() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{class_name}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals(LoggerTest.class.getSimpleName() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());

		Logger.output(new StackTraceElement("com.test.MyClass", "unknown", "unknown", -1), LoggingLevel.INFO, null, "Hello", new Object[0]);

		logEntry = writer.consumeLogEntry();
		assertEquals("com.test.MyClass", logEntry.getClassName());
		assertEquals("MyClass" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());

		Logger.output(new StackTraceElement("MyClass", "unknown", "unknown", -1), LoggingLevel.INFO, null, "Hello", new Object[0]);

		logEntry = writer.consumeLogEntry();
		assertEquals("MyClass", logEntry.getClassName());
		assertEquals("MyClass" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a method name pattern.
	 */
	@Test
	public final void testLogEntryWithMethodName() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.METHOD, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{method}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals("testLogEntryWithMethodName", logEntry.getMethodName());
		assertEquals("testLogEntryWithMethodName" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a file name pattern.
	 */
	@Test
	public final void testLogEntryWithFileName() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.FILE, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{file}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals("LoggerTest.java", logEntry.getFilename());
		assertEquals("LoggerTest.java" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a line number pattern.
	 */
	@Test
	public final void testLogEntryWithLineNumber() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.LINE_NUMBER, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{line}").activate();

		int lineNumber = new Throwable().getStackTrace()[0].getLineNumber() + 1;
		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(lineNumber, logEntry.getLineNumber());
		assertEquals(lineNumber + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a logging level pattern.
	 */
	@Test
	public final void testLogEntryWithLoggingLevel() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.LINE_NUMBER, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{level}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals(LoggingLevel.INFO + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a date pattern.
	 */
	@Test
	public final void testLogEntryWithDate() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.DATE, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{date:yyyy-MM-dd}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(new Date().getTime(), logEntry.getDate().getTime(), 10 * 1000d /* delta of 10 seconds */);
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a message pattern.
	 */
	@Test
	public final void testLogEntryWithMessage() {
		StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.MESSAGE, LogEntryValue.RENDERED_LOG_ENTRY));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{message}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals("Hello", logEntry.getMessage());
		assertEquals("Hello" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());

		Exception exception = new Exception();
		Logger.info(exception, "Hello");

		logEntry = writer.consumeLogEntry();
		assertEquals(exception, logEntry.getException());
		assertEquals("Hello", logEntry.getMessage());
		assertThat(logEntry.getRenderedLogEntry(),
				allOf(startsWith("Hello"), containsString(exception.getClass().getName()), endsWith(EnvironmentHelper.getNewLine())));
	}

	/**
	 * Test a full log entry with all possible patterns.
	 */
	@Test
	public final void testFullLogEntry() {
		StoreWriter writer = new StoreWriter(EnumSet.allOf(LogEntryValue.class));
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO)
				.formatPattern("{pid}#{thread}#{thread_id}#{class}#{package}#{class_name}#{method}#{file}#{line}#{level}#{date:yyyy}#{message}").activate();

		int lineNumber = new Throwable().getStackTrace()[0].getLineNumber() + 1;
		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(EnvironmentHelper.getProcessId().toString(), logEntry.getProcessId());
		assertEquals(Thread.currentThread(), logEntry.getThread());
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals("testFullLogEntry", logEntry.getMethodName());
		assertEquals("LoggerTest.java", logEntry.getFilename());
		assertEquals(lineNumber, logEntry.getLineNumber());
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals(new Date().getTime(), logEntry.getDate().getTime(), 10 * 1000d /* delta of 10 seconds */);
		assertEquals("Hello", logEntry.getMessage());
		assertNull(logEntry.getException());

		String renderedLogEntry = MessageFormat.format("{0}#{1}#{2}#{3}#{4}#{5}#testFullLogEntry#LoggerTest.java#{6}#{7}#{8}#Hello{9}",
				EnvironmentHelper.getProcessId(), Thread.currentThread().getName(), Thread.currentThread().getId(), LoggerTest.class.getName(),
				LoggerTest.class.getPackage().getName(), LoggerTest.class.getSimpleName(), lineNumber, LoggingLevel.INFO,
				new SimpleDateFormat("yyyy").format(new Date()), EnvironmentHelper.getNewLine());
		assertEquals(renderedLogEntry, logEntry.getRenderedLogEntry());
	}

	/**
	 * Test outputting complex exceptions.
	 */
	@Test
	public final void testExceptions() {
		String newLine = EnvironmentHelper.getNewLine();
		StoreWriter writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.EXCEPTION, LogEntryValue.RENDERED_LOG_ENTRY);

		Configurator.defaultConfig().writer(writer).level(LoggingLevel.ERROR).formatPattern("{message}").maxStackTraceElements(0).activate();

		Throwable exception = new Throwable();
		Logger.error(exception);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(exception, logEntry.getException());
		assertEquals("java.lang.Throwable" + newLine, logEntry.getRenderedLogEntry());

		exception = new Throwable("Hello");
		Logger.error(exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(exception, logEntry.getException());
		assertEquals("java.lang.Throwable: Hello" + newLine, logEntry.getRenderedLogEntry());

		Configurator.currentConfig().maxStackTraceElements(1).activate();

		exception = new Exception("Test");
		Logger.error(exception);
		logEntry = writer.consumeLogEntry();
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

	/**
	 * Test getting and setting configuration.
	 * 
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testConfiguration() throws Exception {
		/* Test getting and setting configuration */

		Configuration configuration = Configurator.defaultConfig().writer(null).formatPattern("Hello World").create();
		Logger.setConfirguration(configuration);
		assertEquals("Hello World", Logger.getConfiguration().create().getFormatPattern());

		/* Call init() method only once */

		DummyWriter writer = new DummyWriter();
		configuration = Configurator.defaultConfig().writer(writer).create();
		Logger.setConfirguration(configuration);
		assertSame(writer, Logger.getConfiguration().create().getWriter());
		assertEquals(1, writer.numberOfInits);

		configuration = Configurator.defaultConfig().writer(writer).create();
		Logger.setConfirguration(configuration);
		assertSame(writer, Logger.getConfiguration().create().getWriter());
		assertEquals(1, writer.numberOfInits);
	}

	/**
	 * Test if tinylog get the right stack trace element if JVM doesn't support reflection.
	 */
	@Test
	public final void testJvmWithoutReflection() {
		MockUp<Method> mock = new MockUp<Method>() {

			@Mock
			public Object invoke(final Object instance, final Object... arguments) {
				throw new UnsupportedOperationException();
			}

		};

		try {
			StoreWriter writer = new StoreWriter(EnumSet.of(LogEntryValue.CLASS, LogEntryValue.METHOD, LogEntryValue.RENDERED_LOG_ENTRY));
			Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{class}.{method}()").activate();

			Logger.info("Hello");

			LogEntry logEntry = writer.consumeLogEntry();
			assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
			assertEquals("testJvmWithoutReflection", logEntry.getMethodName());
			assertEquals(LoggerTest.class.getName() + ".testJvmWithoutReflection()" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
		} finally {
			mock.tearDown();
		}
	}

	private WritingThread findWritingThread() {
		for (Thread thread : Thread.getAllStackTraces().keySet()) {
			if (thread instanceof WritingThread) {
				return (WritingThread) thread;
			}
		}
		return null;
	}

	private static final class EvilObject {

		@Override
		public String toString() {
			/* Generate an individual error message */
			String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
			throw new RuntimeException(message);
		}

	}

	private static final class DummyWriter extends NullWriter {

		private int numberOfInits = 0;

		@Override
		public void init(final Configuration configuration) {
			++numberOfInits;
		}

	}

}
