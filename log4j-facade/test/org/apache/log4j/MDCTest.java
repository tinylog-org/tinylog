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

package org.apache.log4j;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.pmw.tinylog.AbstractTinylogTest;
import org.pmw.tinylog.LoggingContext;

/**
 * Tests for thread-based mapped diagnostic context.
 *
 * @see MDC
 */
public class MDCTest extends AbstractTinylogTest {

	/**
	 * Test if the class is a valid utility class.
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(MDC.class);
	}

	/**
	 * Test creating a copy of the current context map.
	 */
	@Test
	public final void testCopying() {
		LoggingContext.put("a", 1);
		LoggingContext.put("b", 2);

		Map<?, ?> copy = MDC.getContext();
		assertEquals(doubletonMap("a", "1", "b", "2"), copy);
		assertNotSame(LoggingContext.getMapping(), copy);
	}

	/**
	 * Test getting a context value.
	 */
	@Test
	public final void testGetting() {
		LoggingContext.put("a", 1);
		LoggingContext.put("b", 2);
		assertEquals(doubletonMap("a", "1", "b", "2"), LoggingContext.getMapping());

		assertEquals("1", MDC.get("a"));
		assertEquals("2", MDC.get("b"));
	}

	/**
	 * Test putting a context value.
	 */
	@Test
	public final void testPutting() {
		LoggingContext.put("a", 1);
		assertEquals(singletonMap("a", "1"), LoggingContext.getMapping());

		MDC.put("b", "2");
		assertEquals(doubletonMap("a", "1", "b", "2"), LoggingContext.getMapping());

		MDC.put("a", "0");
		assertEquals(doubletonMap("a", "0", "b", "2"), LoggingContext.getMapping());
	}

	/**
	 * Test removing a context value.
	 */
	@Test
	public final void testRemoving() {
		LoggingContext.put("a", 1);
		LoggingContext.put("b", 2);
		assertEquals(doubletonMap("a", "1", "b", "2"), LoggingContext.getMapping());

		MDC.remove("a");
		assertEquals(singletonMap("b", "2"), LoggingContext.getMapping());
	}

	/**
	 * Test removing all existing context values.
	 */
	@Test
	public final void testClearing() {
		LoggingContext.put("a", 1);
		LoggingContext.put("b", 2);
		assertEquals(doubletonMap("a", "1", "b", "2"), LoggingContext.getMapping());

		MDC.clear();
		assertEquals(emptyMap(), LoggingContext.getMapping());
	}

	private static Map<String, String> doubletonMap(final String key1, final String value1, final String key2, final String value2) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(key1, value1);
		map.put(key2, value2);
		return map;
	}

}
