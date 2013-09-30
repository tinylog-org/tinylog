/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog.writers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.util.StringListOutputStream;

/**
 * Tests for the console logging writer.
 * 
 * @see ConsoleWriter
 */
public class ConsoleWriterTest extends AbstractTest {

	/**
	 * Test if error and warning messages will appear in the "error" output stream.
	 */
	@Test
	public final void testErrorStream() {
		for (LoggingLevel loggingLevel : Arrays.asList(LoggingLevel.ERROR, LoggingLevel.WARNING)) {
			ConsoleWriter writer = new ConsoleWriter();
			writer.init();
			writer.write(loggingLevel, "Hello\n");

			StringListOutputStream outputStream = getSystemOutputStream();
			assertFalse(outputStream.hasLines());

			StringListOutputStream errorStream = getSystemErrorStream();
			assertTrue(errorStream.hasLines());
			assertEquals("Hello", errorStream.nextLine());
		}
	}

	/**
	 * Test if info, debug and trace messages will appear in the "standard" output stream.
	 */
	@Test
	public final void testOutputStream() {
		for (LoggingLevel loggingLevel : Arrays.asList(LoggingLevel.INFO, LoggingLevel.DEBUG, LoggingLevel.TRACE)) {
			ConsoleWriter writer = new ConsoleWriter();
			writer.init();
			writer.write(loggingLevel, "Hello\n");

			StringListOutputStream outputStream = getSystemOutputStream();
			assertTrue(outputStream.hasLines());
			assertEquals("Hello", outputStream.nextLine());

			StringListOutputStream errorStream = getSystemErrorStream();
			assertFalse(errorStream.hasLines());
		}
	}

}
