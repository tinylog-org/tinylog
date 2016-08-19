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

package org.pmw.commons.logging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.AbstractTinylogTest;

/**
 * Tests for Apache Commons Logging log factory.
 *
 * @see TinylogLogFactory
 */
public class TinylogLogFactoryTest extends AbstractTinylogTest {

	private LogFactory factory;

	/**
	 * Create tinylog's log factory.
	 */
	@Before
	public final void init() {
		factory = new TinylogLogFactory();
	}

	/**
	 * Release the created log factory.
	 */
	@After
	public final void dispose() {
		factory.release();
	}

	/**
	 * Test if log factory implementation is registered as service.
	 */
	@Test
	public final void testRegistered() {
		assertTrue(LogFactory.getFactory() instanceof TinylogLogFactory);
	}

	/**
	 * Test getting the Log class.
	 */
	@Test
	public final void testLogClass() {
		Log logByClass = factory.getInstance(TinylogLogFactoryTest.class);
		assertTrue(logByClass instanceof TinylogLog);

		Log logByName = factory.getInstance("test");
		assertTrue(logByName instanceof TinylogLog);

		assertSame(logByClass, logByName);
	}

	/**
	 * Test manipulating attributes.
	 */
	@Test
	public final void testAttributes() {
		assertEquals(0, factory.getAttributeNames().length);

		/* Setting */

		factory.setAttribute("greeting", "Hello!");
		assertEquals("Hello!", factory.getAttribute("greeting"));
		assertArrayEquals(factory.getAttributeNames(), new String[] { "greeting" });

		factory.setAttribute("farewell", "Goodbye!");
		assertEquals("Goodbye!", factory.getAttribute("farewell"));
		assertTrue(Arrays.asList(factory.getAttributeNames()).containsAll(Arrays.asList("greeting", "farewell")));

		/* Overriding */

		factory.setAttribute("greeting", "Hi!");
		assertEquals("Hi!", factory.getAttribute("greeting"));

		/* Removing */

		factory.removeAttribute("greeting");
		assertNull(factory.getAttribute("greeting"));
		assertArrayEquals(factory.getAttributeNames(), new String[] { "farewell" });

		factory.setAttribute("farewell", null);
		assertNull(factory.getAttribute("farewell"));
		assertEquals(0, factory.getAttributeNames().length);
	}

}
