/*
 * Copyright 2014 Martin Winandy
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
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.tinylog.AbstractTest;
import org.tinylog.Configurator;
import org.tinylog.Level;
import org.tinylog.LogEntry;
import org.tinylog.util.StoreWriter;

/**
 * Tests for SLF4J logger API.
 *
 * @see TinylogLogger
 */
public class TinylogLoggerTest extends AbstractTest {

	private static final Marker MARKER = new BasicMarkerFactory().getMarker(BasicMarker.ANY_MARKER);

	private StoreWriter writer;

	/**
	 * Set up logger.
	 */
	@Before
	public final void init() {
		writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).activate();
	}

	/**
	 * Test names of logger.
	 */
	@Test
	public final void testName() {
		TinylogLogger logger = new TinylogLogger(null);
		assertNull(logger.getName());

		logger = new TinylogLogger("");
		assertEquals("", logger.getName());

		logger = new TinylogLogger("abc");
		assertEquals("abc", logger.getName());
	}

	/**
	 * Test getter for logging level.
	 */
	@Test
	public final void testLoggingLevel() {
		TinylogLogger logger = new TinylogLogger(null);

		Configurator.currentConfig().level(org.tinylog.Level.TRACE).activate();
		assertTrue(logger.isTraceEnabled());
		assertTrue(logger.isTraceEnabled(MARKER));
		assertTrue(logger.isDebugEnabled());
		assertTrue(logger.isDebugEnabled(MARKER));
		assertTrue(logger.isInfoEnabled());
		assertTrue(logger.isInfoEnabled(MARKER));
		assertTrue(logger.isWarnEnabled());
		assertTrue(logger.isWarnEnabled(MARKER));
		assertTrue(logger.isErrorEnabled());
		assertTrue(logger.isErrorEnabled(MARKER));

		Configurator.currentConfig().level(org.tinylog.Level.DEBUG).activate();
		assertFalse(logger.isTraceEnabled());
		assertFalse(logger.isTraceEnabled(MARKER));
		assertTrue(logger.isDebugEnabled());
		assertTrue(logger.isDebugEnabled(MARKER));
		assertTrue(logger.isInfoEnabled());
		assertTrue(logger.isInfoEnabled(MARKER));
		assertTrue(logger.isWarnEnabled());
		assertTrue(logger.isWarnEnabled(MARKER));
		assertTrue(logger.isErrorEnabled());
		assertTrue(logger.isErrorEnabled(MARKER));

		Configurator.currentConfig().level(org.tinylog.Level.INFO).activate();
		assertFalse(logger.isTraceEnabled());
		assertFalse(logger.isTraceEnabled(MARKER));
		assertFalse(logger.isDebugEnabled());
		assertFalse(logger.isDebugEnabled(MARKER));
		assertTrue(logger.isInfoEnabled());
		assertTrue(logger.isInfoEnabled(MARKER));
		assertTrue(logger.isWarnEnabled());
		assertTrue(logger.isWarnEnabled(MARKER));
		assertTrue(logger.isErrorEnabled());
		assertTrue(logger.isErrorEnabled(MARKER));

		Configurator.currentConfig().level(org.tinylog.Level.WARNING).activate();
		assertFalse(logger.isTraceEnabled());
		assertFalse(logger.isTraceEnabled(null));
		assertFalse(logger.isDebugEnabled());
		assertFalse(logger.isDebugEnabled(null));
		assertFalse(logger.isInfoEnabled());
		assertFalse(logger.isInfoEnabled(null));
		assertTrue(logger.isWarnEnabled());
		assertTrue(logger.isWarnEnabled(null));
		assertTrue(logger.isErrorEnabled());
		assertTrue(logger.isErrorEnabled(null));

		Configurator.currentConfig().level(org.tinylog.Level.ERROR).activate();
		assertFalse(logger.isTraceEnabled());
		assertFalse(logger.isTraceEnabled(null));
		assertFalse(logger.isDebugEnabled());
		assertFalse(logger.isDebugEnabled(null));
		assertFalse(logger.isInfoEnabled());
		assertFalse(logger.isInfoEnabled(null));
		assertFalse(logger.isWarnEnabled());
		assertFalse(logger.isWarnEnabled(null));
		assertTrue(logger.isErrorEnabled());
		assertTrue(logger.isErrorEnabled(null));

		Configurator.currentConfig().level(org.tinylog.Level.OFF).activate();
		assertFalse(logger.isTraceEnabled());
		assertFalse(logger.isTraceEnabled(null));
		assertFalse(logger.isDebugEnabled());
		assertFalse(logger.isDebugEnabled(null));
		assertFalse(logger.isInfoEnabled());
		assertFalse(logger.isInfoEnabled(null));
		assertFalse(logger.isWarnEnabled());
		assertFalse(logger.isWarnEnabled(null));
		assertFalse(logger.isErrorEnabled());
		assertFalse(logger.isErrorEnabled(null));

	}

	/**
	 * Test trace logging.
	 */
	@Test
	public final void testTraceLogging() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		TinylogLogger logger = new TinylogLogger(null);

		logger.trace("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.trace(MARKER, "Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.trace("Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.trace(MARKER, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.trace("{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.trace(MARKER, "{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.trace("{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		logger.trace(MARKER, "{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		Exception exception = new Exception();
		logger.trace("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		exception = new Exception();
		logger.trace(MARKER, "Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}

	/**
	 * Test debug logging.
	 */
	@Test
	public final void testDebugLogging() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		TinylogLogger logger = new TinylogLogger(null);

		logger.debug("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.debug(MARKER, "Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.debug("Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.debug(MARKER, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.debug("{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.debug(MARKER, "{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.debug("{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		logger.debug(MARKER, "{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		Exception exception = new Exception();
		logger.debug("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		exception = new Exception();
		logger.debug(MARKER, "Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}

	/**
	 * Test info logging.
	 */
	@Test
	public final void testInfoLogging() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		TinylogLogger logger = new TinylogLogger(null);

		logger.info("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.info(MARKER, "Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.info("Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.info(MARKER, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.info("{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.info(MARKER, "{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.info("{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		logger.info(MARKER, "{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		Exception exception = new Exception();
		logger.info("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		exception = new Exception();
		logger.info(MARKER, "Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}

	/**
	 * Test warning logging.
	 */
	@Test
	public final void testWarningLogging() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		TinylogLogger logger = new TinylogLogger(null);

		logger.warn("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.warn(MARKER, "Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.warn("Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.warn(MARKER, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.warn("{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.warn(MARKER, "{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.warn("{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		logger.warn(MARKER, "{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		Exception exception = new Exception();
		logger.warn("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		exception = new Exception();
		logger.warn(MARKER, "Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}

	/**
	 * Test error logging.
	 */
	@Test
	public final void testErrorLogging() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		TinylogLogger logger = new TinylogLogger(null);

		logger.error("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.error(MARKER, "Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.error("Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.error(MARKER, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		logger.error("{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.error(MARKER, "{} {}!", "Hi", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hi World!", logEntry.getMessage());

		logger.error("{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		logger.error(MARKER, "{}, {} and {}", "a", "b", "c");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("a, b and c", logEntry.getMessage());

		Exception exception = new Exception();
		logger.error("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		exception = new Exception();
		logger.error(MARKER, "Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		exception = new Exception();
		logger.error(MARKER, "Failed {}", "Here", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Failed Here", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}

}
