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

package org.slf4j.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.LogEntry;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.writers.LogEntryValue;

/**
 * Tests for tinylog bridge.
 *
 * @see TinylogBridge
 */
public class TinylogBridgeTest extends AbstractTest {

	private SimpleLogger logger;
	private StoreWriter writer;

	/**
	 * Set up logger.
	 */
	@Before
	public final void init() {
		logger = new SimpleLogger();
		writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).activate();
	}

	/**
	 * Test if a specified logging level is enabled.
	 */
	@Test
	public final void testLoggingLevelEnabled() {
		Configurator.currentConfig().level(Level.TRACE).activate();
		assertTrue(logger.isEnabled(Level.ERROR));
		assertTrue(logger.isEnabled(Level.WARNING));
		assertTrue(logger.isEnabled(Level.INFO));
		assertTrue(logger.isEnabled(Level.DEBUG));
		assertTrue(logger.isEnabled(Level.TRACE));

		Configurator.currentConfig().level(Level.ERROR).activate();
		assertTrue(logger.isEnabled(Level.ERROR));
		assertFalse(logger.isEnabled(Level.WARNING));
		assertFalse(logger.isEnabled(Level.INFO));
		assertFalse(logger.isEnabled(Level.DEBUG));
		assertFalse(logger.isEnabled(Level.TRACE));

		Configurator.currentConfig().level(Level.OFF).activate();
		assertFalse(logger.isEnabled(Level.ERROR));
		assertFalse(logger.isEnabled(Level.WARNING));
		assertFalse(logger.isEnabled(Level.INFO));
		assertFalse(logger.isEnabled(Level.DEBUG));
		assertFalse(logger.isEnabled(Level.TRACE));
	}

	/**
	 * Test logging.
	 */
	@Test
	public final void testLogging() {
		Configurator.currentConfig().level(Level.INFO).activate();
		Exception exception = new Exception();

		logger.log(Level.TRACE, "Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertNull(logEntry);

		logger.log(Level.DEBUG, "Hello!", exception);
		logEntry = writer.consumeLogEntry();
		assertNull(logEntry);

		logger.log(Level.INFO, "Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.log(Level.INFO, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.log(Level.INFO, "{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.log(Level.INFO, "{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		logger.log(Level.ERROR, "Error!", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Error!", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		logger.log(Level.ERROR, "Failed {}", "Here", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Failed Here", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}

	/**
	 * Test computing class name.
	 */
	@Test
	public final void testClassName() {
		writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.CLASS);
		Configurator.currentConfig().writer(writer).level(Level.INFO).activate();

		logger.log(Level.INFO, "Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(TinylogBridgeTest.class.getName(), logEntry.getClassName());

		logger.log(Level.ERROR, "Hello!", new Exception());
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals(TinylogBridgeTest.class.getName(), logEntry.getClassName());
	}

}
