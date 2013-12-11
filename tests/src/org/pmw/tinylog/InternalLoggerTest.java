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

import static org.hamcrest.Matchers.allOf;
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
	 * Test error methods.
	 */
	@Test
	public final void testError() {
		StringListOutputStream errorStream = getSystemErrorStream();

		InternalLogger.error("Hello World!");
		assertTrue(errorStream.hasLines());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("Hello World!")));

		InternalLogger.error("Hello {0}!", "tinylog");
		assertTrue(errorStream.hasLines());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("Hello tinylog!")));

		InternalLogger.error(new IndexOutOfBoundsException());
		assertTrue(errorStream.hasLines());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString(IndexOutOfBoundsException.class.getName())));

		InternalLogger.error(new IndexOutOfBoundsException("Hello World!"));
		assertTrue(errorStream.hasLines());
		assertThat(errorStream.nextLine(),
				allOf(containsString("ERROR"), containsString("Hello World!"), containsString(IndexOutOfBoundsException.class.getName())));

		InternalLogger.error(new NullPointerException(), "Logging message");
		assertTrue(errorStream.hasLines());
		assertThat(errorStream.nextLine(),
				allOf(containsString("ERROR"), containsString("Logging message"), containsString(NullPointerException.class.getName())));

		InternalLogger.error(new NullPointerException("Exception message"), "Logging message");
		assertTrue(errorStream.hasLines());
		assertThat(
				errorStream.nextLine(),
				allOf(containsString("ERROR"), containsString("Logging message"), containsString("Exception message"),
						containsString(NullPointerException.class.getName())));

		InternalLogger.error(new RuntimeException(), "Hello {0}!", "tinylog");
		assertTrue(errorStream.hasLines());
		assertThat(errorStream.nextLine(), allOf(containsString("ERROR"), containsString("Hello tinylog!"), containsString(RuntimeException.class.getName())));
	}

}
