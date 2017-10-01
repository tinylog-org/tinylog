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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.types;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
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
		assertThat(requiredLogEntryValues, containsInAnyOrder(LogEntryValue.LEVEL, LogEntryValue.RENDERED_LOG_ENTRY));
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

		writer.write(new LogEntryBuilder().level(Level.INFO).renderedLogEntry("Hello\n").create());
		writer.flush();

		assertEquals("Hello", getOutputStream().nextLine());
		assertFalse(getErrorStream().hasLines());

		writer.close();
	}

	/**
	 * Test using custom output stream.
	 *
	 * @throws UnsupportedEncodingException
	 *             UTF-8 is not supported
	 */
	@Test
	public void testCustomOutputStream() throws UnsupportedEncodingException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ConsoleWriter writer = new ConsoleWriter(new PrintStream(stream, true, "UTF-8"));

		writer.write(new LogEntryBuilder().level(Level.INFO).renderedLogEntry("Hello\n").create());
		writer.flush();

		assertEquals("Hello\n", new String(stream.toByteArray(), "UTF-8"));
		assertFalse(getOutputStream().hasLines());
		assertFalse(getErrorStream().hasLines());

		writer.close();
	}

	/**
	 * Test string parameter for {@link System#out}.
	 */
	@Test
	public final void testStringParameterForSystemOut() {
		ConsoleWriter writer = new ConsoleWriter("out");

		writer.write(new LogEntryBuilder().level(Level.TRACE).renderedLogEntry("Hello\n").create());
		writer.flush();

		assertEquals("Hello", getOutputStream().nextLine());
		assertFalse(getErrorStream().hasLines());

		writer.write(new LogEntryBuilder().level(Level.ERROR).renderedLogEntry("Bye\n").create());
		writer.flush();

		assertEquals("Bye", getOutputStream().nextLine());
		assertFalse(getErrorStream().hasLines());

		writer.close();
	}

	/**
	 * Test string parameter for {@link System#err}.
	 */
	@Test
	public final void testStringParameterForSystemErr() {
		ConsoleWriter writer = new ConsoleWriter("err");

		writer.write(new LogEntryBuilder().level(Level.TRACE).renderedLogEntry("Hello\n").create());
		writer.flush();

		assertEquals("Hello", getErrorStream().nextLine());
		assertFalse(getOutputStream().hasLines());

		writer.write(new LogEntryBuilder().level(Level.ERROR).renderedLogEntry("Bye\n").create());
		writer.flush();

		assertEquals("Bye", getErrorStream().nextLine());
		assertFalse(getOutputStream().hasLines());

		writer.close();
	}

	/**
	 * Test <code>null</code> as string parameter.
	 */
	@Test
	public final void testNullStringParameter() {
		ConsoleWriter writer = new ConsoleWriter((String) null);

		writer.write(new LogEntryBuilder().level(Level.INFO).renderedLogEntry("Hello\n").create());
		writer.flush();

		assertEquals("Hello", getOutputStream().nextLine());
		assertFalse(getErrorStream().hasLines());

		writer.write(new LogEntryBuilder().level(Level.WARNING).renderedLogEntry("Bye\n").create());
		writer.flush();

		assertEquals("Bye", getErrorStream().nextLine());
		assertFalse(getOutputStream().hasLines());

		writer.close();
	}

	/**
	 * Test exception for "abc".
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testStringParameterForInvalidString() {
		new ConsoleWriter("abc");
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
