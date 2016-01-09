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

package org.tinylog;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for logging context.
 *
 * @see LoggingContext
 */
public class LoggingContextTest extends AbstractTest {

	/**
	 * Test if the class is a valid utility class.
	 *
	 * @see AbstractTest#testIfValidUtilityClass(Class)
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(LoggingContext.class);
	}

	/**
	 * Test all getters and data manipulation methods in single threading mode.
	 */
	@Test
	public final void testSingleThreading() {
		assertEquals(emptyMap(), LoggingContext.getMapping());
		assertNull(LoggingContext.get("number"));
		assertNull(LoggingContext.get("pi"));

		LoggingContext.put("number", 42);
		assertEquals(singletonMap("number", "42"), LoggingContext.getMapping());
		assertEquals("42", LoggingContext.get("number"));
		assertNull(LoggingContext.get("pi"));

		LoggingContext.put("pi", Math.PI);
		assertEquals(doubletonMap("number", "42", "pi", Double.toString(Math.PI)), LoggingContext.getMapping());
		assertEquals("42", LoggingContext.get("number"));
		assertEquals(Double.toString(Math.PI), LoggingContext.get("pi"));

		LoggingContext.remove("number");
		assertEquals(singletonMap("pi", Double.toString(Math.PI)), LoggingContext.getMapping());
		assertNull(LoggingContext.get("number"));
		assertEquals(Double.toString(Math.PI), LoggingContext.get("pi"));

		LoggingContext.clear();
		assertEquals(emptyMap(), LoggingContext.getMapping());
		assertNull(LoggingContext.get("number"));
		assertNull(LoggingContext.get("pi"));
	}

	/**
	 * Test if changes in one thread will not influence other already existing threads.
	 *
	 * @throws InterruptedException
	 *             Failed waiting for thread
	 */
	@Test
	public final void testLocalChanges() throws InterruptedException {
		assertEquals(emptyMap(), LoggingContext.getMapping());
		assertNull(LoggingContext.get("number"));

		Thread thread = new Thread() {

			@Override
			public void run() {
				assertEquals(emptyMap(), LoggingContext.getMapping());
				assertNull(LoggingContext.get("number"));

				LoggingContext.put("number", "42");
				assertEquals(singletonMap("number", "42"), LoggingContext.getMapping());
				assertEquals("42", LoggingContext.get("number"));
			}

		};
		thread.start();
		thread.join();

		assertEquals(emptyMap(), LoggingContext.getMapping());
		assertNull(LoggingContext.get("number"));
	}

	/**
	 * Test inhering initial values from parent thread.
	 *
	 * @throws InterruptedException
	 *             Failed waiting for thread
	 */
	@Test
	public final void testInhertInitValues() throws InterruptedException {
		assertEquals(emptyMap(), LoggingContext.getMapping());
		assertNull(LoggingContext.get("number"));

		LoggingContext.put("number", "42");
		assertEquals(singletonMap("number", "42"), LoggingContext.getMapping());
		assertEquals("42", LoggingContext.get("number"));

		Thread thread = new Thread() {

			@Override
			public void run() {
				assertEquals(singletonMap("number", "42"), LoggingContext.getMapping());
				assertEquals("42", LoggingContext.get("number"));
			}

		};
		thread.start();
		thread.join();
	}

	/**
	 * Test if external changes of map will not apply to logging context.
	 */
	@Test
	public final void testIgnoreIllegalModifying() {
		try {
			LoggingContext.getMapping().put("test", "illegal");
		} catch (RuntimeException ex) {
			// ok
		}

		assertNull(LoggingContext.get("test"));
	}

	private static Map<String, String> doubletonMap(final String key1, final String value1, final String key2, final String value2) {
		Map<String, String> map = new HashMap<>();
		map.put(key1, value1);
		map.put(key2, value2);
		return map;
	}

}
