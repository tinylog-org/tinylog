/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog.writers;

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.Level;
import org.tinylog.core.LogEntryValue;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.LogEntryBuilder;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConsoleWriter}.
 */
public final class ConsoleWriterTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Verifies that all required log entry values will be detected.
	 */
	@Test
	public void requiredLogEntryValues() {
		ConsoleWriter writer = new ConsoleWriter(singletonMap("format", "{message}"));
		assertThat(writer.getRequiredLogEntryValues()).containsOnly(LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
	}

	/**
	 * Verifies that the default pattern contains a minimum set of informations.
	 */
	@Test
	public void defaultFormatPattern() {
		ConsoleWriter writer = new ConsoleWriter(emptyMap());

		assertThat(writer.getRequiredLogEntryValues())
			.contains(LogEntryValue.DATE, LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);

		writer.write(LogEntryBuilder.prefilled(FileWriterTest.class).create());
		writer.close();

		assertThat(systemStream.consumeStandardOutput())
			.contains("1985").contains("03")
			.contains("TRACE")
			.contains("Hello World!")
			.endsWith(NEW_LINE);
	}

	/**
	 * Verifies that a trace log entry will be written to standard output stream.
	 */
	@Test
	public void trace() {
		ConsoleWriter writer = new ConsoleWriter(singletonMap("format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.TRACE).message("Hello World!").create());

		assertThat(systemStream.consumeStandardOutput()).contains("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that a debug log entry will be written to standard output stream.
	 */
	@Test
	public void debug() {
		ConsoleWriter writer = new ConsoleWriter(singletonMap("format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.DEBUG).message("Hello World!").create());

		assertThat(systemStream.consumeStandardOutput()).contains("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that an info log entry will be written to standard output stream.
	 */
	@Test
	public void info() {
		ConsoleWriter writer = new ConsoleWriter(singletonMap("format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.INFO).message("Hello World!").create());

		assertThat(systemStream.consumeStandardOutput()).contains("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that a warning log entry will be written to error output stream.
	 */
	@Test
	public void warning() {
		ConsoleWriter writer = new ConsoleWriter(singletonMap("format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.WARNING).message("Hello World!").create());

		assertThat(systemStream.consumeErrorOutput()).contains("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that an error log entry will be written to error output stream.
	 */
	@Test
	public void error() {
		ConsoleWriter writer = new ConsoleWriter(singletonMap("format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.ERROR).message("Hello World!").create());

		assertThat(systemStream.consumeErrorOutput()).contains("Hello World!" + NEW_LINE);
	}

}
