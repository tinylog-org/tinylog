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

package org.pmw.tinylog;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.sameContent;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.types;

import java.lang.Thread.State;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.LogEntry;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.Writer;

/**
 * Tests for configuration.
 * 
 * @see Configuration
 */
public class ConfigurationTest extends AbstractTest {

	/**
	 * Test a minimal configuration sample.
	 */
	@Test
	public final void testMinimalConfigurationSample() {
		Configuration configuration = createMinimalConfigurationSample();
		testMinimalConfigurationSample(configuration);
	}

	/**
	 * Test a detailed configuration sample.
	 */
	@Test
	public final void testDetailedConfigurationSample() {
		Configuration configuration = createDetailedConfigurationSample();
		testDetailedConfigurationSample(configuration);
	}

	/**
	 * Test getters for custom severity levels.
	 */
	@Test
	public final void testCustomLevels() {
		Map<String, Level> customLevels = noCustomLevels();
		Configuration configuration = new Configuration(Level.DEBUG, customLevels, "", Locale.ROOT, emptyWriterDefinition(), null, 0);
		assertEquals(Level.DEBUG, configuration.getLevel());
		assertFalse(configuration.hasCustomLevels());
		assertEquals(Level.DEBUG, configuration.getLevel("a"));
		assertEquals(Level.DEBUG, configuration.getLevel("a.b"));
		assertEquals(Level.DEBUG, configuration.getLevel("a.b.MyClass"));
		assertEquals(Level.DEBUG, configuration.getLevel("a.b.MyClass2"));
		assertEquals(Level.DEBUG, configuration.getLevel("a.c"));
		assertEquals(Level.DEBUG, configuration.getLevel("b"));

		customLevels = singleCustomLevel("a.b", Level.TRACE);
		configuration = new Configuration(Level.DEBUG, customLevels, "", Locale.ROOT, emptyWriterDefinition(), null, 0);
		assertEquals(Level.DEBUG, configuration.getLevel());
		assertTrue(configuration.hasCustomLevels());
		assertEquals(Level.DEBUG, configuration.getLevel("a"));
		assertEquals(Level.TRACE, configuration.getLevel("a.b"));
		assertEquals(Level.TRACE, configuration.getLevel("a.b.MyClass"));
		assertEquals(Level.TRACE, configuration.getLevel("a.b.MyClass2"));
		assertEquals(Level.DEBUG, configuration.getLevel("a.c"));
		assertEquals(Level.DEBUG, configuration.getLevel("b"));

		customLevels = pairCustomLevels("a", Level.INFO, "a.b.MyClass", Level.TRACE);
		configuration = new Configuration(Level.DEBUG, customLevels, "", Locale.ROOT, emptyWriterDefinition(), null, 0);
		assertEquals(Level.DEBUG, configuration.getLevel());
		assertTrue(configuration.hasCustomLevels());
		assertEquals(Level.INFO, configuration.getLevel("a"));
		assertEquals(Level.INFO, configuration.getLevel("a.b"));
		assertEquals(Level.TRACE, configuration.getLevel("a.b.MyClass"));
		assertEquals(Level.INFO, configuration.getLevel("a.b.MyClass2"));
		assertEquals(Level.INFO, configuration.getLevel("a.c"));
		assertEquals(Level.DEBUG, configuration.getLevel("b"));
	}

