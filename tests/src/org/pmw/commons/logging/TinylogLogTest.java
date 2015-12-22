/*
 * Copyright 2015 Martin Winandy
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

package org.pmw.commons.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.LogEntry;
import org.pmw.tinylog.util.StoreWriter;

/**
 * Tests for Apache Commons Logging log API.
 *
 * @see TinylogLog
 */
public class TinylogLogTest extends AbstractTest {

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
	 * Test getter for logging level.
	 */
	@Test
	public final void testLoggingLevel() {
		TinylogLog log = new TinylogLog(null);

		Configurator.currentConfig().level(Level.TRACE).activate();
		assertTrue(log.isTraceEnabled());
		assertTrue(log.isDebugEnabled());
		assertTrue(log.isInfoEnabled());
		assertTrue(log.isWarnEnabled());
		assertTrue(log.isErrorEnabled());
		assertTrue(log.isFatalEnabled());

		Configurator.currentConfig().level(Level.DEBUG).activate();
		assertFalse(log.isTraceEnabled());
		assertTrue(log.isDebugEnabled());
		assertTrue(log.isInfoEnabled());
		assertTrue(log.isWarnEnabled());
		assertTrue(log.isErrorEnabled());
		assertTrue(log.isFatalEnabled());

		Configurator.currentConfig().level(Level.INFO).activate();
		assertFalse(log.isTraceEnabled());
		assertFalse(log.isDebugEnabled());
		assertTrue(log.isInfoEnabled());
		assertTrue(log.isWarnEnabled());
		assertTrue(log.isErrorEnabled());
		assertTrue(log.isFatalEnabled());

		Configurator.currentConfig().level(Level.WARNING).activate();
		assertFalse(log.isTraceEnabled());
		assertFalse(log.isDebugEnabled());
		assertFalse(log.isInfoEnabled());
		assertTrue(log.isWarnEnabled());
		assertTrue(log.isErrorEnabled());
		assertTrue(log.isFatalEnabled());

		Configurator.currentConfig().level(Level.ERROR).activate();
		assertFalse(log.isTraceEnabled());
		assertFalse(log.isDebugEnabled());
		assertFalse(log.isInfoEnabled());
		assertFalse(log.isWarnEnabled());
		assertTrue(log.isErrorEnabled());
		assertTrue(log.isFatalEnabled());

		Configurator.currentConfig().level(Level.OFF).activate();
		assertFalse(log.isTraceEnabled());
		assertFalse(log.isDebugEnabled());
		assertFalse(log.isInfoEnabled());
		assertFalse(log.isWarnEnabled());
		assertFalse(log.isErrorEnabled());
		assertFalse(log.isFatalEnabled());
	}

	/**
	 * Test trace logging.
	 */
	@Test
	public final void testTraceLogging() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		TinylogLog log = new TinylogLog(null);

		log.trace("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Exception exception = new Exception();
		log.trace("Failed", exception);
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

		TinylogLog log = new TinylogLog(null);

		log.debug("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Exception exception = new Exception();
		log.debug("Failed", exception);
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

		TinylogLog log = new TinylogLog(null);

		log.info("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Exception exception = new Exception();
		log.info("Failed", exception);
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

		TinylogLog log = new TinylogLog(null);

		log.warn("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Exception exception = new Exception();
		log.warn("Failed", exception);
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

		TinylogLog log = new TinylogLog(null);

		log.error("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Exception exception = new Exception();
		log.error("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}

	/**
	 * Test fatal logging.
	 */
	@Test
	public final void testFatalLogging() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		TinylogLog log = new TinylogLog(null);

		log.fatal("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		Exception exception = new Exception();
		log.fatal("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}
	/**
	 * Test ignoring message object if it is the same as the passed throwable.
	 */
	@Test
	public final void testDuplicateException() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		TinylogLog log = new TinylogLog(null);
		Throwable throwable = new Throwable();

		log.trace(throwable, throwable);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertNull(logEntry.getMessage());
		assertSame(throwable, logEntry.getException());

		log.debug(throwable, throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertNull(logEntry.getMessage());
		assertSame(throwable, logEntry.getException());

		log.info(throwable, throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertNull(logEntry.getMessage());
		assertSame(throwable, logEntry.getException());

		log.warn(throwable, throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertNull(logEntry.getMessage());
		assertSame(throwable, logEntry.getException());

		log.error(throwable, throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertNull(logEntry.getMessage());
		assertSame(throwable, logEntry.getException());

		log.fatal(throwable, throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertNull(logEntry.getMessage());
		assertSame(throwable, logEntry.getException());
	}

}
