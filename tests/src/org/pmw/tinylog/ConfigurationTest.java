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

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.Thread.State;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.pmw.tinylog.writers.LogEntry;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.LoggingWriter;

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

	/**
	 * Test calculating effective logging writer.
	 */
	@Test
	public final void testEffectiveWriter() {
		Configuration configuration = new Configuration(LoggingLevel.OFF, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(),
				null, 0);
		assertNull(configuration.getEffectiveWriter());

		configuration = new Configuration(LoggingLevel.OFF, Collections.singletonMap("a", LoggingLevel.INFO), "", Locale.ROOT, new DummyWriter(), null, 0);
		assertThat(configuration.getEffectiveWriter(), instanceOf(DummyWriter.class));

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, null, null, 0);
		assertNull(configuration.getEffectiveWriter());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(), null, 0);
		assertThat(configuration.getEffectiveWriter(), instanceOf(DummyWriter.class));
	}

	/**
	 * Test required log entry values for writers without any of them.
	 */
	@Test
	public final void testEmptyRequiredLogEntryValues() {
		Configuration configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(
				(Set<LogEntryValue>) null), null, 0);
		assertEquals(Collections.emptySet(), configuration.getRequiredLogEntryValues());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(
				Collections.<LogEntryValue> emptySet()), null, 0);
		assertEquals(Collections.emptySet(), configuration.getRequiredLogEntryValues());
	}

	/**
	 * Test required log entry values for a writes with all of them.
	 */
	@Test
	public final void testAllRequiredLogEntryValues() {
		DummyWriter writer = new DummyWriter(EnumSet.allOf(LogEntryValue.class));
		Configuration configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, writer, null, 0);
		assertEquals(EnumSet.allOf(LogEntryValue.class), configuration.getRequiredLogEntryValues());
	}

	/**
	 * Test calculating required log entry values from format pattern.
	 */
	@Test
	public final void testRequiredLogEntryValuesFromFormatPattern() {
		String formatPattern = "{date}#{thread}#{class}#{method}#{file}#{line}#{level}#{message}";
		DummyWriter writer = new DummyWriter(EnumSet.of(LogEntryValue.RENDERED_LOG_ENTRY));

		Configuration configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), formatPattern, Locale.ROOT, writer,
				null, 0);

		Set<LogEntryValue> expected = EnumSet.of(LogEntryValue.DATE, LogEntryValue.THREAD, LogEntryValue.CLASS, LogEntryValue.METHOD, LogEntryValue.FILE,
				LogEntryValue.LINE_NUMBER, LogEntryValue.LOGGING_LEVEL, LogEntryValue.MESSAGE, LogEntryValue.RENDERED_LOG_ENTRY);
		assertEquals(expected, configuration.getRequiredLogEntryValues());
	}

	/**
	 * Test calculating of required stack trace information.
	 */
	@Test
	public final void testRequiredStackTraceInformation() {
		/* Disabled logging */

		Configuration configuration = new Configuration(LoggingLevel.OFF, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(
				LogEntryValue.METHOD), null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.OFF, Collections.singletonMap("a", LoggingLevel.INFO), "", Locale.ROOT, new DummyWriter(
				LogEntryValue.METHOD), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, null, null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());

		/* No requirements defined */

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(), null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.singletonMap("a", LoggingLevel.OFF), "", Locale.ROOT, new DummyWriter(), null, 0);
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation());

		/* Requirement from logging writer */

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(
				LogEntryValue.THREAD), null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(
				LogEntryValue.CLASS), null, 0);
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(
				LogEntryValue.METHOD), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(
				LogEntryValue.FILE), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, new DummyWriter(
				LogEntryValue.LINE_NUMBER), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		/* Requirement from format pattern */

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "{thread}", Locale.ROOT, new DummyWriter(
				LogEntryValue.RENDERED_LOG_ENTRY), null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "{class}", Locale.ROOT, new DummyWriter(
				LogEntryValue.RENDERED_LOG_ENTRY), null, 0);
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "{method}", Locale.ROOT, new DummyWriter(
				LogEntryValue.RENDERED_LOG_ENTRY), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "{method}", Locale.ROOT, new DummyWriter(
				LogEntryValue.RENDERED_LOG_ENTRY), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "{file}", Locale.ROOT, new DummyWriter(
				LogEntryValue.RENDERED_LOG_ENTRY), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "{line}", Locale.ROOT, new DummyWriter(
				LogEntryValue.RENDERED_LOG_ENTRY), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());
	}

	private Configuration createMinimalConfigurationSample() {
		return new Configuration(LoggingLevel.TRACE, Collections.<String, LoggingLevel> emptyMap(), "", Locale.ROOT, null, null, 0);
	}

	private void testMinimalConfigurationSample(final Configuration configuration) {
		assertEquals(LoggingLevel.TRACE, configuration.getLevel());
		assertFalse(configuration.hasCustomLoggingLevels());
		assertEquals(LoggingLevel.TRACE, configuration.getLevel(ConfigurationTest.class.getName()));
		assertEquals(LoggingLevel.TRACE, configuration.getLevel(ConfigurationTest.class.getPackage().getName()));
		assertEquals("", configuration.getFormatPattern());
		assertEquals(Collections.emptyList(), configuration.getFormatTokens());
		assertEquals(Locale.ROOT, configuration.getLocale());
		assertNull(configuration.getWriter());
		assertNull(configuration.getEffectiveWriter());
		assertNull(configuration.getWritingThread());
		assertEquals(0, configuration.getMaxStackTraceElements());
		assertEquals(Collections.emptySet(), configuration.getRequiredLogEntryValues());
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());
	}

	private Configuration createDetailedConfigurationSample() {
		Map<String, LoggingLevel> packageLevels = new HashMap<String, LoggingLevel>();
		packageLevels.put(ConfigurationTest.class.getPackage().getName(), LoggingLevel.INFO);
		return new Configuration(LoggingLevel.WARNING, packageLevels, "{class}{method}", Locale.GERMANY, new DummyWriter(LogEntryValue.RENDERED_LOG_ENTRY),
				new WritingThread(null, Thread.MIN_PRIORITY), Integer.MAX_VALUE);
	}

	private void testDetailedConfigurationSample(final Configuration configuration) {
		assertEquals(LoggingLevel.WARNING, configuration.getLevel());
		assertTrue(configuration.hasCustomLoggingLevels());
		assertEquals(LoggingLevel.WARNING, configuration.getLevel("invalid"));
		assertEquals(LoggingLevel.INFO, configuration.getLevel(ConfigurationTest.class.getName()));
		assertEquals(LoggingLevel.WARNING, configuration.getLevel("invalid"));
		assertEquals(LoggingLevel.INFO, configuration.getLevel(ConfigurationTest.class.getPackage().getName()));
		assertEquals("{class}{method}", configuration.getFormatPattern());
		assertEquals(2, configuration.getFormatTokens().size());
		assertEquals(Locale.GERMANY, configuration.getLocale());
		assertThat(configuration.getWriter(), instanceOf(DummyWriter.class));
		assertThat(configuration.getEffectiveWriter(), instanceOf(DummyWriter.class));
		assertNotNull(configuration.getWritingThread());
		assertEquals(State.NEW, configuration.getWritingThread().getState());
		assertEquals(Integer.MAX_VALUE, configuration.getMaxStackTraceElements());
		assertEquals(EnumSet.of(LogEntryValue.RENDERED_LOG_ENTRY, LogEntryValue.CLASS, LogEntryValue.METHOD), configuration.getRequiredLogEntryValues());
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());
	}

	private static final class DummyWriter implements LoggingWriter {

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
		public void close() {
			// Do nothing
		}

	}

}
