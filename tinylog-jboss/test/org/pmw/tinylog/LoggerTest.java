/*
 * Copyright 2016 Martin Winandy
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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.pmw.tinylog.jboss.logging.LogEntry;
import org.pmw.tinylog.jboss.logging.StorageLogger;

/**
 * Tests for the logger.
 *
 * @see Logger
 */
public class LoggerTest extends AbstractJBossTest {

	/**
	 * Test if the class is a valid utility class.
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(Logger.class);
	}

	/**
	 * Test getting global logging level.
	 */
	@Test
	public final void testLoggingLevel() {
		setSeverityLevel(Level.TRACE, "");
		assertEquals(Level.TRACE, Logger.getLevel());

		setSeverityLevel(Level.DEBUG, "");
		assertEquals(Level.DEBUG, Logger.getLevel());

		setSeverityLevel(Level.INFO, "");
		assertEquals(Level.INFO, Logger.getLevel());

		setSeverityLevel(Level.WARNING, "");
		assertEquals(Level.WARNING, Logger.getLevel());

		setSeverityLevel(Level.ERROR, "");
		assertEquals(Level.ERROR, Logger.getLevel());

		setSeverityLevel(Level.OFF, "");
		assertEquals(Level.OFF, Logger.getLevel());
	}
	
	/**
	 * Test getting logging level for specific name.
	 */
	@Test
	public final void testLoggingLevelForName() {
		setSeverityLevel(Level.TRACE, "a");
		setSeverityLevel(Level.INFO, "a.b");

		assertEquals(Level.TRACE, Logger.getLevel("a"));
		assertEquals(Level.INFO, Logger.getLevel("a.b"));
		assertEquals(Level.TRACE, Logger.getLevel("a.c"));
		assertEquals(Level.INFO, Logger.getLevel("a.b.d"));
		assertEquals(Level.TRACE, Logger.getLevel("a.c.d"));
	}
	
	/**
	 * Test getting logging level for a package.
	 */
	@Test
	public final void testLoggingLevelForPackage() {
		setSeverityLevel(Level.DEBUG, LoggerTest.class.getPackage().getName());

		assertEquals(Level.DEBUG, Logger.getLevel(LoggerTest.class.getPackage()));
		assertEquals(Level.DEBUG, Logger.getLevel(LoggerTest.class));

		setSeverityLevel(Level.TRACE, LoggerTest.class.getName());

		assertEquals(Level.DEBUG, Logger.getLevel(LoggerTest.class.getPackage()));
		assertEquals(Level.TRACE, Logger.getLevel(LoggerTest.class));
	}

	/**
	 * Test trace logging methods if trace severity level is enabled.
	 */
	@Test
	public final void testEnabledTrace() {
		setSeverityLevel(Level.TRACE, LoggerTest.class.getName());
		
		StorageLogger jbossLogger = getLogger(LoggerTest.class.getName());
		Exception exception = new Exception();
		
		Logger.trace(Math.PI);
		assertEquals(createTraceLogEntrySingleton(Math.PI, null), jbossLogger.consumeLogEntries());
		
		Logger.trace("Hello!");
		assertEquals(createTraceLogEntrySingleton("Hello!", null), jbossLogger.consumeLogEntries());

		Logger.trace("Hello {}!", "World");
		assertEquals(createTraceLogEntrySingleton("Hello World!", null), jbossLogger.consumeLogEntries());
		
		Logger.trace(exception);
		assertEquals(createTraceLogEntrySingleton(exception, exception), jbossLogger.consumeLogEntries());
		
		Logger.trace(exception, "Test");
		assertEquals(createTraceLogEntrySingleton("Test", exception), jbossLogger.consumeLogEntries());
	}

	/**
	 * Test trace logging methods if trace severity level is disabled.
	 */
	@Test
	public final void testDisabledTrace() {
		setSeverityLevel(Level.DEBUG, LoggerTest.class.getName());

		StorageLogger jbossLogger = getLogger(LoggerTest.class.getName());
		Exception exception = new Exception();
		
		Logger.trace(Math.PI);
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.trace("Hello!");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());

