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

package org.apache.log4j;

import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.util.StoreWriter.LogEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for tinylog bridge.
 * 
 * @see TinylogBride
 */
public class TinylogBridgeTest extends AbstractTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	private SimpleLoggerWrapper logger;
	private StoreWriter writer;

	/**
	 * Set up logger.
	 */
	@Before
	public final void init() {
		logger = new SimpleLoggerWrapper();
		writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).formatPattern("{message}").maxStackTraceElements(0).activate();
	}

	/**
	 * Test getter for logging level.
	 */
	@Test
	public final void testLoggingLevel() {
		Configurator.currentConfig().level(LoggingLevel.TRACE).activate();
		assertEquals(Level.TRACE, logger.getLoggingLevel());

		Configurator.currentConfig().level(LoggingLevel.ERROR).activate();
		assertEquals(Level.ERROR, logger.getLoggingLevel());

		Configurator.currentConfig().level(LoggingLevel.OFF).activate();
		assertEquals(Level.OFF, logger.getLoggingLevel());

		Configurator.currentConfig().level("org.apache", LoggingLevel.WARNING).activate();
		assertEquals(Level.WARN, logger.getLoggingLevel());
	}

	/**
	 * Test if a specified logging level is enabled.
	 */
	@Test
	public final void testLoggingLevelEnabled() {
		Configurator.currentConfig().level(LoggingLevel.TRACE).activate();
		assertTrue(logger.isEnabled(Level.FATAL));
		assertTrue(logger.isEnabled(Level.ERROR));
		assertTrue(logger.isEnabled(Level.WARN));
		assertTrue(logger.isEnabled(Level.INFO));
		assertTrue(logger.isEnabled(Level.DEBUG));
		assertTrue(logger.isEnabled(Level.TRACE));

		Configurator.currentConfig().level(LoggingLevel.ERROR).activate();
		assertTrue(logger.isEnabled(Level.FATAL));
		assertTrue(logger.isEnabled(Level.ERROR));
		assertFalse(logger.isEnabled(Level.WARN));
		assertFalse(logger.isEnabled(Level.INFO));
		assertFalse(logger.isEnabled(Level.DEBUG));
		assertFalse(logger.isEnabled(Level.TRACE));

		Configurator.currentConfig().level(LoggingLevel.OFF).activate();
		assertFalse(logger.isEnabled(Level.FATAL));
		assertFalse(logger.isEnabled(Level.ERROR));
		assertFalse(logger.isEnabled(Level.WARN));
		assertFalse(logger.isEnabled(Level.INFO));
		assertFalse(logger.isEnabled(Level.DEBUG));
		assertFalse(logger.isEnabled(Level.TRACE));

		Configurator.currentConfig().level("org.apache", LoggingLevel.WARNING).activate();
		assertTrue(logger.isEnabled(Level.FATAL));
		assertTrue(logger.isEnabled(Level.ERROR));
		assertTrue(logger.isEnabled(Level.WARN));
		assertFalse(logger.isEnabled(Level.INFO));
		assertFalse(logger.isEnabled(Level.DEBUG));
		assertFalse(logger.isEnabled(Level.TRACE));
	}

	/**
	 * Test logging.
	 */
	@Test
	public final void testLogging() {
		Configurator.currentConfig().level(LoggingLevel.INFO).activate();

		logger.log(Level.TRACE, "Hello!");
		assertNull(writer.consumeLogEntry());

		logger.log(Level.DEBUG, "Hello!", new Exception());
		assertNull(writer.consumeLogEntry());

		logger.log(Level.INFO, "Hello!");
		assertEquals(new LogEntry(LoggingLevel.INFO, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		logger.log(Level.WARN, null, new Exception());
		assertEquals(new LogEntry(LoggingLevel.WARNING, Exception.class.getName() + NEW_LINE), writer.consumeLogEntry());

		logger.log(Level.ERROR, new StringBuilder("Hello!"));
		assertEquals(new LogEntry(LoggingLevel.ERROR, "Hello!" + NEW_LINE), writer.consumeLogEntry());

		Configurator.currentConfig().formatPattern("{class}").activate();
		logger.log(Level.FATAL, "Hello!");
		assertEquals(new LogEntry(LoggingLevel.ERROR, TinylogBridgeTest.class.getName() + NEW_LINE), writer.consumeLogEntry());
	}

	private static class SimpleLoggerWrapper {

		private Level getLoggingLevel() {
			return TinylogBride.getLoggingLevel();
		}

		private boolean isEnabled(final Priority level) {
			return TinylogBride.isEnabled(level);
		}

		private void log(final Priority level, final Object message) {
			TinylogBride.log(level, message);
		}

		private void log(final Priority level, final Object message, final Throwable throwable) {
			TinylogBride.log(level, message, throwable);
		}

	}

}
