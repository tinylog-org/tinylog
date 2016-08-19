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

import org.junit.Test;

/**
 * Test message formatter.
 *
 * @see MessageFormatter
 */
public class MessageFormatterTest extends AbstractCoreTest {

	/**
	 * Test if the class is a valid utility class.
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(MessageFormatter.class);
	}

	/**
	 * Test format patterns without given arguments.
	 */
	@Test
	public final void testWithoutArguments() {
		assertEquals("Hello World!", MessageFormatter.format("Hello World!"));
		assertEquals("Hello {}!", MessageFormatter.format("Hello {}!"));
	}

	/**
	 * Test format patterns with given arguments.
	 */
	@Test
	public final void testWithArguments() {
		assertEquals("Hello World!", MessageFormatter.format("Hello {}!", "World"));
		assertEquals("Hello World!", MessageFormatter.format("{} {}!", "Hello", "World"));
		assertEquals("Hello {}!", MessageFormatter.format("{} {}!", "Hello"));
		assertEquals("Hello!", MessageFormatter.format("{}!", "Hello", "World"));
	}

}
