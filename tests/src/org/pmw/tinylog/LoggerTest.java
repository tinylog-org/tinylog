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
import org.pmw.tinylog.util.StoreWriter;

/**
 * Tests for the logger.
 * 
 * @see Logger
 */
public class LoggerTest extends AbstractTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Test getter and setter for logging level.
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
	 * Test special logging levels for packages.
	 */
	@Test
	public final void testPackageLoggingLevel() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{message}").activate();

		Configurator.currentConfig().level("a.b", LoggingLevel.DEBUG).activate();
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("a"));
		assertEquals(LoggingLevel.DEBUG, Logger.getLoggingLevel("a.b"));
		assertEquals(LoggingLevel.DEBUG, Logger.getLoggingLevel("a.b.c"));
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("a.bc"));

		Configurator.currentConfig().level("a.b", null).activate();
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("a.b"));

		Configurator.currentConfig().level("a.b", LoggingLevel.DEBUG).activate();
		assertEquals(LoggingLevel.DEBUG, Logger.getLoggingLevel("a.b"));

		Configurator.currentConfig().level("a.b", null).activate();
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("a.b"));

		Logger.debug("Hello!");
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("org.pmw.tinylog"));
		assertNull(writer.consumeMessage());

		Configurator.currentConfig().level("org.pmw.tinylog", LoggingLevel.INFO).activate();
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("org.pmw.tinylog"));
		Logger.debug("Hello!");
		assertNull(writer.consumeMessage());

		Configurator.currentConfig().level("org.pmw.tinylog", LoggingLevel.DEBUG).activate();
		assertEquals(LoggingLevel.DEBUG, Logger.getLoggingLevel("org.pmw.tinylog"));
		Logger.debug("Hello!");
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Configurator.currentConfig().resetAllLevelsForPackages().activate();
		assertEquals(LoggingLevel.INFO, Logger.getLoggingLevel("org.pmw.tinylog"));
		Logger.debug("Hello!");
		assertNull(writer.consumeMessage());
	}

	/**
	 * Test getter and setter for locale.
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
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.TRACE).formatPattern("{message}").activate();

		Logger.trace(new StringBuilder("Hello!"));
		assertEquals(LoggingLevel.TRACE, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.trace("Hello!");
		assertEquals(LoggingLevel.TRACE, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.trace("Hello!", (Object) null);
		assertEquals(LoggingLevel.TRACE, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.trace(new Exception());
		assertEquals(LoggingLevel.TRACE, writer.consumeLevel());

		Logger.trace(new Exception(), "Hello!");
		assertEquals(LoggingLevel.TRACE, writer.consumeLevel());
	}

	/**
	 * Test debug methods.
	 */
	@Test
	public final void testDebug() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.DEBUG).formatPattern("{message}").activate();

		Logger.debug(new StringBuilder("Hello!"));
		assertEquals(LoggingLevel.DEBUG, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.debug("Hello!");
		assertEquals(LoggingLevel.DEBUG, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.debug("Hello!", (Object) null);
		assertEquals(LoggingLevel.DEBUG, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.debug(new Exception());
		assertEquals(LoggingLevel.DEBUG, writer.consumeLevel());

		Logger.debug(new Exception(), "Hello!");
		assertEquals(LoggingLevel.DEBUG, writer.consumeLevel());
	}

	/**
	 * Test info methods.
	 */
	@Test
	public final void testInfo() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{message}").activate();

		Logger.info(new StringBuilder("Hello!"));
		assertEquals(LoggingLevel.INFO, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.info("Hello!");
		assertEquals(LoggingLevel.INFO, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.info("Hello!", (Object) null);
		assertEquals(LoggingLevel.INFO, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.info(new Exception());
		assertEquals(LoggingLevel.INFO, writer.consumeLevel());

		Logger.info(new Exception(), "Hello!");
		assertEquals(LoggingLevel.INFO, writer.consumeLevel());
	}

	/**
	 * Test warning methods.
	 */
	@Test
	public final void testWarn() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.WARNING).formatPattern("{message}").activate();

		Logger.warn(new StringBuilder("Hello!"));
		assertEquals(LoggingLevel.WARNING, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.warn("Hello!");
		assertEquals(LoggingLevel.WARNING, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.warn("Hello!", (Object) null);
		assertEquals(LoggingLevel.WARNING, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.warn(new Exception());
		assertEquals(LoggingLevel.WARNING, writer.consumeLevel());

		Logger.warn(new Exception(), "Hello!");
		assertEquals(LoggingLevel.WARNING, writer.consumeLevel());
	}

	/**
	 * Test error methods.
	 */
	@Test
	public final void testError() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.ERROR).formatPattern("{message}").activate();

		Logger.error(new StringBuilder("Hello!"));
		assertEquals(LoggingLevel.ERROR, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.error("Hello!");
		assertEquals(LoggingLevel.ERROR, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.error("Hello!", (Object) null);
		assertEquals(LoggingLevel.ERROR, writer.getLevel());
		assertEquals("Hello!" + NEW_LINE, writer.consumeMessage());

		Logger.error(new Exception());
		assertEquals(LoggingLevel.ERROR, writer.consumeLevel());

		Logger.error(new Exception(), "Hello!");
		assertEquals(LoggingLevel.ERROR, writer.consumeLevel());
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
				MessageFormat.format("{0}#{1}#testFullLogEntry#LoggerTest.java#{2}#{3}#{4}#Hello{5}", Thread.currentThread().getName(),
						LoggerTest.class.getName(), lineNumber, LoggingLevel.INFO, new SimpleDateFormat("yyyy").format(new Date()), NEW_LINE),
				writer.consumeMessage());
	}

	/**
	 * Test case if failed to create log entry.
	 */
	@Test
	public final void testFailedLogEntry() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).maxStackTraceElements(0).activate();

		Object object = new Object() {

			@Override
			public String toString() {
				throw new NullPointerException();
			}

		};

		Configurator.currentConfig().formatPattern("{level}#{class}#{message}").activate();
		Logger.info("{0}", object);
		assertEquals(MessageFormat.format("ERROR#{0}#Could not created log entry: {1}{2}", LoggerTest.class.getName(),
				NullPointerException.class.getName(), NEW_LINE), writer.consumeMessage());
	}

	/**
	 * Test log entries which display exceptions.
	 */
	@Test
	public final void testExceptions() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{message}").activate();

		Configurator.currentConfig().maxStackTraceElements(0).activate();
		Logger.info(new Exception());
		assertEquals(Exception.class.getName() + NEW_LINE, writer.consumeMessage());

		Configurator.currentConfig().maxStackTraceElements(0).activate();
		Logger.info(new Exception("my test"));
		assertEquals(Exception.class.getName() + ": my test" + NEW_LINE, writer.consumeMessage());

		Configurator.currentConfig().maxStackTraceElements(1).activate();
		Logger.info(new Exception());
		String regex = Exception.class.getName().replaceAll("\\.", "\\\\.") + NEW_LINE + "\tat [\\S ]*" + NEW_LINE + "\t\\.\\.\\." + NEW_LINE;
		String message = writer.consumeMessage();
		assertTrue("[" + message + "] doesn't match [" + regex + "]", Pattern.matches(regex, message));

		Configurator.currentConfig().maxStackTraceElements(-1).activate();
		Logger.info(new Exception(new IndexOutOfBoundsException()));
		regex = Exception.class.getName().replaceAll("\\.", "\\\\.") + "\\: " + IndexOutOfBoundsException.class.getName().replaceAll("\\.", "\\\\.")
				+ NEW_LINE + "(\tat [\\S ]*" + NEW_LINE + ")*" + "Caused by\\: " + IndexOutOfBoundsException.class.getName().replaceAll("\\.", "\\\\.")
				+ NEW_LINE + "(\tat [\\S ]*" + NEW_LINE + ")*";
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
		Configurator.defaultConfig().activate();
		final List<Exception> exceptions = Collections.synchronizedList(new ArrayList<Exception>());

		ThreadGroup threadGroup = new ThreadGroup("logging threads");

		for (int i = 0; i < 10; ++i) {
			Thread thread = new Thread(threadGroup, new Runnable() {

				@Override
				public void run() {
					try {
						for (int n = 0; n < 100; ++n) {
							Configurator.currentConfig().writer(new StoreWriter()).level(LoggingLevel.TRACE).activate();
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
