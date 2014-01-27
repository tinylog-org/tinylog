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

import org.junit.Test;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.writers.LogEntry;
import org.pmw.tinylog.writers.LogEntryValue;

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
	 * Test the default forward methods.
	 */
	@Test
	public final void testLogging() {
		StoreWriter writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.FILE, LogEntryValue.MESSAGE);
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.TRACE).activate();

		LogEntryForwarder.forward(0, LoggingLevel.INFO, "Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("LogEntryForwarderTest.java", logEntry.getFilename());
		assertEquals("Hello!", logEntry.getMessage());

		LogEntryForwarder.forward(0, LoggingLevel.INFO, "Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("LogEntryForwarderTest.java", logEntry.getFilename());
		assertEquals("Hello World!", logEntry.getMessage());

		Exception exception = new Exception();
		LogEntryForwarder.forward(0, LoggingLevel.ERROR, exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
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
		StoreWriter writer = new StoreWriter(LogEntryValue.LOGGING_LEVEL, LogEntryValue.CLASS, LogEntryValue.MESSAGE);
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.TRACE).activate();

		LogEntryForwarder.forward(stackTraceElement, LoggingLevel.INFO, "Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("MyClass", logEntry.getClassName());
		assertEquals("Hello!", logEntry.getMessage());

		LogEntryForwarder.forward(stackTraceElement, LoggingLevel.INFO, "Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.INFO, logEntry.getLoggingLevel());
		assertEquals("MyClass", logEntry.getClassName());
		assertEquals("Hello World!", logEntry.getMessage());

		Exception exception = new Exception();
		LogEntryForwarder.forward(stackTraceElement, LoggingLevel.ERROR, exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(LoggingLevel.ERROR, logEntry.getLoggingLevel());
		assertEquals("MyClass", logEntry.getClassName());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());
	}

}