	/**
	 * Test check if output of log entries is possible.
	 */
	@Test
	public final void testOutputPossible() {
		/* Without writers */

		List<WriterDefinition> writerDefinition = emptyWriterDefinition();

		Configuration configuration = new Configuration(Level.OFF, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertFalse(configuration.isOutputPossible(Level.INFO));
		assertFalse(configuration.isOutputPossible(Level.WARNING));
		assertFalse(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertFalse(configuration.isOutputPossible(Level.INFO));
		assertFalse(configuration.isOutputPossible(Level.WARNING));
		assertFalse(configuration.isOutputPossible(Level.ERROR));

		/* With writers without custom severity level */

		writerDefinition = singleWriterDefinition(new DummyWriter());

		configuration = new Configuration(Level.OFF, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertFalse(configuration.isOutputPossible(Level.INFO));
		assertFalse(configuration.isOutputPossible(Level.WARNING));
		assertFalse(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.INFO, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertTrue(configuration.isOutputPossible(Level.INFO));
		assertTrue(configuration.isOutputPossible(Level.WARNING));
		assertTrue(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.INFO, singleCustomLevel("a", Level.ERROR), "", Locale.ROOT, writerDefinition, null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertTrue(configuration.isOutputPossible(Level.INFO));
		assertTrue(configuration.isOutputPossible(Level.WARNING));
		assertTrue(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.INFO, singleCustomLevel("a", Level.DEBUG), "", Locale.ROOT, writerDefinition, null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertTrue(configuration.isOutputPossible(Level.DEBUG));
		assertTrue(configuration.isOutputPossible(Level.INFO));
		assertTrue(configuration.isOutputPossible(Level.WARNING));
		assertTrue(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.INFO, pairCustomLevels("a", Level.TRACE, "b", Level.DEBUG), "", Locale.ROOT, writerDefinition, null, 0);
		assertTrue(configuration.isOutputPossible(Level.TRACE));
		assertTrue(configuration.isOutputPossible(Level.DEBUG));
		assertTrue(configuration.isOutputPossible(Level.INFO));
		assertTrue(configuration.isOutputPossible(Level.WARNING));
		assertTrue(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.INFO, pairCustomLevels("a", Level.OFF, "b", Level.TRACE), "", Locale.ROOT, writerDefinition, null, 0);
		assertTrue(configuration.isOutputPossible(Level.TRACE));
		assertTrue(configuration.isOutputPossible(Level.DEBUG));
		assertTrue(configuration.isOutputPossible(Level.INFO));
		assertTrue(configuration.isOutputPossible(Level.WARNING));
		assertTrue(configuration.isOutputPossible(Level.ERROR));

		/* With writers with custom severity level */

		writerDefinition = singleWriterDefinition(new DummyWriter(), Level.WARNING);

		configuration = new Configuration(Level.OFF, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertFalse(configuration.isOutputPossible(Level.INFO));
		assertFalse(configuration.isOutputPossible(Level.WARNING));
		assertFalse(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.INFO, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertFalse(configuration.isOutputPossible(Level.INFO));
		assertTrue(configuration.isOutputPossible(Level.WARNING));
		assertTrue(configuration.isOutputPossible(Level.ERROR));
	}

	/**
	 * Test calculating the effective writers.
	 */
	@Test
	public final void testWriters() {
		DummyWriter dummyWriter = new DummyWriter();
		ConsoleWriter consoleWriter = new ConsoleWriter();

		List<WriterDefinition> writerDefinition = emptyWriterDefinition();
		Configuration configuration = new Configuration(Level.INFO, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getWriters(), empty());
		assertThat(configuration.getEffectiveWriters(Level.TRACE), empty());
		assertThat(configuration.getEffectiveWriters(Level.DEBUG), empty());
		assertThat(configuration.getEffectiveWriters(Level.INFO), empty());
		assertThat(configuration.getEffectiveWriters(Level.WARNING), empty());
		assertThat(configuration.getEffectiveWriters(Level.ERROR), empty());

		writerDefinition = singleWriterDefinition(dummyWriter);
		configuration = new Configuration(Level.INFO, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getWriters(), sameContent(dummyWriter));
		assertThat(configuration.getEffectiveWriters(Level.TRACE), sameContent(dummyWriter));
		assertThat(configuration.getEffectiveWriters(Level.DEBUG), sameContent(dummyWriter));
		assertThat(configuration.getEffectiveWriters(Level.INFO), sameContent(dummyWriter));
		assertThat(configuration.getEffectiveWriters(Level.WARNING), sameContent(dummyWriter));
		assertThat(configuration.getEffectiveWriters(Level.ERROR), sameContent(dummyWriter));

		writerDefinition = singleWriterDefinition(dummyWriter, Level.WARNING);
		configuration = new Configuration(Level.INFO, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getWriters(), sameContent(dummyWriter));
		assertThat(configuration.getEffectiveWriters(Level.TRACE), empty());
		assertThat(configuration.getEffectiveWriters(Level.DEBUG), empty());
		assertThat(configuration.getEffectiveWriters(Level.INFO), empty());
		assertThat(configuration.getEffectiveWriters(Level.WARNING), sameContent(dummyWriter));
		assertThat(configuration.getEffectiveWriters(Level.ERROR), sameContent(dummyWriter));

		writerDefinition = pairWriterDefinition(dummyWriter, Level.WARNING, consoleWriter, Level.INFO);
		configuration = new Configuration(Level.INFO, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getWriters(), sameContent(dummyWriter, consoleWriter));
		assertThat(configuration.getEffectiveWriters(Level.TRACE), empty());
		assertThat(configuration.getEffectiveWriters(Level.DEBUG), empty());
		assertThat(configuration.getEffectiveWriters(Level.INFO), sameContent(consoleWriter));
		assertThat(configuration.getEffectiveWriters(Level.WARNING), sameContent(dummyWriter, consoleWriter));
		assertThat(configuration.getEffectiveWriters(Level.ERROR), sameContent(dummyWriter, consoleWriter));
	}

	/**
	 * Test calculating required log entry values from writers.
	 */
	@Test
	public final void testRequiredLogEntryValues() {
		/* No required log entry values */

		List<WriterDefinition> writerDefinition = emptyWriterDefinition();
		Configuration configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), empty());

		writerDefinition = singleWriterDefinition(new DummyWriter((Set<LogEntryValue>) null));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), empty());

		writerDefinition = singleWriterDefinition(new DummyWriter(EnumSet.noneOf(LogEntryValue.class)));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), empty());

		/* Required log entry values from writer */

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.MESSAGE));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), sameContent(LogEntryValue.MESSAGE));

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.MESSAGE), Level.INFO);
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), sameContent(LogEntryValue.MESSAGE));

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));

		writerDefinition = pairWriterDefinition(new DummyWriter(LogEntryValue.LEVEL), new DummyWriter(LogEntryValue.MESSAGE));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));

		writerDefinition = pairWriterDefinition(new DummyWriter(LogEntryValue.LEVEL), Level.INFO, new DummyWriter(LogEntryValue.MESSAGE), Level.WARNING);
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), sameContent(LogEntryValue.LEVEL));
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), sameContent(LogEntryValue.LEVEL, LogEntryValue.MESSAGE));

		/* Required log entry values from format pattern */

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.MESSAGE));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "{level}", Locale.ROOT, writerDefinition, null, 0);
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), sameContent(LogEntryValue.MESSAGE));

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.RENDERED_LOG_ENTRY));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "{level}", Locale.ROOT, writerDefinition, null, 0);
		Object[] expected = new Object[] { LogEntryValue.LEVEL, LogEntryValue.RENDERED_LOG_ENTRY };
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), sameContent(expected));

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.MESSAGE, LogEntryValue.RENDERED_LOG_ENTRY));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "{level}", Locale.ROOT, writerDefinition, null, 0);
		expected = new Object[] { LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.RENDERED_LOG_ENTRY };
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), sameContent(expected));

		writerDefinition = pairWriterDefinition(new DummyWriter(LogEntryValue.MESSAGE), new DummyWriter(LogEntryValue.RENDERED_LOG_ENTRY));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "{level}", Locale.ROOT, writerDefinition, null, 0);
		expected = new Object[] { LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.RENDERED_LOG_ENTRY };
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), sameContent(expected));

		DummyWriter writer1 = new DummyWriter(LogEntryValue.MESSAGE);
		DummyWriter writer2 = new DummyWriter(LogEntryValue.RENDERED_LOG_ENTRY);
		writerDefinition = pairWriterDefinition(writer1, Level.INFO, writer2, Level.WARNING);
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "{level}", Locale.ROOT, writerDefinition, null, 0);
		expected = new Object[] { LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.RENDERED_LOG_ENTRY };
		assertThat(configuration.getRequiredLogEntryValues(Level.TRACE), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.DEBUG), empty());
		assertThat(configuration.getRequiredLogEntryValues(Level.INFO), sameContent(LogEntryValue.MESSAGE));
		assertThat(configuration.getRequiredLogEntryValues(Level.WARNING), sameContent(expected));
		assertThat(configuration.getRequiredLogEntryValues(Level.ERROR), sameContent(expected));
	}

	/**
	 * Test calculating of required stack trace information.
	 */
	@Test
	public final void testRequiredStackTraceInformation() {
		/* Without any required log entry values */

		List<WriterDefinition> writerDefinition = emptyWriterDefinition();
		Configuration configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.ERROR));

		writerDefinition = singleWriterDefinition(new DummyWriter());
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.ERROR));

		writerDefinition = singleWriterDefinition(new DummyWriter());
		configuration = new Configuration(Level.INFO, singleCustomLevel("a", Level.DEBUG), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.ERROR));

		/* Required log entry values from one writer */

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.MESSAGE));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.ERROR));

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.CLASS));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.ERROR));

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.METHOD));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.ERROR));

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.LINE));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.ERROR));

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.FILE));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.ERROR));

		writerDefinition = singleWriterDefinition(new DummyWriter(LogEntryValue.CLASS, LogEntryValue.METHOD));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.ERROR));

		/* Required log entry values from multiple writers */

		writerDefinition = pairWriterDefinition(new DummyWriter(LogEntryValue.CLASS), new DummyWriter(LogEntryValue.METHOD));
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.ERROR));

		writerDefinition = pairWriterDefinition(new DummyWriter(LogEntryValue.CLASS), Level.DEBUG, new DummyWriter(LogEntryValue.METHOD), Level.WARNING);
		configuration = new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, writerDefinition, null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.TRACE));
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.DEBUG));
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation(Level.INFO));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.WARNING));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.ERROR));
	}

	/**
	 * Test copying.
	 */
	@Test
	public final void testCopy() {
		Configuration minimalConfiguration = createMinimalConfigurationSample();
		Configuration minimalCopy = minimalConfiguration.copy().create();
		assertNotSame(minimalConfiguration, minimalCopy);
		testMinimalConfigurationSample(minimalCopy);

		Configuration detailedConfiguration = createDetailedConfigurationSample();
		Configuration detailedCopy = detailedConfiguration.copy().create();
		assertNotSame(detailedConfiguration, detailedCopy);
		testDetailedConfigurationSample(detailedCopy);
	}

	private Configuration createMinimalConfigurationSample() {
		return new Configuration(Level.TRACE, noCustomLevels(), "", Locale.ROOT, emptyWriterDefinition(), null, 0);
	}

	private void testMinimalConfigurationSample(final Configuration configuration) {
		assertEquals(Level.TRACE, configuration.getLevel());
		assertFalse(configuration.hasCustomLevels());
		assertEquals(Level.TRACE, configuration.getLevel(ConfigurationTest.class.getName()));
		assertEquals(Level.TRACE, configuration.getLevel(ConfigurationTest.class.getPackage().getName()));
		assertEquals("", configuration.getFormatPattern());
		assertEquals(Collections.emptyList(), configuration.getFormatTokens());
		assertEquals(Locale.ROOT, configuration.getLocale());
		assertThat(configuration.getWriters(), empty());
		assertThat(configuration.getEffectiveWriters(Level.ERROR), empty());
		assertNull(configuration.getWritingThread());
		assertEquals(0, configuration.getMaxStackTraceElements());
		assertEquals(Collections.emptySet(), configuration.getRequiredLogEntryValues(Level.ERROR));
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation(Level.ERROR));
	}

	private Configuration createDetailedConfigurationSample() {
		Map<String, Level> packageLevels = new HashMap<String, Level>();
		packageLevels.put(ConfigurationTest.class.getPackage().getName(), Level.INFO);
		return new Configuration(Level.WARNING, packageLevels, "{class}{method}", Locale.GERMANY, singleWriterDefinition(new DummyWriter(
				LogEntryValue.RENDERED_LOG_ENTRY)), new WritingThread(null, Thread.MIN_PRIORITY), Integer.MAX_VALUE);
	}

	private void testDetailedConfigurationSample(final Configuration configuration) {
		assertEquals(Level.WARNING, configuration.getLevel());
		assertTrue(configuration.hasCustomLevels());
		assertEquals(Level.WARNING, configuration.getLevel("invalid"));
		assertEquals(Level.INFO, configuration.getLevel(ConfigurationTest.class.getName()));
		assertEquals(Level.WARNING, configuration.getLevel("invalid"));
		assertEquals(Level.INFO, configuration.getLevel(ConfigurationTest.class.getPackage().getName()));
		assertEquals("{class}{method}", configuration.getFormatPattern());
		assertEquals(2, configuration.getFormatTokens().size());
		assertEquals(Locale.GERMANY, configuration.getLocale());
		assertThat(configuration.getWriters(), types(DummyWriter.class));
		assertThat(configuration.getEffectiveWriters(Level.ERROR), types(DummyWriter.class));
		assertNotNull(configuration.getWritingThread());
		assertEquals(State.NEW, configuration.getWritingThread().getState());
		assertEquals(Integer.MAX_VALUE, configuration.getMaxStackTraceElements());
		assertEquals(EnumSet.of(LogEntryValue.RENDERED_LOG_ENTRY, LogEntryValue.CLASS, LogEntryValue.METHOD),
				configuration.getRequiredLogEntryValues(Level.ERROR));
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation(Level.ERROR));
	}

	private static Map<String, Level> noCustomLevels() {
		return Collections.<String, Level> emptyMap();
	}

	private static Map<String, Level> singleCustomLevel(final String packageOrClass, final Level level) {
		return Collections.singletonMap(packageOrClass, level);
	}

	private static Map<String, Level> pairCustomLevels(final String packageOrClass1, final Level level1, final String packageOrClass2, final Level level2) {
		Map<String, Level> customLevels = new HashMap<String, Level>();
		customLevels.put(packageOrClass1, level1);
		customLevels.put(packageOrClass2, level2);
		return customLevels;
	}

	private static List<WriterDefinition> emptyWriterDefinition() {
		return Collections.<WriterDefinition> emptyList();
	}

	private static List<WriterDefinition> singleWriterDefinition(final Writer writer) {
		return Collections.<WriterDefinition> singletonList(new WriterDefinition(writer));
	}

	private static List<WriterDefinition> singleWriterDefinition(final Writer writer, final Level level) {
		return Collections.<WriterDefinition> singletonList(new WriterDefinition(writer, level));
	}

	private static List<WriterDefinition> pairWriterDefinition(final Writer writer1, final Writer writer2) {
		return Arrays.asList(new WriterDefinition(writer1), new WriterDefinition(writer2));
	}

	private static List<WriterDefinition> pairWriterDefinition(final Writer writer1, final Level level1, final Writer writer2, final Level level2) {
		return Arrays.asList(new WriterDefinition(writer1, level1), new WriterDefinition(writer2, level2));
	}

	private static final class DummyWriter implements Writer {

		private final Set<LogEntryValue> requiredLogEntryValue;

		public DummyWriter() {
			this.requiredLogEntryValue = Collections.emptySet();
		}

		public DummyWriter(final Set<LogEntryValue> requiredLogEntryValues) {
			this.requiredLogEntryValue = requiredLogEntryValues;
		}

		public DummyWriter(final LogEntryValue... requiredLogEntryValues) {
			this.requiredLogEntryValue = EnumSet.copyOf(Arrays.asList(requiredLogEntryValues));
		}

		@Override
		public Set<LogEntryValue> getRequiredLogEntryValues() {
			return requiredLogEntryValue;
		}

		@Override
		public void init(final Configuration configuration) {
			// Do nothing
		}

		@Override
		public void write(final LogEntry logEntry) {
			// Just ignore
		}

		@Override
		public void flush() {
			// Do nothing
		}

		@Override
		public void close() {
			// Do nothing
		}

	}

}
