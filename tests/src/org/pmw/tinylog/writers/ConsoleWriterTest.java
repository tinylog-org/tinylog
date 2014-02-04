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

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.util.LogEntryBuilder;

/**
 * Tests for the console logging writer.
 * 
 * @see ConsoleWriter
 */
public class ConsoleWriterTest extends AbstractTest {

	/**
	 * Test required log entry values.
	 */
	@Test
	public final void testRequiredLogEntryValue() {
		Set<LogEntryValue> requiredLogEntryValues = new ConsoleWriter().getRequiredLogEntryValues();
		assertThat(requiredLogEntryValues, contains(LogEntryValue.LOGGING_LEVEL, LogEntryValue.RENDERED_LOG_ENTRY));
	}

	/**
	 * Test if error and warning messages will appear in the "error" output stream.
	 */
	@Test
	public final void testErrorStream() {
		for (LoggingLevel loggingLevel : Arrays.asList(LoggingLevel.ERROR, LoggingLevel.WARNING)) {
			ConsoleWriter writer = new ConsoleWriter();
			writer.init(null);

			writer.write(new LogEntryBuilder().level(loggingLevel).renderedLogEntry("Hello\n").create());

			assertFalse(getOutputStream().hasLines());
			assertEquals("Hello", getErrorStream().nextLine());

			writer.close();
		}
	}

	/**
	 * Test if info, debug and trace messages will appear in the "standard" output stream.
	 */
	@Test
	public final void testOutputStream() {
		for (LoggingLevel loggingLevel : Arrays.asList(LoggingLevel.INFO, LoggingLevel.DEBUG, LoggingLevel.TRACE)) {
			ConsoleWriter writer = new ConsoleWriter();
			writer.init(null);

			writer.write(new LogEntryBuilder().level(loggingLevel).renderedLogEntry("Hello\n").create());

			assertEquals("Hello", getOutputStream().nextLine());
			assertFalse(getErrorStream().hasLines());

			writer.close();
		}
	}

	/**
	 * Test flushing.
	 */
	@Test
	public final void testFlush() {
		ConsoleWriter writer = new ConsoleWriter();
		writer.init(null);

		writer.write(new LogEntryBuilder().level(LoggingLevel.INFO).renderedLogEntry("Hello").create());
		writer.flush();

		assertEquals("Hello", getOutputStream().nextLine());
		assertFalse(getErrorStream().hasLines());

		writer.close();
	}

}
