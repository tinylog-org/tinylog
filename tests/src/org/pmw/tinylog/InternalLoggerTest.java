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

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.pmw.tinylog.util.StringListOutputStream;

/**
 * Tests the internal logger.
 * 
 * @see InternalLogger
 */
public class InternalLoggerTest extends AbstractTest {

	/**
	 * Test if the class is a valid utility class.
	 * 
	 * @see AbstractTest#testIfValidUtilityClass(Class)
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
		StringListOutputStream errorStream = getSystemErrorStream();

		InternalLogger.warn("Hello World!");
		assertTrue(errorStream.hasLines());
		String nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("WARNING"));
		assertThat(nextLine, containsString("Hello World!"));

		InternalLogger.warn("Hello {0}!", "tinylog");
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("WARNING"));
		assertThat(nextLine, containsString("Hello tinylog!"));

		InternalLogger.warn(new IndexOutOfBoundsException());
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("WARNING"));
		assertThat(nextLine, containsString(IndexOutOfBoundsException.class.getName()));

		InternalLogger.warn(new IndexOutOfBoundsException(""));
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("WARNING"));
		assertThat(nextLine, containsString(IndexOutOfBoundsException.class.getName()));

		InternalLogger.warn(new IndexOutOfBoundsException("Hello World!"));
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("WARNING"));
		assertThat(nextLine, containsString("Hello World!"));
		assertThat(nextLine, containsString(IndexOutOfBoundsException.class.getName()));

		InternalLogger.warn(new NullPointerException(), "Logging message");
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("WARNING"));
		assertThat(nextLine, containsString("Logging message"));
		assertThat(nextLine, containsString(NullPointerException.class.getName()));

		InternalLogger.warn(new NullPointerException(""), "Logging message");
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("WARNING"));
		assertThat(nextLine, containsString("Logging message"));
		assertThat(nextLine, containsString(NullPointerException.class.getName()));

		InternalLogger.warn(new NullPointerException("Exception message"), "Logging message");
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("WARNING"));
		assertThat(nextLine, containsString("Logging message"));
		assertThat(nextLine, containsString("Exception message"));
		assertThat(nextLine, containsString(NullPointerException.class.getName()));

		InternalLogger.warn(new RuntimeException(), "Hello {0}!", "tinylog");
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("WARNING"));
		assertThat(nextLine, containsString("Hello tinylog!"));
		assertThat(nextLine, containsString(RuntimeException.class.getName()));
	}

	/**
	 * Test error methods.
	 */
	@Test
	public final void testError() {
		StringListOutputStream errorStream = getSystemErrorStream();

		InternalLogger.error("Hello World!");
		assertTrue(errorStream.hasLines());
		String nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("ERROR"));
		assertThat(nextLine, containsString("Hello World!"));

		InternalLogger.error("Hello {0}!", "tinylog");
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("ERROR"));
		assertThat(nextLine, containsString("Hello tinylog!"));

		InternalLogger.error(new IndexOutOfBoundsException());
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("ERROR"));
		assertThat(nextLine, containsString(IndexOutOfBoundsException.class.getName()));

		InternalLogger.error(new IndexOutOfBoundsException(""));
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("ERROR"));
		assertThat(nextLine, containsString(IndexOutOfBoundsException.class.getName()));

		InternalLogger.error(new IndexOutOfBoundsException("Hello World!"));
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("ERROR"));
		assertThat(nextLine, containsString("Hello World!"));
		assertThat(nextLine, containsString(IndexOutOfBoundsException.class.getName()));

		InternalLogger.error(new NullPointerException(""), "Logging message");
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("ERROR"));
		assertThat(nextLine, containsString("Logging message"));
		assertThat(nextLine, containsString(NullPointerException.class.getName()));

		InternalLogger.error(new NullPointerException(), "Logging message");
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("ERROR"));
		assertThat(nextLine, containsString("Logging message"));
		assertThat(nextLine, containsString(NullPointerException.class.getName()));

		InternalLogger.error(new NullPointerException("Exception message"), "Logging message");
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("ERROR"));
		assertThat(nextLine, containsString("Logging message"));
		assertThat(nextLine, containsString("Exception message"));
		assertThat(nextLine, containsString(NullPointerException.class.getName()));

		InternalLogger.error(new RuntimeException(), "Hello {0}!", "tinylog");
		assertTrue(errorStream.hasLines());
		nextLine = errorStream.nextLine();
		assertThat(nextLine, containsString("ERROR"));
		assertThat(nextLine, containsString("Hello tinylog!"));
		assertThat(nextLine, containsString(RuntimeException.class.getName()));
	}

}
