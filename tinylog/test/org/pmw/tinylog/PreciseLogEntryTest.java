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

package org.pmw.tinylog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

/**
 * Tests the precise log entry data class.
 *
 * @see PreciseLogEntry
 */
public class PreciseLogEntryTest extends AbstractCoreTest {

	/**
	 * Test all getters.
	 */
	@Test
	public final void testGetters() {
		Instant instant = Instant.now();
		String processId = "1234";
		Thread thread = new Thread();
		Map<String, String> context = Collections.emptyMap();
		String className = "org.package.MyClass";
		String method = "myMethod";
		String file = "myFile";
		int lineNumber = 42;
		Level level = Level.DEBUG;
		String message = "Hello World!";
		RuntimeException exception = new RuntimeException("Hello from Exception!");
		String renderedLogEntry = "My log entry";

		PreciseLogEntry logEntry = new PreciseLogEntry(instant, processId, thread, context, className, method, file, lineNumber, level, message, exception);

		assertEquals(Date.from(instant), logEntry.getDate());
		assertEquals(Timestamp.from(instant), logEntry.getTimestamp());
		assertSame(instant, logEntry.getInstant());
		assertSame(processId, logEntry.getProcessId());
		assertSame(thread, logEntry.getThread());
		assertSame(context, logEntry.getContext());
		assertEquals(className, logEntry.getClassName());
		assertEquals(method, logEntry.getMethodName());
		assertEquals(file, logEntry.getFilename());
		assertEquals(lineNumber, logEntry.getLineNumber());
		assertEquals(level, logEntry.getLevel());
		assertEquals(message, logEntry.getMessage());
		assertSame(exception, logEntry.getException());
		assertNull(logEntry.getRenderedLogEntry());

		logEntry.setRenderedLogEntry(renderedLogEntry);

		assertEquals(renderedLogEntry, logEntry.getRenderedLogEntry());
	}

}
