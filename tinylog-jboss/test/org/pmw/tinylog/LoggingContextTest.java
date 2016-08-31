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

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.MDC;
import org.junit.After;
import org.junit.Test;
import org.pmw.tinylog.jboss.logging.LogEntry;
import org.pmw.tinylog.jboss.logging.StorageLogger;

import mockit.Mock;
import mockit.MockUp;

/**
 * Tests for logging context.
 *
 * @see LoggingContext
 */
public class LoggingContextTest extends AbstractJBossTest {

	/**
	 * Clear {@link MDC} after each test.
	 */
	@After
	public final void clearMDC() {
		MDC.clear();
	}

	/**
	 * Test getting all stored mappings.
	 */
	@Test
	public final void testGettingAllMappings() {
		MDC.put("message", "Hello World");
		MDC.put("pi", 3.14);

		assertEquals(doubletonMap("message", "Hello World", "pi", "3.14"), LoggingContext.getMapping());

		MDC.put("message", null);
		assertEquals(doubletonMap("message", null, "pi", "3.14"), LoggingContext.getMapping());
	}

	/**
	 * Test getting defined mappings by key.
	 */
	@Test
	public final void testGettingSingleMapping() {
		MDC.put("message", "Hello World");
		MDC.put("pi", 3.14);

		assertEquals("Hello World", LoggingContext.get("message"));
		assertEquals("3.14", LoggingContext.get("pi"));
		assertNull(LoggingContext.get("invalid"));
	}

	/**
	 * Test storing new mappings.
	 */
	@Test
	public final void testPuttingValues() {
		LoggingContext.put("message", "Hello World");
		assertEquals(singletonMap("message", "Hello World"), MDC.getMap());

		LoggingContext.put("pi", 3.14);
		assertEquals(doubletonMap("message", "Hello World", "pi", 3.14), MDC.getMap());

		LoggingContext.put("message", "Good Bye");
		assertEquals(doubletonMap("message", "Good Bye", "pi", 3.14), MDC.getMap());
	}

	/**
	 * Test removing defined mappings by key.
	 */
	@Test
	public final void testRemovingValues() {
		MDC.put("message", "Hello World");
		MDC.put("pi", 3.14);

		LoggingContext.remove("invalid");
		assertEquals(doubletonMap("message", "Hello World", "pi", 3.14), MDC.getMap());

		LoggingContext.remove("message");
		assertEquals(singletonMap("pi", 3.14), MDC.getMap());

		LoggingContext.remove("pi");
		assertEquals(emptyMap(), MDC.getMap());
	}

	/**
	 * Test removing all mappings.
	 */
	@Test
	public final void testClearingMappings() {
		MDC.put("message", "Hello World");
		MDC.put("pi", 3.14);

		LoggingContext.clear();
		assertEquals(emptyMap(), MDC.getMap());
	}

	/**
	 * Test warning if there is no clear() method as this method is not available for JBoss Logging prior 3.3.0.
	 */
	@Test
	public final void testClearingUnsupported() {
		new MockUp<MDC>() {
			@Mock
			public void clear() {
				throw new NoSuchMethodError();
			}
		};

		LoggingContext.clear();
		
		StorageLogger logger = (StorageLogger) Logger.getLogger(LoggingContext.class);
		String expectedMessage = "Clearing thread-based mapped diagnostic context is not supported by underlying logging framework";
		assertThat(logger.consumeLogEntries(), contains(new LogEntry(Level.WARN, expectedMessage)));
	}

	private static <T> Map<String, T> doubletonMap(final String key1, final T value1, final String key2, final T value2) {
		Map<String, T> map = new HashMap<String, T>();
		map.put(key1, value1);
		map.put(key2, value2);
		return map;
	}

}
