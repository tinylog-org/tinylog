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
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Tests for the logger.
 * 
 * @see org.pmw.tinylog.Logger
 */
public class LoggerTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Test getter and setter for logging level.
	 */
	@Test
	public final void testLoggingLevel() {
		Logger.setLoggingLevel(ELoggingLevel.TRACE);
		assertEquals(ELoggingLevel.TRACE, Logger.getLoggingLevel());

		Logger.setLoggingLevel(ELoggingLevel.OFF);
		assertEquals(ELoggingLevel.OFF, Logger.getLoggingLevel());

		Logger.setLoggingLevel(ELoggingLevel.ERROR);
		assertEquals(ELoggingLevel.ERROR, Logger.getLoggingLevel());

		Logger.setLoggingLevel(null);
		assertEquals(ELoggingLevel.OFF, Logger.getLoggingLevel());
	}

	/**
	 * Test getter and setter for logging format pattern.
	 */
	@Test
	public final void testLoggingFormat() {
		Logger.setLoggingFormat("{message}");
		assertEquals("{message}", Logger.getLoggingFormat());

		Logger.setLoggingFormat(null);
		assertNotNull(Logger.getLoggingFormat());
		assertFalse("{message}".equals(Logger.getLoggingFormat()));
	}

	/**
	 * Test getter and setter for locale.
	 */
	@Test
	public final void testLocale() {
		Logger.setLocale(Locale.US);
		assertEquals(Locale.US, Logger.getLocale());

		Logger.setLocale(Locale.GERMANY);
		assertEquals(Locale.GERMANY, Logger.getLocale());

		Logger.setLocale(null);
		assertEquals(Locale.getDefault(), Logger.getLocale());
	}

	/**
	 * Test getter and setter for limitation of stack traces.
	 */
	@Test
	public final void testMaxStackTraceElements() {
		Logger.setMaxStackTraceElements(Integer.MIN_VALUE);
		assertEquals(Integer.MAX_VALUE, Logger.getMaxStackTraceElements());

		Logger.setMaxStackTraceElements(-1);
		assertEquals(Integer.MAX_VALUE, Logger.getMaxStackTraceElements());

		Logger.setMaxStackTraceElements(0);
		assertEquals(0, Logger.getMaxStackTraceElements());

		Logger.setMaxStackTraceElements(40);
		assertEquals(40, Logger.getMaxStackTraceElements());
	}

	/**
	 * Test getter and setter for logging writer.
	 */
	@Test
	public final void testLoggingWriter() {
		Logger.setWriter(null);
		assertNull(Logger.getWriter());
		Logger.info("My message"); // Must NOT thrown any exception but ignore the log entry

		ILoggingWriter writer = new ConsoleWriter();
		Logger.setWriter(writer);
		assertEquals(writer, Logger.getWriter());
	}

	/**
	 * Test trace methods.
	 */
	@Test
	public final void testTrace() {
		StoreWriter writer = new StoreWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.TRACE);
		Logger.setLoggingFormat("{message}");

		Logger.trace("Hello!");
		assertEquals(ELoggingLevel.TRACE, writer.consumeLevel());

		Logger.trace(new Exception());
		assertEquals(ELoggingLevel.TRACE, writer.consumeLevel());

		Logger.trace(new Exception(), "Hello!");
		assertEquals(ELoggingLevel.TRACE, writer.consumeLevel());
	}

	/**
	 * Test debug methods.
	 */
	@Test
	public final void testDebug() {
		StoreWriter writer = new StoreWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.DEBUG);
		Logger.setLoggingFormat("{message}");

		Logger.debug("Hello!");
		assertEquals(ELoggingLevel.DEBUG, writer.consumeLevel());

		Logger.debug(new Exception());
		assertEquals(ELoggingLevel.DEBUG, writer.consumeLevel());

		Logger.debug(new Exception(), "Hello!");
		assertEquals(ELoggingLevel.DEBUG, writer.consumeLevel());
	}

	/**
	 * Test info methods.
	 */
	@Test
	public final void testInfo() {
		StoreWriter writer = new StoreWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.INFO);
		Logger.setLoggingFormat("{message}");

		Logger.info("Hello!");
		assertEquals(ELoggingLevel.INFO, writer.consumeLevel());

		Logger.info(new Exception());
		assertEquals(ELoggingLevel.INFO, writer.consumeLevel());

		Logger.info(new Exception(), "Hello!");
		assertEquals(ELoggingLevel.INFO, writer.consumeLevel());
	}

	/**
	 * Test warning methods.
	 */
	@Test
	public final void testWarn() {
		StoreWriter writer = new StoreWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.WARNING);
		Logger.setLoggingFormat("{message}");

		Logger.warn("Hello!");
		assertEquals(ELoggingLevel.WARNING, writer.consumeLevel());

		Logger.warn(new Exception());
		assertEquals(ELoggingLevel.WARNING, writer.consumeLevel());

		Logger.warn(new Exception(), "Hello!");
		assertEquals(ELoggingLevel.WARNING, writer.consumeLevel());
	}

	/**
	 * Test error methods.
	 */
	@Test
	public final void testError() {
		StoreWriter writer = new StoreWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.ERROR);
		Logger.setLoggingFormat("{message}");

		Logger.error("Hello!");
		assertEquals(ELoggingLevel.ERROR, writer.consumeLevel());

		Logger.error(new Exception());
		assertEquals(ELoggingLevel.ERROR, writer.consumeLevel());

		Logger.error(new Exception(), "Hello!");
		assertEquals(ELoggingLevel.ERROR, writer.consumeLevel());
	}

	/**
	 * Test special logging levels for packages.
	 */
	@Test
	public final void testPackageLoggingLevel() {

		StoreWriter writer = new StoreWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.INFO);
		Logger.setLoggingFormat("{message}");

		Logger.setLoggingLevel("a.b", ELoggingLevel.DEBUG);
		assertEquals(ELoggingLevel.INFO, Logger.getLoggingLevel("a"));
		assertEquals(ELoggingLevel.DEBUG, Logger.getLoggingLevel("a.b"));
		assertEquals(ELoggingLevel.DEBUG, Logger.getLoggingLevel("a.b.c"));
		assertEquals(ELoggingLevel.INFO, Logger.getLoggingLevel("a.bc"));

		Logger.resetLoggingLevel("a.b");
		assertEquals(ELoggingLevel.INFO, Logger.getLoggingLevel("a.b"));

		Logger.setLoggingLevel("a.b", ELoggingLevel.DEBUG);
		assertEquals(ELoggingLevel.DEBUG, Logger.getLoggingLevel("a.b"));

		Logger.setLoggingLevel("a.b", null);
		assertEquals(ELoggingLevel.INFO, Logger.getLoggingLevel("a.b"));

		Logger.debug("Hello!");
		assertEquals(ELoggingLevel.INFO, Logger.getLoggingLevel("org.pmw.tinylog"));
		assertNull(writer.consumeMessage());

		Logger.setLoggingLevel("org.pmw.tinylog", ELoggingLevel.INFO);
		assertEquals(ELoggingLevel.INFO, Logger.getLoggingLevel("org.pmw.tinylog"));
		Logger.debug("Hello!");
		assertNull(writer.consumeMessage());

		Logger.setLoggingLevel("org.pmw.tinylog", ELoggingLevel.DEBUG);
		assertEquals(ELoggingLevel.DEBUG, Logger.getLoggingLevel("org.pmw.tinylog"));
		Logger.debug("Hello!");
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.resetAllLoggingLevel();
		assertEquals(ELoggingLevel.INFO, Logger.getLoggingLevel("org.pmw.tinylog"));
		Logger.debug("Hello!");
		assertNull(writer.consumeMessage());
	}

	/**
	 * Test a full log entry with all possible patterns.
	 */
	@Test
	public final void testFullLogEntry() {
		StoreWriter writer = new StoreWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.INFO);
		Logger.setLoggingFormat("{thread}#{class}#{method}#{file}#{line}#{level}#{date:yyyy}#{message}");

		int lineNumber = new Throwable().getStackTrace()[0].getLineNumber() + 1;
		Logger.info("Hello");
		assertEquals(
				MessageFormat.format("{0}#{1}#testFullLogEntry#LoggerTest.java#{2}#{3}#{4}#Hello{5}", Thread.currentThread().getName(),
						LoggerTest.class.getName(), lineNumber, ELoggingLevel.INFO, new SimpleDateFormat("yyyy").format(new Date()), NEW_LINE),
				writer.consumeMessage());
	}

	/**
	 * Test case if failed to create log entry.
	 */
	@Test
	public final void testFailedLogEntry() {
		StoreWriter writer = new StoreWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.INFO);
		Logger.setMaxStackTraceElements(0);

		Object object = new Object() {

			@Override
			public String toString() {
				throw new NullPointerException();
			}

		};

		Logger.setLoggingFormat("{level}#{class}#{message}");
		Logger.info("{0}", object);
		assertEquals(MessageFormat.format("ERROR#{0}#Could not created log entry: {1}{2}", LoggerTest.class.getName(), NullPointerException.class.getName(),
				NEW_LINE), writer.consumeMessage());
	}

	/**
	 * Test log entries which display exceptions.
	 */
	@Test
	public final void testExceptions() {
		StoreWriter writer = new StoreWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.INFO);
		Logger.setLoggingFormat("{message}");

		Logger.setMaxStackTraceElements(0);
		Logger.info(new Exception());
		assertEquals(Exception.class.getName() + NEW_LINE, writer.consumeMessage());

		Logger.setMaxStackTraceElements(0);
		Logger.info(new Exception("my test"));
		assertEquals(Exception.class.getName() + ": my test" + NEW_LINE, writer.consumeMessage());

		Logger.setMaxStackTraceElements(1);
		Logger.info(new Exception());
		String regex = Exception.class.getName().replaceAll("\\.", "\\\\.") + NEW_LINE + "\tat [\\S ]*" + NEW_LINE + "\t\\.\\.\\." + NEW_LINE;
		String message = writer.consumeMessage();
		assertTrue("[" + message + "] doesn't match [" + regex + "]", Pattern.matches(regex, message));

		Logger.setMaxStackTraceElements(-1);
		Logger.info(new Exception(new IndexOutOfBoundsException()));
		regex = Exception.class.getName().replaceAll("\\.", "\\\\.") + "\\: " + IndexOutOfBoundsException.class.getName().replaceAll("\\.", "\\\\.") + NEW_LINE
				+ "(\tat [\\S ]*" + NEW_LINE + ")*" + "Caused by\\: " + IndexOutOfBoundsException.class.getName().replaceAll("\\.", "\\\\.") + NEW_LINE
				+ "(\tat [\\S ]*" + NEW_LINE + ")*";
		message = writer.consumeMessage();
		assertTrue("[" + message + "] doesn't match [" + regex + "]", Pattern.matches(regex, message));
	}

	/**
	 * Test threading.
	 * 
	 * @throws Exception
	 *             Necessary to handle thread exceptions
	 */
	@Test
	public final void testThreading() throws Exception {
		final List<Exception> exceptions = Collections.synchronizedList(new ArrayList<Exception>());

		ThreadGroup threadGroup = new ThreadGroup("logging threads");

		for (int i = 0; i < 10; ++i) {
			Thread thread = new Thread(threadGroup, new Runnable() {

				@Override
				public void run() {
					try {
						for (int n = 0; n < 100; ++n) {
							Logger.setWriter(new StoreWriter());
							Logger.setLoggingLevel(ELoggingLevel.TRACE);
							Logger.info("Test threading! This is log entry {0}.", n);
						}
					} catch (Exception ex) {
						exceptions.add(ex);
					}
				}

			});
			thread.start();
		}

		while (threadGroup.activeCount() > 0) {
			Thread.sleep(10);
		}

		for (Exception exception : exceptions) {
			throw exception;
		}
	}

}
