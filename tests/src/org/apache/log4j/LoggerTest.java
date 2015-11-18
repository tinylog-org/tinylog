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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.LogEntry;
import org.pmw.tinylog.util.MappedResourceBundle;
import org.pmw.tinylog.util.StoreWriter;

/**
 * Tests for log4j logging API.
 * 
 * @see Logger
 */
public class LoggerTest extends AbstractTest {

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
	 * Test the root logger.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public final void testRoot() {
		Logger root = Logger.getRootLogger();
		assertNotNull(root);
		assertEquals("root", root.getName());
		assertNull(root.getParent());

		assertSame(root, Logger.getRootLogger());
		assertSame(root, Logger.getRoot());
	}

	/**
	 * Test getting loggers for classes and names.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public final void testGettingLoggers() {
		Logger testLogger = Logger.getLogger(LoggerTest.class);
		assertNotNull(testLogger);
		assertEquals(LoggerTest.class.getName(), testLogger.getName());
		assertSame(testLogger, Logger.getInstance(LoggerTest.class));

		Logger orgLogger = Logger.getLogger("org");
		assertNotNull(orgLogger);
		assertEquals("org", orgLogger.getName());
		assertSame(orgLogger, Logger.getInstance("org"));

		assertNotSame(testLogger, orgLogger);
	}

	/**
	 * Test getter for logging level.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public final void testLoggingLevel() {
		Logger logger = Logger.getRootLogger();

		Configurator.currentConfig().level(org.pmw.tinylog.Level.TRACE).activate();
		assertTrue(logger.isTraceEnabled());
		assertTrue(logger.isDebugEnabled());
		assertTrue(logger.isInfoEnabled());
		assertTrue(logger.isEnabledFor(Level.INFO));
		assertEquals(Level.TRACE, logger.getChainedPriority());
		assertEquals(Level.TRACE, logger.getPriority());
		assertEquals(Level.TRACE, logger.getEffectiveLevel());
		assertEquals(Level.TRACE, logger.getLevel());

		Configurator.currentConfig().level(org.pmw.tinylog.Level.DEBUG).activate();
		assertFalse(logger.isTraceEnabled());
		assertTrue(logger.isDebugEnabled());
		assertTrue(logger.isInfoEnabled());
		assertTrue(logger.isEnabledFor(Level.INFO));
		assertEquals(Level.DEBUG, logger.getChainedPriority());
		assertEquals(Level.DEBUG, logger.getPriority());
		assertEquals(Level.DEBUG, logger.getEffectiveLevel());
		assertEquals(Level.DEBUG, logger.getLevel());

		Configurator.currentConfig().level(org.pmw.tinylog.Level.INFO).activate();
		assertFalse(logger.isTraceEnabled());
		assertFalse(logger.isDebugEnabled());
		assertTrue(logger.isInfoEnabled());
		assertTrue(logger.isEnabledFor(Level.INFO));
		assertEquals(Level.INFO, logger.getChainedPriority());
		assertEquals(Level.INFO, logger.getPriority());
		assertEquals(Level.INFO, logger.getEffectiveLevel());
		assertEquals(Level.INFO, logger.getLevel());

		Configurator.currentConfig().level(org.pmw.tinylog.Level.WARNING).activate();
		assertFalse(logger.isTraceEnabled());
		assertFalse(logger.isDebugEnabled());
		assertFalse(logger.isInfoEnabled());
		assertFalse(logger.isEnabledFor(Level.INFO));
		assertEquals(Level.WARN, logger.getChainedPriority());
		assertEquals(Level.WARN, logger.getPriority());
		assertEquals(Level.WARN, logger.getEffectiveLevel());
		assertEquals(Level.WARN, logger.getLevel());

		Configurator.currentConfig().level(org.pmw.tinylog.Level.ERROR).activate();
		assertFalse(logger.isTraceEnabled());
		assertFalse(logger.isDebugEnabled());
		assertFalse(logger.isInfoEnabled());
		assertFalse(logger.isEnabledFor(Level.INFO));
		assertEquals(Level.ERROR, logger.getChainedPriority());
		assertEquals(Level.ERROR, logger.getPriority());
		assertEquals(Level.ERROR, logger.getEffectiveLevel());
		assertEquals(Level.ERROR, logger.getLevel());

		Configurator.currentConfig().level(org.pmw.tinylog.Level.OFF).activate();
		assertFalse(logger.isTraceEnabled());
		assertFalse(logger.isDebugEnabled());
		assertFalse(logger.isInfoEnabled());
		assertFalse(logger.isEnabledFor(Level.INFO));
		assertEquals(Level.OFF, logger.getChainedPriority());
		assertEquals(Level.OFF, logger.getPriority());
		assertEquals(Level.OFF, logger.getEffectiveLevel());
		assertEquals(Level.OFF, logger.getLevel());
	}

	/**
	 * Test logging.
	 */
	@Test
	public final void testLogging() {
		Configurator.currentConfig().level(org.pmw.tinylog.Level.TRACE).activate();

		Logger logger = Logger.getRootLogger();
		Exception exception = new Exception();

		logger.trace("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.TRACE, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());
		logger.trace("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.TRACE, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		logger.debug("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());
		logger.debug("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		logger.info("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.INFO, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());
		logger.info("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.INFO, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		logger.warn("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.WARNING, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());
		logger.warn("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.WARNING, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		logger.error("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.ERROR, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());
		logger.error("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.ERROR, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		logger.fatal("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.ERROR, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());
		logger.fatal("Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.ERROR, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		Configurator.currentConfig().level(org.pmw.tinylog.Level.INFO).activate();

		logger.log(Level.TRACE, "Hello!");
		assertNull(writer.consumeLogEntry());
		logger.log(Level.FATAL, "Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.ERROR, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		logger.log(Level.TRACE, "Failed", exception);
		assertNull(writer.consumeLogEntry());
		logger.log(Level.FATAL, "Failed", exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.ERROR, logEntry.getLevel());
		assertEquals("Failed", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}
	
	/**
	 * Test localized logging with resource bundle.
	 */
	@Test
	public final void testLocalizedLogging() {
		Configurator.currentConfig().level(org.pmw.tinylog.Level.INFO).activate();

		Throwable throwable = new Throwable();

		Logger child = Logger.getLogger("a.b");
		assertNull(child.getResourceBundle());
		Category parent = child.getParent();
		assertNull(parent.getResourceBundle());

		/* No resource bundle */

		child.l7dlog(Level.INFO, "common", throwable);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.INFO, logEntry.getLevel());
		assertEquals("common", logEntry.getMessage());
		assertEquals(throwable, logEntry.getException());

		child.l7dlog(Level.INFO, "named", new Object[] { "Java" }, throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.INFO, logEntry.getLevel());
		assertEquals("named", logEntry.getMessage());
		assertEquals(throwable, logEntry.getException());

		/* Set resource bundle */

		MappedResourceBundle bundle = new MappedResourceBundle();
		bundle.put("common", "Hello World");
		bundle.put("named", "Hello {0}!");
		parent.setResourceBundle(bundle);

		assertSame(bundle, parent.getResourceBundle());
		assertSame(bundle, child.getResourceBundle());

		/* Silent */

		child.l7dlog(Level.DEBUG, "common", throwable);
		assertNull(writer.consumeLogEntry());

		child.l7dlog(Level.DEBUG, "named", new Object[] { "Java" }, throwable);
		assertNull(writer.consumeLogEntry());

		parent.l7dlog(Level.DEBUG, "common", throwable);
		assertNull(writer.consumeLogEntry());

		parent.l7dlog(Level.DEBUG, "named", new Object[] { "Java" }, throwable);
		assertNull(writer.consumeLogEntry());

		parent.l7dlog(Level.DEBUG, "invalid", throwable);
		assertNull(writer.consumeLogEntry());

		parent.l7dlog(Level.DEBUG, "invalid", new Object[] { "Java" }, throwable);
		assertNull(writer.consumeLogEntry());

		/* Output */

		child.l7dlog(Level.INFO, "common", throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.INFO, logEntry.getLevel());
		assertEquals("Hello World", logEntry.getMessage());
		assertEquals(throwable, logEntry.getException());

		child.l7dlog(Level.INFO, "named", new Object[] { "Java" }, throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.INFO, logEntry.getLevel());
		assertEquals("Hello Java!", logEntry.getMessage());
		assertEquals(throwable, logEntry.getException());

		parent.l7dlog(Level.INFO, "common", throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.INFO, logEntry.getLevel());
		assertEquals("Hello World", logEntry.getMessage());
		assertEquals(throwable, logEntry.getException());

		parent.l7dlog(Level.INFO, "named", new Object[] { "Java" }, throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.INFO, logEntry.getLevel());
		assertEquals("Hello Java!", logEntry.getMessage());
		assertEquals(throwable, logEntry.getException());

		parent.l7dlog(Level.INFO, "invalid", throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.INFO, logEntry.getLevel());
		assertEquals("invalid", logEntry.getMessage());
		assertEquals(throwable, logEntry.getException());

		parent.l7dlog(Level.INFO, "invalid", new Object[] { "Java" }, throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.INFO, logEntry.getLevel());
		assertEquals("invalid", logEntry.getMessage());
		assertEquals(throwable, logEntry.getException());
	}

	/**
	 * Test logging with assertions.
	 */
	@Test
	public final void testLoggingWithAssertions() {
		Logger logger = Logger.getRootLogger();

		logger.assertLog(true, "Hello!");
		assertNull(writer.consumeLogEntry());

		logger.assertLog(false, "Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.ERROR, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());
	}

}
