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

package org.slf4j.impl;

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
 * Test SLF4J compatible MDC adapter.
 *
 * @see TinylogMDCAdapter
 */
public class TinylogMDCAdapterTest extends AbstractTinylogTest {

	/**
	 * Test creating a copy of the current context map.
	 */
	@Test
	public final void testCopying() {
		LoggingContext.put("a", 1);
		LoggingContext.put("b", 2);

		Map<String, String> copy = new TinylogMDCAdapter().getCopyOfContextMap();
		assertEquals(doubletonMap("a", "1", "b", "2"), copy);
		assertNotSame(LoggingContext.getMapping(), copy);
	}

	/**
	 * Test replacing the current context by a new map.
	 */
	@Test
	public final void testReplacing() {
		LoggingContext.put("a", 1);

		Map<String, String> map = new HashMap<>();
		map.put("b", "2");
		map.put("c", "3");

		new TinylogMDCAdapter().setContextMap(map);
		assertEquals(doubletonMap("b", "2", "c", "3"), LoggingContext.getMapping());
	}

	/**
	 * Test getting a context value.
	 */
	@Test
	public final void testGetting() {
		LoggingContext.put("a", 1);
		LoggingContext.put("b", 2);
		assertEquals(doubletonMap("a", "1", "b", "2"), LoggingContext.getMapping());

		TinylogMDCAdapter adapter = new TinylogMDCAdapter();
		assertEquals("1", adapter.get("a"));
		assertEquals("2", adapter.get("b"));
	}

	/**
	 * Test putting a context value.
	 */
	@Test
	public final void testPutting() {
		TinylogMDCAdapter adapter = new TinylogMDCAdapter();

		LoggingContext.put("a", 1);
		assertEquals(singletonMap("a", "1"), LoggingContext.getMapping());

		adapter.put("b", "2");
		assertEquals(doubletonMap("a", "1", "b", "2"), LoggingContext.getMapping());

		adapter.put("a", "0");
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

		new TinylogMDCAdapter().remove("a");
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

		new TinylogMDCAdapter().clear();
		assertEquals(emptyMap(), LoggingContext.getMapping());
	}

	private static Map<String, String> doubletonMap(final String key1, final String value1, final String key2, final String value2) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(key1, value1);
		map.put(key2, value2);
		return map;
	}

}
