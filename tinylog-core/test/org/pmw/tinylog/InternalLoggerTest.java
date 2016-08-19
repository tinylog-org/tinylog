/*
 * Copyright 2013 Martin Winandy
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
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Tests the internal logger.
 *
 * @see InternalLogger
 */
public class InternalLoggerTest extends AbstractCoreTest {

	/**
	 * Test if the class is a valid utility class.
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(InternalLogger.class);
	}

	/**
	 * Test warnings methods.
	 */
	@Test
	public final void testWarnings() {
		InternalLogger.warn("Hello World!");
		assertEquals("LOGGER WARNING: Hello World!", getErrorStream().nextLine());

		InternalLogger.warn("Hello {}!", "tinylog");
		assertEquals("LOGGER WARNING: Hello tinylog!", getErrorStream().nextLine());

		InternalLogger.warn(new IndexOutOfBoundsException());
		assertEquals("LOGGER WARNING: " + IndexOutOfBoundsException.class.getName(), getErrorStream().nextLine());

		InternalLogger.warn(new IndexOutOfBoundsException("Hello World!"));
		assertEquals("LOGGER WARNING: Hello World! (" + IndexOutOfBoundsException.class.getName() + ")", getErrorStream().nextLine());

		InternalLogger.warn(new NullPointerException(), "Logging message");
		assertEquals("LOGGER WARNING: Logging message (" + NullPointerException.class.getName() + ")", getErrorStream().nextLine());

		InternalLogger.warn(new NullPointerException("Exception message"), "Logging message");
		assertEquals("LOGGER WARNING: Logging message (" + NullPointerException.class.getName() + ": Exception message)", getErrorStream().nextLine());

		InternalLogger.warn(new RuntimeException(), "Hello {}!", "tinylog");
		assertEquals("LOGGER WARNING: Hello tinylog! (" + RuntimeException.class.getName() + ")", getErrorStream().nextLine());

	}

	/**
	 * Test repeating warnings.
	 */
	@Test
	public final void testRepeatingWarnings() {
		InternalLogger.warn("Hello World!");
		assertEquals("LOGGER WARNING: Hello World!", getErrorStream().nextLine());

		InternalLogger.warn("Hello World!");
		assertFalse(getErrorStream().hasLines()); // Repeating warning should be ignored

		InternalLogger.warn("Hello tinylog!");
		assertEquals("LOGGER WARNING: Hello tinylog!", getErrorStream().nextLine());

		InternalLogger.warn("Hello World!");
		assertEquals("LOGGER WARNING: Hello World!", getErrorStream().nextLine());
	}

	/**
	 * Test error methods.
	 */
	@Test
	public final void testError() {
		InternalLogger.error("Hello World!");
		assertEquals("LOGGER ERROR: Hello World!", getErrorStream().nextLine());

		InternalLogger.error("Hello {}!", "tinylog");
		assertEquals("LOGGER ERROR: Hello tinylog!", getErrorStream().nextLine());

		InternalLogger.error(new IndexOutOfBoundsException());
		assertEquals("LOGGER ERROR: " + IndexOutOfBoundsException.class.getName(), getErrorStream().nextLine());

		InternalLogger.error(new IndexOutOfBoundsException("Hello World!"));
		assertEquals("LOGGER ERROR: Hello World! (" + IndexOutOfBoundsException.class.getName() + ")", getErrorStream().nextLine());

		InternalLogger.error(new NullPointerException(), "Logging message");
		assertEquals("LOGGER ERROR: Logging message (" + NullPointerException.class.getName() + ")", getErrorStream().nextLine());

		InternalLogger.error(new NullPointerException("Exception message"), "Logging message");
		assertEquals("LOGGER ERROR: Logging message (" + NullPointerException.class.getName() + ": Exception message)", getErrorStream().nextLine());

		InternalLogger.error(new RuntimeException(), "Hello {}!", "tinylog");
		assertEquals("LOGGER ERROR: Hello tinylog! (" + RuntimeException.class.getName() + ")", getErrorStream().nextLine());
	}

	/**
	 * Test repeating errors.
	 */
	@Test
	public final void testRepeatingErrors() {
		InternalLogger.error("Hello World!");
		assertEquals("LOGGER ERROR: Hello World!", getErrorStream().nextLine());

		InternalLogger.error("Hello World!");
		assertFalse(getErrorStream().hasLines()); // Repeating error should be ignored

		InternalLogger.error("Hello tinylog!");
		assertEquals("LOGGER ERROR: Hello tinylog!", getErrorStream().nextLine());

		InternalLogger.error("Hello World!");
		assertEquals("LOGGER ERROR: Hello World!", getErrorStream().nextLine());
	}

}
