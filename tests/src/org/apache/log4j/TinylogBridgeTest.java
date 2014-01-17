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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.writers.LogEntry;
import org.pmw.tinylog.writers.LogEntryValue;

/**
 * Tests for tinylog bridge.
 * 
 * @see TinylogBride
 */
public class TinylogBridgeTest extends AbstractTest {

	private SimpleLog4Facade logger;
	private StoreWriter writer;

	/**
	 * Set up logger.
	 */
	@Before
	public final void init() {
		logger = new SimpleLog4Facade();
		writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).activate();
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
		Exception exception = new Exception();

		logger.log(Level.TRACE, "Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertNull(logEntry);

		logger.log(Level.DEBUG, "Hello!", exception);
		logEntry = writer.consumeLogEntry();
		assertNull(logEntry);

		logger.log(Level.INFO, "Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.log(Level.WARN, null, exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.WARNING, logEntry.getLoggingLevel());
		assertEquals(exception, logEntry.getException());

		logger.log(Level.ERROR, new StringBuilder("Hello!"));
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.log(Level.FATAL, "Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals("Hello!", logEntry.getMessage());
	}

	/**
	 * Test computing class name.
	 */
	@Test
	public final void testClassName() {
		writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.CLASS);
		Configurator.currentConfig().writer(writer).level(LoggingLevel.INFO).activate();

		logger.log(Level.INFO, "Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals(TinylogBridgeTest.class.getName(), logEntry.getClassName());

		logger.log(Level.ERROR, "Hello!", new Exception());
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals(TinylogBridgeTest.class.getName(), logEntry.getClassName());
	}

}