		Logger.trace("Hello {}!", "World");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.trace(exception);
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.trace(exception, "Test");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
	}
	
	/**
	 * Test debug logging methods if debug severity level is enabled.
	 */
	@Test
	public final void testEnabledDebug() {
		setSeverityLevel(Level.DEBUG, LoggerTest.class.getName());
		
		StorageLogger jbossLogger = getLogger(LoggerTest.class.getName());
		Exception exception = new Exception();
		
		Logger.debug(Math.PI);
		assertEquals(createDebugLogEntrySingleton(Math.PI, null), jbossLogger.consumeLogEntries());
		
		Logger.debug("Hello!");
		assertEquals(createDebugLogEntrySingleton("Hello!", null), jbossLogger.consumeLogEntries());

		Logger.debug("Hello {}!", "World");
		assertEquals(createDebugLogEntrySingleton("Hello World!", null), jbossLogger.consumeLogEntries());
		
		Logger.debug(exception);
		assertEquals(createDebugLogEntrySingleton(exception, exception), jbossLogger.consumeLogEntries());
		
		Logger.debug(exception, "Test");
		assertEquals(createDebugLogEntrySingleton("Test", exception), jbossLogger.consumeLogEntries());
	}

	/**
	 * Test debug logging methods if debug severity level is disabled.
	 */
	@Test
	public final void testDisabledDebug() {
		setSeverityLevel(Level.INFO, LoggerTest.class.getName());

		StorageLogger jbossLogger = getLogger(LoggerTest.class.getName());
		Exception exception = new Exception();
		
		Logger.debug(Math.PI);
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.debug("Hello!");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());

		Logger.debug("Hello {}!", "World");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.debug(exception);
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.debug(exception, "Test");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
	}
	
	/**
	 * Test info logging methods if info severity level is enabled.
	 */
	@Test
	public final void testEnabledInfo() {
		setSeverityLevel(Level.INFO, LoggerTest.class.getName());
		
		StorageLogger jbossLogger = getLogger(LoggerTest.class.getName());
		Exception exception = new Exception();
		
		Logger.info(Math.PI);
		assertEquals(createInfoLogEntrySingleton(Math.PI, null), jbossLogger.consumeLogEntries());
		
		Logger.info("Hello!");
		assertEquals(createInfoLogEntrySingleton("Hello!", null), jbossLogger.consumeLogEntries());

		Logger.info("Hello {}!", "World");
		assertEquals(createInfoLogEntrySingleton("Hello World!", null), jbossLogger.consumeLogEntries());
		
		Logger.info(exception);
		assertEquals(createInfoLogEntrySingleton(exception, exception), jbossLogger.consumeLogEntries());
		
		Logger.info(exception, "Test");
		assertEquals(createInfoLogEntrySingleton("Test", exception), jbossLogger.consumeLogEntries());
	}

	/**
	 * Test info logging methods if info severity level is disabled.
	 */
	@Test
	public final void testDisabledInfo() {
		setSeverityLevel(Level.WARNING, LoggerTest.class.getName());

		StorageLogger jbossLogger = getLogger(LoggerTest.class.getName());
		Exception exception = new Exception();
		
		Logger.info(Math.PI);
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.info("Hello!");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());

		Logger.info("Hello {}!", "World");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.info(exception);
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.info(exception, "Test");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
	}
	
	/**
	 * Test warning logging methods if warning severity level is enabled.
	 */
	@Test
	public final void testEnabledWarning() {
		setSeverityLevel(Level.WARNING, LoggerTest.class.getName());
		
		StorageLogger jbossLogger = getLogger(LoggerTest.class.getName());
		Exception exception = new Exception();
		
		Logger.warn(Math.PI);
		assertEquals(createWarningLogEntrySingleton(Math.PI, null), jbossLogger.consumeLogEntries());
		
		Logger.warn("Hello!");
		assertEquals(createWarningLogEntrySingleton("Hello!", null), jbossLogger.consumeLogEntries());

		Logger.warn("Hello {}!", "World");
		assertEquals(createWarningLogEntrySingleton("Hello World!", null), jbossLogger.consumeLogEntries());
		
		Logger.warn(exception);
		assertEquals(createWarningLogEntrySingleton(exception, exception), jbossLogger.consumeLogEntries());
		
		Logger.warn(exception, "Test");
		assertEquals(createWarningLogEntrySingleton("Test", exception), jbossLogger.consumeLogEntries());
	}

	/**
	 * Test warning logging methods if warning severity level is disabled.
	 */
	@Test
	public final void testDisabledWarning() {
		setSeverityLevel(Level.ERROR, LoggerTest.class.getName());

		StorageLogger jbossLogger = getLogger(LoggerTest.class.getName());
		Exception exception = new Exception();
		
		Logger.warn(Math.PI);
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.warn("Hello!");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());

		Logger.warn("Hello {}!", "World");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.warn(exception);
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.warn(exception, "Test");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
	}
	
	/**
	 * Test error logging methods if error severity level is enabled.
	 */
	@Test
	public final void testEnabledError() {
		setSeverityLevel(Level.ERROR, LoggerTest.class.getName());
		
		StorageLogger jbossLogger = getLogger(LoggerTest.class.getName());
		Exception exception = new Exception();
		
		Logger.error(Math.PI);
		assertEquals(createErrorLogEntrySingleton(Math.PI, null), jbossLogger.consumeLogEntries());
		
		Logger.error("Hello!");
		assertEquals(createErrorLogEntrySingleton("Hello!", null), jbossLogger.consumeLogEntries());

		Logger.error("Hello {}!", "World");
		assertEquals(createErrorLogEntrySingleton("Hello World!", null), jbossLogger.consumeLogEntries());
		
		Logger.error(exception);
		assertEquals(createErrorLogEntrySingleton(exception, exception), jbossLogger.consumeLogEntries());
		
		Logger.error(exception, "Test");
		assertEquals(createErrorLogEntrySingleton("Test", exception), jbossLogger.consumeLogEntries());
	}

	/**
	 * Test error logging methods if error severity level is disabled.
	 */
	@Test
	public final void testDisabledError() {
		setSeverityLevel(Level.OFF, LoggerTest.class.getName());

		StorageLogger jbossLogger = getLogger(LoggerTest.class.getName());
		Exception exception = new Exception();
		
		Logger.error(Math.PI);
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.error("Hello!");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());

		Logger.error("Hello {}!", "World");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.error(exception);
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
		
		Logger.error(exception, "Test");
		assertEquals(emptyList(), jbossLogger.consumeLogEntries());
	}

	private static StorageLogger getLogger(final String name) {
		return (StorageLogger) org.jboss.logging.Logger.getLogger(name);
	}
	
	private static void setSeverityLevel(final Level level, final String name) {
		getLogger(name).setLevel(level);
	}
	
	private static List<LogEntry> createTraceLogEntrySingleton(final Object message, final Throwable exception) {
		return singletonList(new LogEntry(org.jboss.logging.Logger.Level.TRACE, message, exception));
	}
	
	private static List<LogEntry> createDebugLogEntrySingleton(final Object message, final Throwable exception) {
		return singletonList(new LogEntry(org.jboss.logging.Logger.Level.DEBUG, message, exception));
	}
	
	private static List<LogEntry> createInfoLogEntrySingleton(final Object message, final Throwable exception) {
		return singletonList(new LogEntry(org.jboss.logging.Logger.Level.INFO, message, exception));
	}
	
	private static List<LogEntry> createWarningLogEntrySingleton(final Object message, final Throwable exception) {
		return singletonList(new LogEntry(org.jboss.logging.Logger.Level.WARN, message, exception));
	}
	
	private static List<LogEntry> createErrorLogEntrySingleton(final Object message, final Throwable exception) {
		return singletonList(new LogEntry(org.jboss.logging.Logger.Level.ERROR, message, exception));
	}

}
