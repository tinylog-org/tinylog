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

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.logging.Level;

import org.junit.Test;
import org.pmw.tinylog.util.logging.LogEntry;

/**
 * Tests for logging context.
 *
 * @see LoggingContext
 */
public class LoggingContextTest extends AbstractJulTest {

	/**
	 * Test getting mappings.
	 */
	@Test
	public final void testGetting() {
		assertEquals(Collections.emptyMap(), LoggingContext.getMapping());
		assertNull(LoggingContext.get("anything"));
	}

	/**
	 * Test putting a new mapping.
	 */
	@Test
	public final void testPutting() {
		LoggingContext.put("anything", 42);
		assertEquals(Collections.emptyMap(), LoggingContext.getMapping());

		String expectedMessage = "Thread-based mapped diagnostic context is not supported by underlying logging framework";
		assertThat(consumeLogEntries(), contains(new LogEntry(Level.WARNING, LoggingContext.class.getName(), "put", expectedMessage)));
	}

	/**
	 * Test that remove() method does not throw any exception.
	 */
	@Test
	public final void testRemoving() {
		LoggingContext.remove("anything");
	}

	/**
	 * Test that clear() method does not throw any exception.
	 */
	@Test
	public final void testClearing() {
		LoggingContext.clear();
	}

}
