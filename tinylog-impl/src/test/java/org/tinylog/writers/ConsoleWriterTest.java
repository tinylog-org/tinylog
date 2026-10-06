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

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.Level;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.core.LogEntryValue;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.LogEntryBuilder;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.tinylog.util.Maps.doubletonMap;

/**
 * Tests for {@link ConsoleWriter}.
 */
public final class ConsoleWriterTest {

	private static final String NEW_LINE = System.lineSeparator();

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
	 * Verifies that the default pattern contains a minimum set of information.
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
		writer.write(LogEntryBuilder.empty().level(Level.WARN).message("Hello World!").create());

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

	/**
	 * Verifies that log entries will be written to standard output stream, if property "stream" is set to "out".
	 */
	@Test
	public void standardOutputStream() {
		ConsoleWriter writer = new ConsoleWriter(doubletonMap("stream", "out", "format", "{message}"));

		writer.write(LogEntryBuilder.empty().level(Level.TRACE).message("Hello World!").create());
		assertThat(systemStream.consumeStandardOutput()).contains("Hello World!" + NEW_LINE);

		writer.write(LogEntryBuilder.empty().level(Level.ERROR).message("Hello World!").create());
		assertThat(systemStream.consumeStandardOutput()).contains("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that log entries will be written to error output stream, if property "stream" is set to "err".
	 */
	@Test
	public void errorOutputStream() {
		ConsoleWriter writer = new ConsoleWriter(doubletonMap("stream", "err", "format", "{message}"));

		writer.write(LogEntryBuilder.empty().level(Level.TRACE).message("Hello World!").create());
		assertThat(systemStream.consumeErrorOutput()).contains("Hello World!" + NEW_LINE);

		writer.write(LogEntryBuilder.empty().level(Level.ERROR).message("Hello World!").create());
		assertThat(systemStream.consumeErrorOutput()).contains("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that an error message will be output for an invalid stream name. Nevertheless the console writer should
	 * work normally.
	 */
	@Test
	public void invalidOutputStream() {
		ConsoleWriter writer = new ConsoleWriter(doubletonMap("stream", "test", "format", "{message}"));
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("test");

		writer.write(LogEntryBuilder.empty().level(Level.TRACE).message("Hello World!").create());
		assertThat(systemStream.consumeStandardOutput()).contains("Hello World!" + NEW_LINE);

		writer.write(LogEntryBuilder.empty().level(Level.ERROR).message("Hello World!").create());
		assertThat(systemStream.consumeErrorOutput()).contains("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that writer is registered as service under the name "console".
	 */
	@Test
	public void isRegistered() {
		Writer writer = new ServiceLoader<>(Writer.class, Map.class).create("console", emptyMap());
		assertThat(writer).isInstanceOf(ConsoleWriter.class);
	}
	
	/**
	 * Verifies that log entries will be written to the correct output stream, if property "stream" is set with level.
	 */
	@Test
	public void errorOutputStreamWithLevelInfo() {
		ConsoleWriter writer = new ConsoleWriter(doubletonMap("stream", "err@INFO", "format", "{message}"));

		writer.write(LogEntryBuilder.empty().level(Level.TRACE).message("Hello World!").create());
		assertThat(systemStream.consumeStandardOutput()).contains("Hello World!" + NEW_LINE);

		writer.write(LogEntryBuilder.empty().level(Level.INFO).message("Hello World!").create());
		assertThat(systemStream.consumeErrorOutput()).contains("Hello World!" + NEW_LINE);

		writer.write(LogEntryBuilder.empty().level(Level.ERROR).message("Hello World!").create());
		assertThat(systemStream.consumeErrorOutput()).contains("Hello World!" + NEW_LINE);
	}
	
	/**
	 * Verifies that log entries will be written to the correct output stream, if property "stream" is set with level.
	 */
	@Test
	public void errorOutputStreamWithLevelError() {
		ConsoleWriter writer = new ConsoleWriter(doubletonMap("stream", "err@ERROR", "format", "{message}"));

		writer.write(LogEntryBuilder.empty().level(Level.TRACE).message("Hello World!").create());
		assertThat(systemStream.consumeStandardOutput()).contains("Hello World!" + NEW_LINE);

		writer.write(LogEntryBuilder.empty().level(Level.WARN).message("Hello World!").create());
		assertThat(systemStream.consumeStandardOutput()).contains("Hello World!" + NEW_LINE);

		writer.write(LogEntryBuilder.empty().level(Level.ERROR).message("Hello World!").create());
		assertThat(systemStream.consumeErrorOutput()).contains("Hello World!" + NEW_LINE);
	}	
	
	/**
	 * Verifies the property "stream" with level raises an error log if wrongly configured.
	 */
	@Test
	public void errorOutputStreamWithLevelMisconfiguration() {
		ConsoleWriter writer = new ConsoleWriter(doubletonMap("stream", "dummy@INFO", "format", "{message}"));
		assertThat(systemStream.consumeErrorOutput()).contains("Stream with level must be \"err\", \"");
		
		writer.write(LogEntryBuilder.empty().level(Level.INFO).message("Hello World!").create());
		assertThat(systemStream.consumeErrorOutput()).contains("Hello World!" + NEW_LINE);
	}
	
	/**
	 * Verifies that log entries are written in chronological order, even if standard output stream is buffered.
	 */
	@Test
	public void chronologicalOrderWithBufferedStandardOutputStream() throws UnsupportedEncodingException {
		List<String> writeOrder = new ArrayList<String>();
		PrintStream originalStandardStream = System.out;
		PrintStream originalErrorStream = System.err;

		try {
			OutputStream outSink = new OutputStream() {
				@Override
				public void write(final int b) {
					write(new byte[] { (byte) b }, 0, 1);
				}

				@Override
				public void write(final byte[] buffer, final int offset, final int length) {
					writeOrder.add("out:" + new String(buffer, offset, length, StandardCharsets.UTF_8));
				}
			};
			OutputStream errSink = new OutputStream() {
				@Override
				public void write(final int b) {
					write(new byte[] { (byte) b }, 0, 1);
				}

				@Override
				public void write(final byte[] buffer, final int offset, final int length) {
					writeOrder.add("err:" + new String(buffer, offset, length, StandardCharsets.UTF_8));
				}
			};
			System.setOut(new PrintStream(new BufferedOutputStream(outSink), false, "UTF-8"));
			System.setErr(new PrintStream(errSink, true, "UTF-8"));

			ConsoleWriter writer = new ConsoleWriter(singletonMap("format", "{message}"));
			writer.write(LogEntryBuilder.empty().level(Level.TRACE).message("1 Test Trace").create());
			writer.write(LogEntryBuilder.empty().level(Level.DEBUG).message("2 Test Debug").create());
			writer.write(LogEntryBuilder.empty().level(Level.INFO).message("3 Test Info").create());
			writer.write(LogEntryBuilder.empty().level(Level.WARN).message("4 Test Warn").create());
			writer.write(LogEntryBuilder.empty().level(Level.ERROR).message("5 Test Error").create());
			writer.write(LogEntryBuilder.empty().level(Level.WARN).message("6 Test Warn").create());
			writer.write(LogEntryBuilder.empty().level(Level.INFO).message("7 Test Info").create());
			writer.write(LogEntryBuilder.empty().level(Level.DEBUG).message("8 Test Debug").create());
			writer.write(LogEntryBuilder.empty().level(Level.TRACE).message("9 Test Trace").create());

			assertThat(writeOrder).containsExactly(
				"out:1 Test Trace" + NEW_LINE,
				"out:2 Test Debug" + NEW_LINE,
				"out:3 Test Info" + NEW_LINE,
				"err:4 Test Warn" + NEW_LINE,
				"err:5 Test Error" + NEW_LINE,
				"err:6 Test Warn" + NEW_LINE,
				"out:7 Test Info" + NEW_LINE,
				"out:8 Test Debug" + NEW_LINE,
				"out:9 Test Trace" + NEW_LINE
			);
		} finally {
			System.setOut(originalStandardStream);
			System.setErr(originalErrorStream);
		}
	}

	/**
	 * Verifies that an empty logger works correctly.
	 */
	@Test
	public void errorOutputStreamForEmptyLogger() {
		ConsoleWriter writer = new ConsoleWriter();
		writer.flush(); // Flushes buffered console streams

		writer.write(LogEntryBuilder.empty().level(Level.TRACE).message("Hello World!").date(LocalDate.now()).create());
		assertThat(systemStream.consumeStandardOutput()).contains("Hello World!" + NEW_LINE);
		
		writer.write(LogEntryBuilder.empty().level(Level.ERROR).message("Hello World!").date(LocalDate.now()).create());
		assertThat(systemStream.consumeErrorOutput()).contains("Hello World!" + NEW_LINE);
	}
}
