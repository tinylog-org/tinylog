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

package org.pmw.tinylog.writers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.LoggingLevel;

/**
 * Tests the log entry data class.
 * 
 * @see LogEntry
 */
public class LogEntryTest extends AbstractTest {

	/**
	 * Test all getters.
	 */
	@Test
	public final void testGetters() {
		Date date = new Date();
		String processId = "1234";
		Thread thread = new Thread();
		String className = "org.package.MyClass";
		String method = "myMethod";
		String file = "myFile";
		int lineNumber = 42;
		LoggingLevel level = LoggingLevel.DEBUG;
		String message = "Hello World!";
		RuntimeException exception = new RuntimeException("Hello from Exception!");
		String renderedLogEntry = "My log entry";

		LogEntry logEntry = new LogEntry(date, processId, thread, className, method, file, lineNumber, level, message, exception, renderedLogEntry);

		assertSame(date, logEntry.getDate());
		assertSame(processId, logEntry.getProcessId());
		assertSame(thread, logEntry.getThread());
		assertEquals(className, logEntry.getClassName());
		assertEquals(method, logEntry.getMethodName());
		assertEquals(file, logEntry.getFilename());
		assertEquals(lineNumber, logEntry.getLineNumber());
		assertEquals(level, logEntry.getLoggingLevel());
		assertEquals(message, logEntry.getMessage());
		assertSame(exception, logEntry.getException());
		assertEquals(renderedLogEntry, logEntry.getRenderedLogEntry());
	}

}
