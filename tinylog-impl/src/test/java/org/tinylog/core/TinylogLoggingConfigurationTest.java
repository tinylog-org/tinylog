/*
 * Copyright 2017 Martin Winandy
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

package org.tinylog.core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.throwable.ThrowableFilter;
import org.tinylog.util.ConfigurationStoreWriter;
import org.tinylog.writers.ConsoleWriter;
import org.tinylog.writers.FileWriter;
import org.tinylog.writers.Writer;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link ConfigurationParser}.
 */
public final class TinylogLoggingConfigurationTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Resets configuration.
	 */
	@Before
	@After
	public void reset() {
		Whitebox.setInternalState(Configuration.class, "frozen", false);
		Configuration.replace(emptyMap());
	}
	
	/**
	 * Verifies that a default writer will be created for all tags and severity levels, if logging is enabled but no
	 * writer explicitly defined.
	 */
	@Test
	public void defaultWriter() {
		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(emptyList(), Level.TRACE, false);

		assertThat(writers).hasSize(2).allSatisfy(element ->
			assertThat(element).hasSize(5).allSatisfy(collection ->
				assertThat(collection).hasSize(1).allSatisfy(writer -> assertThat(writer).isNotNull())
			)
		);
	}

	/**
	 * Verifies that no writer will be created, if logging is disabled.
	 */
	@Test
	public void noWriters() {
		Configuration.set("writer", "console");
		
		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(emptyList(), Level.OFF, false);

		assertThat(writers).hasSize(2).allSatisfy(element ->
			assertThat(element).hasSize(5).allSatisfy(collection -> assertThat(collection).isEmpty())
		);
	}

	/**
	 * Verifies that a single tagged writer will be created and assigned correctly.
	 */
	@Test
	public void singleTaggedWriter() {
		Configuration.set("writer", "console");
		Configuration.set("writer.tag", "SYSTEM");

		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(singletonList("SYSTEM"), Level.TRACE, false);

		assertThat(writers)
			.hasSize(3)
			.allSatisfy(element -> assertThat(element).hasSize(5));

		assertThat(writers[0]).allSatisfy(collection -> assertThat(collection).isEmpty());
		assertThat(writers[1]).allSatisfy(collection ->
			assertThat(collection).hasSize(1).allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class))
		);
		assertThat(writers[2]).allSatisfy(collection -> assertThat(collection).isEmpty());
	}

	/**
	 * Verifies that a single explicitly untagged writer will be created and assigned correctly.
	 */
	@Test
	public void singleUntaggedWriter() {
		Configuration.set("writer", "console");
		Configuration.set("writer.tag", "-");

		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(emptyList(), Level.TRACE, false);

		assertThat(writers).hasSize(2);
		assertThat(writers[0]).allSatisfy(collection ->
			assertThat(collection).hasSize(1).allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class))
		);
		assertThat(writers[1]).allSatisfy(collection -> assertThat(collection).isEmpty());
	}

	/**
	 * Verifies that a single tagged writer with multiple tags will be created and assigned correctly.
	 */
	@Test
	public void singleMultipleTaggedWriter() {
		Configuration.set("writer", "console");
		Configuration.set("writer.tag", " system , , backup  , test, , "); // Test also unusual tag entries
		
		List<String> tags = ConfigurationParser.getTags();
		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(tags, Level.TRACE, false);

		assertThat(writers)
			.hasSize(5)
			.allSatisfy(element -> assertThat(element).hasSize(5));

		assertThat(writers[0]).allSatisfy(collection -> assertThat(collection).isEmpty());
		assertThat(writers[1]).allSatisfy(collection -> {
			assertThat(collection).hasSize(1).allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
		});
		assertThat(writers[2]).allSatisfy(collection -> {
			assertThat(collection).hasSize(1).allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
		});
		assertThat(writers[3]).allSatisfy(collection -> {
			assertThat(collection).hasSize(1).allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
		});
		assertThat(writers[4]).allSatisfy(collection -> assertThat(collection).isEmpty());
	}
	
	/**
	 * Verifies that two tagged writers will be created and assigned correctly.
	 *
	 * @throws IOException
	 *             Failed to create temporary log file
	 */
	@Test
	public void multipleTaggedWriter() throws IOException {
		Configuration.set("writer1", "console");
		Configuration.set("writer1.tag", "SYSTEM");

		Configuration.set("writer2", "file");
		Configuration.set("writer2.tag", "SYSTEM");
		Configuration.set("writer2.file", File.createTempFile("tinylog", ".log").getAbsolutePath());

		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(singletonList("SYSTEM"), Level.TRACE, false);

		assertThat(writers)
			.hasSize(3)
			.allSatisfy(element -> assertThat(element).hasSize(5));

		assertThat(writers[0]).allSatisfy(collection -> assertThat(collection).isEmpty());
		assertThat(writers[1]).allSatisfy(collection ->
			assertThat(collection)
				.hasSize(2)
				.hasAtLeastOneElementOfType(ConsoleWriter.class)
				.hasAtLeastOneElementOfType(FileWriter.class)
		);
		assertThat(writers[2]).allSatisfy(collection -> assertThat(collection).isEmpty());
	}

	/**
	 * Verifies that a writer with a defined severity level, which is above the minimum severity level, will be only
	 * assigned to the configured severity levels.
	 */
	@Test
	public void levelOfWriterAboveMinimumLevel() {
		Configuration.set("writer", "console");
		Configuration.set("writer.level", "WARN");

		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(emptyList(), Level.TRACE, false);

		assertThat(writers).hasSize(2).allSatisfy(element -> {
			assertThat(element).hasSize(5);
			assertThat(element[Level.TRACE.ordinal()]).isEmpty();
			assertThat(element[Level.DEBUG.ordinal()]).isEmpty();
			assertThat(element[Level.INFO.ordinal()]).isEmpty();
			assertThat(element[Level.WARN.ordinal()])
				.hasSize(1)
				.allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
			assertThat(element[Level.ERROR.ordinal()])
				.hasSize(1)
				.allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
		});
	}

	/**
	 * Verifies that a writer with a defined severity level, which is below the minimum severity level, will be not
	 * assigned to severity levels below the minimum severity level.
	 */
	@Test
	public void levelOfWriterBelowMinimumLevel() {
		Configuration.set("writer", "console");
		Configuration.set("writer.level", "trace");

		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(emptyList(), Level.INFO, false);

		assertThat(writers).hasSize(2).allSatisfy(element -> {
			assertThat(element).hasSize(5);
			assertThat(element[Level.TRACE.ordinal()]).isEmpty();
			assertThat(element[Level.DEBUG.ordinal()]).isEmpty();
			assertThat(element[Level.INFO.ordinal()])
				.hasSize(1)
				.allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
			assertThat(element[Level.WARN.ordinal()])
				.hasSize(1)
				.allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
			assertThat(element[Level.ERROR.ordinal()])
				.hasSize(1)
				.allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
		});
	}

	/**
	 * Verifies that writers with the severity level {@link Level#OFF OFF} will not be created.
	 */
	@Test
	public void levelOfWriterIsOff() {
		Configuration.set("writer", "console");
		Configuration.set("writer.level", "off");

		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(emptyList(), Level.INFO, false);

		assertThat(writers).hasSize(2).allSatisfy(element ->
			assertThat(element).hasSize(5).allSatisfy(collection -> assertThat(collection).isEmpty())
		);
	}

	/**
	 * Verifies that a {@link ThrowableFilter} can be registered globally.
	 */
	@Test
	public void globalException() {
		Configuration.set("exception", "drop cause");
		Configuration.set("writer", "configuration store");
		
		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(emptyList(), Level.INFO, false);
		ConfigurationStoreWriter writer = (ConfigurationStoreWriter) writers[0][Level.INFO.ordinal()].iterator().next();
		assertThat(writer.getProperties()).contains(entry("exception", "drop cause"));
	}
	
	/**
	 * Verifies that a {@link ThrowableFilter} can be registered on a writer.
	 */
	@Test
	public void localException() {
		Configuration.set("writer", "configuration store");
		Configuration.set("writer.exception", "unpack");
		
		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(emptyList(), Level.INFO, false);
		ConfigurationStoreWriter writer = (ConfigurationStoreWriter) writers[0][Level.INFO.ordinal()].iterator().next();
		assertThat(writer.getProperties()).contains(entry("exception", "unpack"));
	}
	
	/**
	 * Verifies that a global {@link ThrowableFilter} can be overridden by another one directly registered on a writer.
	 */
	@Test
	public void overrideException() {
		Configuration.set("exception", "drop cause");
		Configuration.set("writer", "configuration store");
		Configuration.set("writer.exception", "unpack");
		
		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		Collection<Writer>[][] writers = config.createWriters(emptyList(), Level.INFO, false);
		ConfigurationStoreWriter writer = (ConfigurationStoreWriter) writers[0][Level.INFO.ordinal()].iterator().next();
		assertThat(writer.getProperties()).contains(entry("exception", "unpack"));
	}

	/**
	 * Verifies that the minimum level is calculated properly from the global and custom level.
	 */
	@Test
	public void calcMinimumLevelFromGlobalLevel() {
		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		
		Level level1 = config.calculateMinimumLevel(Level.INFO, Collections.emptyMap());
		assertThat(level1).isEqualTo(Level.INFO);

		Level level2 = config.calculateMinimumLevel(Level.INFO, Collections.singletonMap("test", Level.TRACE));
		assertThat(level2).isEqualTo(Level.TRACE);

		Level level3 = config.calculateMinimumLevel(Level.INFO, Collections.singletonMap("test", Level.ERROR));
		assertThat(level3).isEqualTo(Level.INFO);
	}	

	/**
	 * Verifies that the required log entry values are calculated properly from the writers.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void calcRequiredLogEntryValues() {
		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		
		Collection<Writer>[][] writers = new Collection[2][Level.values().length - 1];
		for (Collection<Writer>[] writer: writers) {
			Arrays.fill(writer, Collections.emptyList());
		}
		writers[0][0] = Collections.singleton(new WriterOne());
		writers[0][1] = Collections.singleton(new WriterTwo());
		writers[1][0] = Arrays.asList(new Writer[] {new WriterOne(), new WriterTwo()});
		
		Collection<LogEntryValue>[][] values = config.calculateRequiredLogEntryValues(writers);
		assertThat(values[0][0]).containsExactlyInAnyOrder(LogEntryValue.TAG, LogEntryValue.LEVEL);
		assertThat(values[0][1]).containsExactlyInAnyOrder(LogEntryValue.DATE, LogEntryValue.LEVEL, LogEntryValue.FILE);
		assertThat(values[1][0]).containsExactlyInAnyOrder(LogEntryValue.TAG, LogEntryValue.LEVEL, LogEntryValue.DATE, LogEntryValue.FILE);
		assertThat(values[1][1]).isEmpty();
	}	
	
	/**
	 * Verifies that the full stack trace requirements are calculated properly from the log entries.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void calcFullStackTraceRequirements() {
		TinylogLoggingConfiguration config = new TinylogLoggingConfiguration();
		
		Collection<LogEntryValue>[][] logEntries = new Collection[5][Level.values().length - 1];
		for (Collection<LogEntryValue>[] logEntry: logEntries) {
			Arrays.fill(logEntry, Collections.emptyList());
		}
		logEntries[0][Level.ERROR.ordinal()] = Collections.singleton(LogEntryValue.TAG);
		logEntries[1][Level.ERROR.ordinal()] = Collections.singleton(LogEntryValue.METHOD);
		logEntries[2][Level.ERROR.ordinal()] = Collections.singleton(LogEntryValue.FILE);
		logEntries[3][Level.ERROR.ordinal()] = Collections.singleton(LogEntryValue.LINE);
		logEntries[4][Level.ERROR.ordinal()] = Arrays.asList(new LogEntryValue[] {LogEntryValue.FILE, LogEntryValue.LINE});
		
		BitSet requirements = config.calculateFullStackTraceRequirements(logEntries);
		assertThat(requirements.get(0)).isFalse();
		assertThat(requirements.get(1)).isTrue();
		assertThat(requirements.get(2)).isTrue();
		assertThat(requirements.get(3)).isTrue();
		assertThat(requirements.get(4)).isTrue();
	}	

	/**
	 * Verifies that all writers can be obained.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void getAllWriters() {
		Collection<Writer>[][] writers = new Collection[2][Level.values().length - 1];
		for (Collection<Writer>[] writer: writers) {
			Arrays.fill(writer, Collections.emptyList());
		}
		writers[0][0] = Collections.singleton(new WriterOne());
		writers[0][1] = Collections.singleton(new WriterTwo());
		writers[1][0] = Arrays.asList(new Writer[] {new WriterOne(), new WriterTwo()});
		
		Collection<Writer> allWriters = TinylogLoggingConfiguration.getAllWriters(writers);
		assertThat(allWriters)
			.hasSize(4)
			.hasAtLeastOneElementOfType(WriterOne.class)
			.hasAtLeastOneElementOfType(WriterTwo.class);
	}		
	
	/**
	 * Verifies that a log entry can be created properly.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void createLogEntry() {
		TinylogContextProvider contextProvider = new TinylogContextProvider();
		contextProvider.put("id", "100");
		Collection<LogEntryValue>[] requiredLogEntryValues = new Collection[Level.values().length - 1];

		requiredLogEntryValues[Level.INFO.ordinal()] = Arrays.asList(new LogEntryValue[] {LogEntryValue.TAG, LogEntryValue.LEVEL});
		LogEntry entry1 = TinylogLoggingConfiguration.createLogEntry(null, "testtag", Level.INFO, null, null, null,
				null, requiredLogEntryValues, null);
		assertThat(entry1.getTag()).isEqualTo("testtag");
		assertThat(entry1.getLevel()).isEqualTo(Level.INFO);
		
		requiredLogEntryValues[Level.INFO.ordinal()] = Arrays.asList(new LogEntryValue[] {LogEntryValue.CONTEXT});
		LogEntry entry2 = TinylogLoggingConfiguration.createLogEntry(null, null, Level.INFO, null, null, null,
				null, requiredLogEntryValues, contextProvider);
		assertThat(entry2.getContext()).containsAllEntriesOf(Collections.singletonMap("id", "100"));
		
		requiredLogEntryValues[Level.INFO.ordinal()] = Arrays.asList(new LogEntryValue[] {LogEntryValue.EXCEPTION});
		LogEntry entry3 = TinylogLoggingConfiguration.createLogEntry(null, null, Level.INFO, new Exception("test"), null, null,
				null, requiredLogEntryValues, null);
		assertThat(entry3.getException().getMessage()).isEqualTo("test");
	}	
	
	/**
	 * Dummy writer class for log entry testing.
	 */
	public static final class WriterOne extends AbstractWriter {
		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			LogEntryValue[] values = new LogEntryValue[] {LogEntryValue.TAG, LogEntryValue.LEVEL};
			return Arrays.asList(values);
		}
	}

	/**
	 * Dummy writer class for log entry testing.
	 */
	public static final class WriterTwo extends AbstractWriter {
		
		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			LogEntryValue[] values = new LogEntryValue[] {LogEntryValue.DATE, LogEntryValue.LEVEL, LogEntryValue.FILE};
			return Arrays.asList(values);
		}
		
	}

	/**
	 * Base dummy writer class that does nothing.
	 */
	public abstract static class AbstractWriter implements Writer {

		@Override
		public Collection<LogEntryValue> getRequiredLogEntryValues() {
			return null;
		}

		@Override
		public void write(final LogEntry logEntry) throws Exception {
		}

		@Override
		public void flush() throws Exception {
		}

		@Override
		public void close() throws Exception {
		}

	}
	
}
