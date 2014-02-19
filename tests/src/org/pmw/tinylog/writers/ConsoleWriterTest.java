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
import static org.pmw.tinylog.hamcrest.CollectionMatchers.types;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.util.LogEntryBuilder;
import org.pmw.tinylog.util.PropertiesBuilder;

/**
 * Tests for the console writer.
 * 
 * @see ConsoleWriter
 */
public class ConsoleWriterTest extends AbstractWriterTest {

	/**
	 * Test required log entry values.
	 */
	@Test
	public final void testRequiredLogEntryValue() {
		Set<LogEntryValue> requiredLogEntryValues = new ConsoleWriter().getRequiredLogEntryValues();
		assertThat(requiredLogEntryValues, contains(LogEntryValue.LEVEL, LogEntryValue.RENDERED_LOG_ENTRY));
	}

	/**
	 * Test if error and warning messages will appear in the "error" output stream.
	 */
	@Test
	public final void testErrorStream() {
		for (Level loggingLevel : Arrays.asList(Level.ERROR, Level.WARNING)) {
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
		for (Level loggingLevel : Arrays.asList(Level.INFO, Level.DEBUG, Level.TRACE)) {
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

		writer.write(new LogEntryBuilder().level(Level.INFO).renderedLogEntry("Hello").create());
		writer.flush();

		assertEquals("Hello", getOutputStream().nextLine());
		assertFalse(getErrorStream().hasLines());

		writer.close();
	}

	/**
	 * Test reading console writer from properties.
	 */
	@Test
	public final void testFromProperties() {
		List<Writer> writers = createFromProperties(new PropertiesBuilder().set("tinylog.writer", "console").create());
		assertThat(writers, types(ConsoleWriter.class));
	}

}
