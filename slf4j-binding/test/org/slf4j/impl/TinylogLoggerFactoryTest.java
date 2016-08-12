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

package org.slf4j.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * Tests for SLF4J logger factory.
 *
 * @see TinylogLoggerFactory
 */
public class TinylogLoggerFactoryTest {

	/**
	 * Test getting loggers.
	 */
	@Test
	public final void testGettingLoggers() {
		TinylogLoggerFactory factory = new TinylogLoggerFactory();

		TinylogLogger first = factory.getLogger("abc");
		assertEquals("abc", first.getName());

		TinylogLogger second = factory.getLogger("abc");
		assertEquals("abc", second.getName());
		assertSame(first, second);

		TinylogLogger third = factory.getLogger("ABC");
		assertEquals("ABC", third.getName());
		assertNotSame(first, third);
	}

}
