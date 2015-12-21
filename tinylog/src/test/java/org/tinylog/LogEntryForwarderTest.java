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

package org.tinylog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.tinylog.Configurator;
import org.tinylog.Level;
import org.tinylog.LogEntry;
import org.tinylog.LogEntryForwarder;
import org.tinylog.util.StoreWriter;
import org.tinylog.writers.LogEntryValue;

/**
 * Tests for the logging facade API.
 *
 * @see LogEntryForwarder
 */
public class LogEntryForwarderTest extends AbstractTest {

	/**
	 * Test if the class is a valid utility class.
	 *
	 * @see AbstractTest#testIfValidUtilityClass(Class)
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(LogEntryForwarder.class);
	}

	/**
	 * Test logging level enabled tester by stack trace deep.
	 */
	@Test
	public final void testIsEnabledByStackTraceDeep() {
		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertTrue(LogEntryForwarder.isEnabled(0, Level.TRACE));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.DEBUG));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.INFO));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.WARNING));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.ERROR));

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertFalse(LogEntryForwarder.isEnabled(0, Level.TRACE));
		assertFalse(LogEntryForwarder.isEnabled(0, Level.DEBUG));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.INFO));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.WARNING));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.ERROR));

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertFalse(LogEntryForwarder.isEnabled(0, Level.TRACE));
		assertFalse(LogEntryForwarder.isEnabled(0, Level.DEBUG));
		assertFalse(LogEntryForwarder.isEnabled(0, Level.INFO));
		assertFalse(LogEntryForwarder.isEnabled(0, Level.WARNING));
		assertFalse(LogEntryForwarder.isEnabled(0, Level.WARNING));

		Configurator.defaultConfig().level(Level.WARNING).level(LogEntryForwarderTest.class, Level.DEBUG).activate();
		assertFalse(LogEntryForwarder.isEnabled(0, Level.TRACE));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.DEBUG));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.INFO));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.WARNING));
		assertTrue(LogEntryForwarder.isEnabled(0, Level.ERROR));
	}

	/**
	 * Test logging level enabled tester by stack trace element.
	 */
	@Test
	public final void testIsEnabledByStackTraceElement() {
		StackTraceElement stackTraceElement = new StackTraceElement("MyClass", "?", "?", -1);

		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.TRACE));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.DEBUG));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.INFO));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.WARNING));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.ERROR));

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertFalse(LogEntryForwarder.isEnabled(stackTraceElement, Level.TRACE));
		assertFalse(LogEntryForwarder.isEnabled(stackTraceElement, Level.DEBUG));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.INFO));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.WARNING));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.ERROR));

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertFalse(LogEntryForwarder.isEnabled(stackTraceElement, Level.TRACE));
		assertFalse(LogEntryForwarder.isEnabled(stackTraceElement, Level.DEBUG));
		assertFalse(LogEntryForwarder.isEnabled(stackTraceElement, Level.INFO));
		assertFalse(LogEntryForwarder.isEnabled(stackTraceElement, Level.WARNING));
		assertFalse(LogEntryForwarder.isEnabled(stackTraceElement, Level.WARNING));

		Configurator.defaultConfig().level(Level.WARNING).level("MyClass", Level.DEBUG).activate();
		assertFalse(LogEntryForwarder.isEnabled(stackTraceElement, Level.TRACE));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.DEBUG));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.INFO));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.WARNING));
		assertTrue(LogEntryForwarder.isEnabled(stackTraceElement, Level.ERROR));
	}

	/**
	 * Test getting logging level by stack trace deep.
	 */
	@Test
	public final void testGetLevelByStackTraceDeep() {
		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertEquals(Level.TRACE, LogEntryForwarder.getLevel(0));

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertEquals(Level.INFO, LogEntryForwarder.getLevel(0));

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertEquals(Level.OFF, LogEntryForwarder.getLevel(0));

		Configurator.defaultConfig().level(Level.WARNING).level(LogEntryForwarderTest.class, Level.DEBUG).activate();
		assertEquals(Level.DEBUG, LogEntryForwarder.getLevel(0));
	}

	/**
	 * Test getting logging level by stack trace element.
	 */
	@Test
	public final void testGetLevelByStackTraceElement() {
		StackTraceElement stackTraceElement = new StackTraceElement("MyClass", "?", "?", -1);

		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertEquals(Level.TRACE, LogEntryForwarder.getLevel(stackTraceElement));

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertEquals(Level.INFO, LogEntryForwarder.getLevel(stackTraceElement));

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertEquals(Level.OFF, LogEntryForwarder.getLevel(stackTraceElement));

		Configurator.defaultConfig().level(Level.WARNING).level("MyClass", Level.DEBUG).activate();
		assertEquals(Level.DEBUG, LogEntryForwarder.getLevel(stackTraceElement));
	}

	/**
	 * Test the default forward methods.
	 */
	@Test
	public final void testLogging() {
		StoreWriter writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.FILE, LogEntryValue.MESSAGE);
		Configurator.defaultConfig().writer(writer).level(Level.TRACE).activate();

		LogEntryForwarder.forward(0, Level.INFO, "Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("LogEntryForwarderTest.java", logEntry.getFilename());
		assertEquals("Hello!", logEntry.getMessage());

		LogEntryForwarder.forward(0, Level.INFO, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("LogEntryForwarderTest.java", logEntry.getFilename());
		assertEquals("Hello World!", logEntry.getMessage());

		Exception exception = new Exception();
		LogEntryForwarder.forward(0, Level.ERROR, exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("LogEntryForwarderTest.java", logEntry.getFilename());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}

	/**
	 * Test the the advanced forward methods with given stack trace elements.
	 */
	@Test
	public final void testLoggingWithStackTraceElement() {
		StackTraceElement stackTraceElement = new StackTraceElement("MyClass", "?", "?", -1);
		StoreWriter writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.CLASS, LogEntryValue.MESSAGE);
		Configurator.defaultConfig().writer(writer).level(Level.TRACE).activate();

		LogEntryForwarder.forward(stackTraceElement, Level.INFO, "Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("MyClass", logEntry.getClassName());
		assertEquals("Hello!", logEntry.getMessage());

		LogEntryForwarder.forward(stackTraceElement, Level.INFO, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("MyClass", logEntry.getClassName());
		assertEquals("Hello World!", logEntry.getMessage());

		Exception exception = new Exception();
		LogEntryForwarder.forward(stackTraceElement, Level.ERROR, exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("MyClass", logEntry.getClassName());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}

}
