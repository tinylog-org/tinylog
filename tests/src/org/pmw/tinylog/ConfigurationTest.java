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
import static org.pmw.tinylog.hamcrest.CollectionMatchers.types;

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
	 * Test check if output of log entries is possible.
	 */
	@Test
	public final void testOutputPossible() {
		Configuration configuration = new Configuration(Level.OFF, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> emptyList(), null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertFalse(configuration.isOutputPossible(Level.INFO));
		assertFalse(configuration.isOutputPossible(Level.WARNING));
		assertFalse(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.OFF, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter()), null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertFalse(configuration.isOutputPossible(Level.INFO));
		assertFalse(configuration.isOutputPossible(Level.WARNING));
		assertFalse(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.INFO, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter()), null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertTrue(configuration.isOutputPossible(Level.INFO));
		assertTrue(configuration.isOutputPossible(Level.WARNING));
		assertTrue(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.INFO, Collections.singletonMap("a", Level.ERROR), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter()), null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertFalse(configuration.isOutputPossible(Level.DEBUG));
		assertTrue(configuration.isOutputPossible(Level.INFO));
		assertTrue(configuration.isOutputPossible(Level.WARNING));
		assertTrue(configuration.isOutputPossible(Level.ERROR));

		configuration = new Configuration(Level.INFO, Collections.singletonMap("a", Level.DEBUG), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter()), null, 0);
		assertFalse(configuration.isOutputPossible(Level.TRACE));
		assertTrue(configuration.isOutputPossible(Level.DEBUG));
		assertTrue(configuration.isOutputPossible(Level.INFO));
		assertTrue(configuration.isOutputPossible(Level.WARNING));
		assertTrue(configuration.isOutputPossible(Level.ERROR));

		Map<String, Level> customLevels = new HashMap<String, Level>();
		customLevels.put("a", Level.OFF);
		customLevels.put("b", Level.TRACE);
		customLevels.put("c", Level.DEBUG);
		configuration = new Configuration(Level.INFO, customLevels, "", Locale.ROOT, Collections.<Writer> singletonList(new DummyWriter()), null, 0);
		assertTrue(configuration.isOutputPossible(Level.TRACE));
		assertTrue(configuration.isOutputPossible(Level.DEBUG));
		assertTrue(configuration.isOutputPossible(Level.INFO));
		assertTrue(configuration.isOutputPossible(Level.WARNING));
		assertTrue(configuration.isOutputPossible(Level.ERROR));
	}

	/**
	 * Test calculating the effective writers.
	 */
	@Test
	public final void testEffectiveWriter() {
		Configuration configuration = new Configuration(Level.OFF, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter()), null, 0);
		assertThat(configuration.getEffectiveWriters(), empty());

		configuration = new Configuration(Level.OFF, Collections.singletonMap("a", Level.INFO), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter()), null, 0);
		assertThat(configuration.getEffectiveWriters(), types(DummyWriter.class));

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT, Collections.<Writer> emptyList(),
				null, 0);
		assertThat(configuration.getEffectiveWriters(), empty());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter()), null, 0);
		assertThat(configuration.getEffectiveWriters(), types(DummyWriter.class));
	}

	/**
	 * Test required log entry values for writers without any of them.
	 */
	@Test
	public final void testEmptyRequiredLogEntryValues() {
		Configuration configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter((Set<LogEntryValue>) null)), null, 0);
		assertEquals(Collections.emptySet(), configuration.getRequiredLogEntryValues());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(Collections.<LogEntryValue> emptySet())), null, 0);
		assertEquals(Collections.emptySet(), configuration.getRequiredLogEntryValues());
	}

	/**
	 * Test required log entry values for a writes with all of them.
	 */
	@Test
	public final void testAllRequiredLogEntryValues() {
		DummyWriter writer = new DummyWriter(EnumSet.allOf(LogEntryValue.class));
		Configuration configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(writer), null, 0);
		assertEquals(EnumSet.allOf(LogEntryValue.class), configuration.getRequiredLogEntryValues());
	}

	/**
	 * Test calculating required log entry values from format pattern.
	 */
	@Test
	public final void testRequiredLogEntryValuesFromFormatPattern() {
		String formatPattern = "{date}#{thread}#{class}#{method}#{file}#{line}#{level}#{message}";
		DummyWriter writer = new DummyWriter(EnumSet.of(LogEntryValue.RENDERED_LOG_ENTRY));

		Configuration configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), formatPattern, Locale.ROOT,
				Collections.<Writer> singletonList(writer), null, 0);

		Set<LogEntryValue> expected = EnumSet.of(LogEntryValue.DATE, LogEntryValue.THREAD, LogEntryValue.CLASS, LogEntryValue.METHOD, LogEntryValue.FILE,
				LogEntryValue.LINE_NUMBER, LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.RENDERED_LOG_ENTRY);
		assertEquals(expected, configuration.getRequiredLogEntryValues());
	}

	/**
	 * Test calculating of required stack trace information.
	 */
	@Test
	public final void testRequiredStackTraceInformation() {
		/* Disabled logging */

		Configuration configuration = new Configuration(Level.OFF, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.METHOD)), null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.OFF, Collections.singletonMap("a", Level.INFO), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.METHOD)), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT, Collections.<Writer> emptyList(),
				null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());

		/* No requirements defined */

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter()), null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.singletonMap("a", Level.OFF), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter()), null, 0);
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation());

		/* Requirement from writer */

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.THREAD)), null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.CLASS)), null, 0);
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.METHOD)), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.FILE)), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.LINE_NUMBER)), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		/* Requirement from format pattern */

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "{thread}", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.RENDERED_LOG_ENTRY)), null, 0);
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "{class}", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.RENDERED_LOG_ENTRY)), null, 0);
		assertEquals(StackTraceInformation.CLASS_NAME, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "{method}", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.RENDERED_LOG_ENTRY)), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "{method}", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.RENDERED_LOG_ENTRY)), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "{file}", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.RENDERED_LOG_ENTRY)), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());

		configuration = new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "{line}", Locale.ROOT,
				Collections.<Writer> singletonList(new DummyWriter(LogEntryValue.RENDERED_LOG_ENTRY)), null, 0);
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());
	}

	private Configuration createMinimalConfigurationSample() {
		return new Configuration(Level.TRACE, Collections.<String, Level> emptyMap(), "", Locale.ROOT, Collections.<Writer> emptyList(), null, 0);
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
		assertThat(configuration.getEffectiveWriters(), empty());
		assertNull(configuration.getWritingThread());
		assertEquals(0, configuration.getMaxStackTraceElements());
		assertEquals(Collections.emptySet(), configuration.getRequiredLogEntryValues());
		assertEquals(StackTraceInformation.NONE, configuration.getRequiredStackTraceInformation());
	}

	private Configuration createDetailedConfigurationSample() {
		Map<String, Level> packageLevels = new HashMap<String, Level>();
		packageLevels.put(ConfigurationTest.class.getPackage().getName(), Level.INFO);
		return new Configuration(Level.WARNING, packageLevels, "{class}{method}", Locale.GERMANY, Collections.<Writer> singletonList(new DummyWriter(
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
		assertThat(configuration.getEffectiveWriters(), types(DummyWriter.class));
		assertNotNull(configuration.getWritingThread());
		assertEquals(State.NEW, configuration.getWritingThread().getState());
		assertEquals(Integer.MAX_VALUE, configuration.getMaxStackTraceElements());
		assertEquals(EnumSet.of(LogEntryValue.RENDERED_LOG_ENTRY, LogEntryValue.CLASS, LogEntryValue.METHOD), configuration.getRequiredLogEntryValues());
		assertEquals(StackTraceInformation.FULL, configuration.getRequiredStackTraceInformation());
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
