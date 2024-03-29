/*
 * Copyright 2021 Direnc Timur
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.core.TinylogLoggingProviderTest.LogEntryValues;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.CustomTestCharsetProvider;
import org.tinylog.util.FileSystem;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link JsonWriter}.
 */
@RunWith(Enclosed.class)
public final class JsonWriterTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Verifies that the required {@link LogEntryValues} are correctly evaluated.
	 *
	 * @throws IOException Failed writing to file
	 */
	@Test
	public void evaluatesRequiredLogValuesCorrectly() throws IOException {
		String file = FileSystem.createTemporaryFile();
		Map<String, String> properties = new HashMap<>();
		properties.put("file", file);
		properties.put("field.message", "message");
		properties.put("field.date", "date");
		properties.put("field.line", "line");

		Writer writer = new JsonWriter(properties);

		Collection<LogEntryValue> logValues = writer.getRequiredLogEntryValues();

		List<LogEntryValue> expectedValues = List.of(LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION, LogEntryValue.DATE,
				LogEntryValue.LINE);

		assertThat(logValues).containsAll(expectedValues);
	}

	/**
	 * Verifies that standard JSON output with JSON array for all log entries is used by default.
	 *
	 * @throws IOException Failed writing to file
	 */
	@Test
	public void standardJsonByDefault() throws IOException {
		String file = FileSystem.createTemporaryFile();
		Map<String, String> properties = new HashMap<>();
		properties.put("file", file);
		properties.put("field.level", "level");
		properties.put("field.message", "message");

		JsonWriter writer = new JsonWriter(properties);
		writer.close();

		String expectedFileContent = "[" + NEW_LINE + "]";
		String resultingEntry = FileSystem.readFile(file);

		assertThat(resultingEntry).isEqualTo(expectedFileContent);
	}

	/**
	 * Verifies that standard JSON will be used if an invalid JSON format is configured.
	 *
	 * @throws IOException Failed writing to file
	 */
	@Test
	public void fallbackToStandardJson() throws IOException {
		String file = FileSystem.createTemporaryFile();
		Map<String, String> properties = new HashMap<>();
		properties.put("file", file);
		properties.put("format", "FOO");
		properties.put("field.level", "level");
		properties.put("field.message", "message");

		JsonWriter writer = new JsonWriter(properties);
		writer.close();

		String expectedFileContent = "[" + NEW_LINE + "]";
		String resultingEntry = FileSystem.readFile(file);

		assertThat(resultingEntry).isEqualTo(expectedFileContent);
		assertThat(systemStream.consumeErrorOutput()).contains("WARN", "format", "FOO");
	}

	/**
	 * Tests for standard JSON output with JSON array for all log entries.
	 */
	@RunWith(Parameterized.class)
	public static class StandardJsonOutputTest {

		/**
		 * Redirects and collects system output streams.
		 */
		@Rule
		public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

		private final Charset charset;
		private final boolean writingThread;

		/**
		 * @param charset       The charset to use for JSON generation
		 * @param writingThread {@code true} to simulate an active writing thread, {@code false} to simulate the absence of
		 *                      a writing thread
		 */
		public StandardJsonOutputTest(final Charset charset, final boolean writingThread) {
			this.charset = charset;
			this.writingThread = writingThread;
		}

		/**
		 * Returns sample charsets and both writing thread states ({@code true} and {@code false}) that should be tested.
		 *
		 * @return Each object array contains a single charset and a single boolean
		 */
		@Parameterized.Parameters(name = "{0} ({1})")
		public static Collection<Object[]> getSizes() {
			List<Object[]> arguments = new ArrayList<>();
			arguments.add(new Object[] {StandardCharsets.US_ASCII, false});
			arguments.add(new Object[] {StandardCharsets.US_ASCII, true});
			arguments.add(new Object[] {StandardCharsets.ISO_8859_1, false});
			arguments.add(new Object[] {StandardCharsets.ISO_8859_1, true});
			arguments.add(new Object[] {StandardCharsets.UTF_8, false});
			arguments.add(new Object[] {StandardCharsets.UTF_8, true});
			arguments.add(new Object[] {StandardCharsets.UTF_16, false});
			arguments.add(new Object[] {StandardCharsets.UTF_16, true});
			arguments.add(new Object[] {StandardCharsets.UTF_16BE, false});
			arguments.add(new Object[] {StandardCharsets.UTF_16BE, true});
			arguments.add(new Object[] {StandardCharsets.UTF_16LE, false});
			arguments.add(new Object[] {StandardCharsets.UTF_16LE, true});
			return arguments;
		}

		/**
		 * Verifies that with no logging, an empty array is created.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void noWriting() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "JSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("buffered", "false");
			properties.put("append", "false");
			properties.put("charset", charset.name());

			JsonWriter writer = new JsonWriter(properties);
			writer.close();

			String expectedFileContent = "[" + NEW_LINE + "]";
			String resultingEntry = FileSystem.readFile(file, charset);

			assertThat(resultingEntry).isEqualTo(expectedFileContent);
		}

		/**
		 * Verifies that file is rewritten.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void unappendingWriting() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "JSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("buffered", "false");
			properties.put("append", "false");
			properties.put("charset", charset.name());
			properties.put("field.level", "level");
			properties.put("field.message", "message");

			JsonWriter writer;
			writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
			writer.write(givenLogEntry);
			writer.write(givenLogEntry);
			writer.close();

			writer = new JsonWriter(properties);
			writer.write(givenLogEntry);
			writer.write(givenLogEntry);
			writer.close();

			String expectedMessage = String.format("\"message\": \"%s\"", givenLogEntry.getMessage());
			String expectedLevel = String.format("\"level\": \"%s\"", givenLogEntry.getLevel());
			String resultingEntry = FileSystem.readFile(file, charset);

			int resultingMessageCount = resultingEntry.split(Pattern.quote(expectedMessage)).length - 1;
			int resultingLevelCount = resultingEntry.split(Pattern.quote(expectedLevel)).length - 1;
			assertThat(resultingMessageCount).isEqualTo(2);
			assertThat(resultingLevelCount).isEqualTo(2);
		}

		/**
		 * Verifies that file is appended with new entries.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void appendingWriting() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "JSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("buffered", "false");
			properties.put("append", "true");
			properties.put("charset", charset.name());
			properties.put("field.level", "level");
			properties.put("field.message", "message");

			JsonWriter writer;
			writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
			writer.write(givenLogEntry);
			writer.write(givenLogEntry);
			writer.close();

			writer = new JsonWriter(properties);
			writer.write(givenLogEntry);
			writer.write(givenLogEntry);
			writer.close();

			String expectedMessage = String.format("\"message\": \"%s\"", givenLogEntry.getMessage());
			String expectedLevel = String.format("\"level\": \"%s\"", givenLogEntry.getLevel());
			String resultingEntry = FileSystem.readFile(file, charset);

			int resultingMessageCount = resultingEntry.split(Pattern.quote(expectedMessage)).length - 1;
			int resultingLevelCount = resultingEntry.split(Pattern.quote(expectedLevel)).length - 1;
			assertThat(resultingMessageCount).isEqualTo(4);
			assertThat(resultingLevelCount).isEqualTo(4);
		}

		/**
		 * Verifies that JSON Array is correctly built.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void writesArrayCorrectly() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "JSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("buffered", "false");
			properties.put("append", "false");
			properties.put("charset", charset.name());

			JsonWriter writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
			writer.write(givenLogEntry);
			writer.close();

			String resultingEntry = FileSystem.readFile(file, charset);

			int indexOfOpeningArray = resultingEntry.indexOf("[");
			int indexOfOpeningJsonObject = resultingEntry.indexOf("{");
			int indexOfClosingJsonObject = resultingEntry.indexOf("}");
			int indexOfClosingArray = resultingEntry.indexOf("]");

			assertThat(indexOfOpeningJsonObject).isGreaterThan(indexOfOpeningArray).isLessThan(indexOfClosingJsonObject);
			assertThat(indexOfClosingJsonObject).isLessThan(indexOfClosingArray);
		}

		/**
		 * Verifies that JSON entries are correctly added and are separated by commas.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void addsJsonObjectCorrectly() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "JSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("buffered", "false");
			properties.put("append", "true");
			properties.put("charset", charset.name());

			JsonWriter writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
			writer.write(givenLogEntry);
			writer.write(givenLogEntry);
			writer.close();

			String resultingEntry = FileSystem.readFile(file, charset);

			int indexOfClosingFirstJsonObject = resultingEntry.indexOf("}");
			int indexOfOpeningSecondJsonObject = resultingEntry.indexOf("{", indexOfClosingFirstJsonObject);
			int indexOfComma = resultingEntry.indexOf(",", indexOfClosingFirstJsonObject);
			assertThat(indexOfComma).isLessThan(indexOfOpeningSecondJsonObject)
					.isGreaterThan(indexOfClosingFirstJsonObject);
		}

		/**
		 * Verifies that input fields match output properties in JSON.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void evaluatesFieldsCorrectly() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "JSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("charset", charset.name());
			properties.put("field.msg", "message");
			properties.put("field.lvl", "level");

			JsonWriter writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
			writer.write(givenLogEntry);
			writer.close();

			String expectedMessageField = String.format("\"msg\": \"%s\"", givenLogEntry.getMessage());
			String expectedLevelField = String.format("\"lvl\": \"%s\"", givenLogEntry.getLevel());
			String resultingEntry = FileSystem.readFile(file, charset);
			assertThat(resultingEntry).contains(expectedMessageField).contains(expectedLevelField);
		}

		@Test
		public void validJsonAfterFlushingOrClosing() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "JSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("charset", charset.name());
			properties.put("buffered", "true");
			properties.put("field.msg", "message");

			JsonWriter writer = new JsonWriter(properties);

			writer.write(LogEntryBuilder.empty().message("1").create());
			writer.write(LogEntryBuilder.empty().message("2").create());
			writer.flush();

			assertThat(FileSystem.readFile(file, charset))
					.isEqualToIgnoringWhitespace("[{\"msg\": \"1\"}, {\"msg\": \"2\"}]");

			writer.write(LogEntryBuilder.empty().message("3").create());
			writer.close();

			assertThat(FileSystem.readFile(file, charset))
					.isEqualToIgnoringWhitespace("[{\"msg\": \"1\"}, {\"msg\": \"2\"}, {\"msg\": \"3\"}]");
		}

		/**
		 * Verifies that special characters get escaped.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void escapesCharacters() throws IOException {
			String file = FileSystem.createTemporaryFile();
			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "JSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("charset", charset.name());
			properties.put("field.message", "message");

			JsonWriter writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class)
					.message("Hello World!" + NEW_LINE + "\t\t" + NEW_LINE + "\"\f\b").create();
			writer.write(givenLogEntry);
			writer.close();

			String expectedMessage = "Hello World!\\n\\t\\t\\n\\\"\\f\\b";
			String resultingEntry = FileSystem.readFile(file, charset);

			assertThat(resultingEntry).contains(expectedMessage);
		}

		/**
		 * Verifies that an exception is thrown when there is an invalid JSON file
		 * (currently only missing closing bracket).
		 *
		 * @throws IOException Failed creating the file
		 */
		@Test
		public void handlesInvalidJsonFile() throws IOException {
			String file = FileSystem.createTemporaryFile(charset, "[{}");
			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "JSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("append", "true");
			properties.put("charset", charset.name());
			properties.put("field.message", "message");

			assertThatCode(() -> new JsonWriter(properties))
					.isInstanceOf(IOException.class)
					.hasMessageContainingAll("JSON", "closing bracket");
		}

	}

	/**
	 * Tests for line-delimited JSON output aka LDJSON.
	 */
	@RunWith(Parameterized.class)
	public static class LineDelimitedJsonOutputTest {

		/**
		 * Redirects and collects system output streams.
		 */
		@Rule
		public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

		private final Charset charset;
		private final boolean writingThread;

		/**
		 * @param charset       The charset to use for JSON generation
		 * @param writingThread {@code true} to simulate an active writing thread, {@code false} to simulate the absence of
		 *                      a writing thread
		 */
		public LineDelimitedJsonOutputTest(final Charset charset, final boolean writingThread) {
			this.charset = charset;
			this.writingThread = writingThread;
		}

		/**
		 * Returns sample charsets and both writing thread states ({@code true} and {@code false}) that should be tested.
		 *
		 * @return Each object array contains a single charset and a single boolean
		 */
		@Parameterized.Parameters(name = "{0} ({1})")
		public static Collection<Object[]> getSizes() {
			List<Object[]> arguments = new ArrayList<>();
			arguments.add(new Object[] {StandardCharsets.US_ASCII, false});
			arguments.add(new Object[] {StandardCharsets.US_ASCII, true});
			arguments.add(new Object[] {StandardCharsets.ISO_8859_1, false});
			arguments.add(new Object[] {StandardCharsets.ISO_8859_1, true});
			arguments.add(new Object[] {StandardCharsets.UTF_8, false});
			arguments.add(new Object[] {StandardCharsets.UTF_8, true});
			arguments.add(new Object[] {StandardCharsets.UTF_16, false});
			arguments.add(new Object[] {StandardCharsets.UTF_16, true});
			arguments.add(new Object[] {StandardCharsets.UTF_16BE, false});
			arguments.add(new Object[] {StandardCharsets.UTF_16BE, true});
			arguments.add(new Object[] {StandardCharsets.UTF_16LE, false});
			arguments.add(new Object[] {StandardCharsets.UTF_16LE, true});
			return arguments;
		}

		/**
		 * Verifies that with no logging, an empty file is created.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void noWriting() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "LDJSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("buffered", "false");
			properties.put("append", "false");
			properties.put("charset", charset.name());

			JsonWriter writer = new JsonWriter(properties);
			writer.close();

			String resultingEntry = FileSystem.readFile(file, charset);
			assertThat(resultingEntry).isEmpty();
		}

		/**
		 * Verifies that file is rewritten.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void unappendingWriting() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "LDJSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("buffered", "false");
			properties.put("append", "false");
			properties.put("charset", charset.name());
			properties.put("field.level", "level");
			properties.put("field.message", "message");

			JsonWriter writer;
			writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
			writer.write(givenLogEntry);
			writer.write(givenLogEntry);
			writer.close();

			writer = new JsonWriter(properties);
			writer.write(givenLogEntry);
			writer.write(givenLogEntry);
			writer.close();

			String expectedMessage = String.format("\"message\": \"%s\"", givenLogEntry.getMessage());
			String expectedLevel = String.format("\"level\": \"%s\"", givenLogEntry.getLevel());
			String resultingEntry = FileSystem.readFile(file, charset);

			int resultingMessageCount = resultingEntry.split(Pattern.quote(expectedMessage)).length - 1;
			int resultingLevelCount = resultingEntry.split(Pattern.quote(expectedLevel)).length - 1;
			assertThat(resultingMessageCount).isEqualTo(2);
			assertThat(resultingLevelCount).isEqualTo(2);
		}

		/**
		 * Verifies that file is appended with new entries.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void appendingWriting() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "LDJSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("buffered", "false");
			properties.put("append", "true");
			properties.put("charset", charset.name());
			properties.put("field.level", "level");
			properties.put("field.message", "message");

			JsonWriter writer;
			writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
			writer.write(givenLogEntry);
			writer.write(givenLogEntry);
			writer.close();

			writer = new JsonWriter(properties);
			writer.write(givenLogEntry);
			writer.write(givenLogEntry);
			writer.close();

			String expectedMessage = String.format("\"message\": \"%s\"", givenLogEntry.getMessage());
			String expectedLevel = String.format("\"level\": \"%s\"", givenLogEntry.getLevel());
			String resultingEntry = FileSystem.readFile(file, charset);

			int resultingMessageCount = resultingEntry.split(Pattern.quote(expectedMessage)).length - 1;
			int resultingLevelCount = resultingEntry.split(Pattern.quote(expectedLevel)).length - 1;
			assertThat(resultingMessageCount).isEqualTo(4);
			assertThat(resultingLevelCount).isEqualTo(4);
		}

		/**
		 * Verifies that one line is used per log entry and no array brackets.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void writesLinesCorrectly() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "LDJSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("buffered", "false");
			properties.put("append", "false");
			properties.put("charset", charset.name());

			JsonWriter writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
			writer.write(givenLogEntry);
			writer.write(givenLogEntry);
			writer.close();

			String resultingEntry = FileSystem.readFile(file, charset);
			assertThat(resultingEntry).hasLineCount(2);
			assertThat(resultingEntry).doesNotContain("[", "]");
		}

		/**
		 * Verifies that input fields match output properties in JSON.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void evaluatesFieldsCorrectly() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "LDJSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("charset", charset.name());
			properties.put("field.msg", "message");
			properties.put("field.lvl", "level");

			JsonWriter writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
			writer.write(givenLogEntry);
			writer.close();

			String expectedMessageField = String.format("\"msg\": \"%s\"", givenLogEntry.getMessage());
			String expectedLevelField = String.format("\"lvl\": \"%s\"", givenLogEntry.getLevel());
			String resultingEntry = FileSystem.readFile(file, charset);
			assertThat(resultingEntry).contains(expectedMessageField).contains(expectedLevelField);
		}

		@Test
		public void validJsonAfterFlushingOrClosing() throws IOException {
			String file = FileSystem.createTemporaryFile();

			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "LDJSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("charset", charset.name());
			properties.put("buffered", "true");
			properties.put("field.msg", "message");

			JsonWriter writer = new JsonWriter(properties);

			writer.write(LogEntryBuilder.empty().message("1").create());
			writer.write(LogEntryBuilder.empty().message("2").create());
			writer.flush();

			assertThat(FileSystem.readFile(file, charset))
					.isEqualToIgnoringWhitespace("{\"msg\": \"1\"}\n{\"msg\": \"2\"}");

			writer.write(LogEntryBuilder.empty().message("3").create());
			writer.close();

			assertThat(FileSystem.readFile(file, charset))
					.isEqualToIgnoringWhitespace("{\"msg\": \"1\"}\n{\"msg\": \"2\"}\n{\"msg\": \"3\"}");
		}

		/**
		 * Verifies that special characters get escaped.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void escapesCharacters() throws IOException {
			String file = FileSystem.createTemporaryFile();
			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "LDJSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("charset", charset.name());
			properties.put("field.message", "message");

			JsonWriter writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class)
					.message("Hello World!" + NEW_LINE + "\t\t" + NEW_LINE + "\"\f\b").create();
			writer.write(givenLogEntry);
			writer.close();

			String expectedMessage = "Hello World!\\n\\t\\t\\n\\\"\\f\\b";
			String resultingEntry = FileSystem.readFile(file, charset);

			assertThat(resultingEntry).contains(expectedMessage);
		}

		@Test
		public void handlesInvalidJsonFile() throws IOException {
			String file = FileSystem.createTemporaryFile(charset, "[{}");
			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("format", "LDJSON");
			properties.put("writingthread", Boolean.toString(writingThread));
			properties.put("append", "true");
			properties.put("charset", charset.name());
			properties.put("field.message", "message");

			JsonWriter writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.empty().message("Hello World!").create();
			writer.write(givenLogEntry);
			writer.close();

			assertThat(FileSystem.readFile(file, charset))
					.isEqualToIgnoringWhitespace("[{}\n{\"message\": \"Hello World!\"}");
		}

	}

	/**
	 * Tests for invalid configuration properties.
	 */
	public static class InvalidConfigurationPropertiesTest {

		/**
		 * Redirects and collects system output streams.
		 */
		@Rule
		public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

		/**
		 * Verifies that exception is thrown when there is no file name.
		 *
		 */
		@Test
		public void expectsFilename() {
			assertThatCode(() -> new JsonWriter(Collections.emptyMap()))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContainingAll("File name", "missing");
		}

		/**
		 * Verifies that an invalid charset is handled with the default charset.
		 *
		 * @throws IOException Failed writing to file
		 */
		@Test
		public void handlesInvalidCharset() throws IOException {
			String file = FileSystem.createTemporaryFile();
			Map<String, String> properties = new HashMap<>();
			properties.put("file", file);
			properties.put("charset", "asdf");
			properties.put("field.message", "message");

			JsonWriter writer = new JsonWriter(properties);
			LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
			writer.write(givenLogEntry);
			writer.close();

			String resultingEntry = FileSystem.readFile(file, Charset.defaultCharset());
			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("charset")
					.containsOnlyOnce("asdf");
			assertThat(resultingEntry).contains(givenLogEntry.getMessage());
		}

	}

	/**
	 * Tests for illegal charset validation.
	 */
	@RunWith(Parameterized.class)
	public static class IllegalCharsetTest {

		/**
		 * Redirects and collects system output streams.
		 */
		@Rule
		public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

		private final Charset charset;

		/**
		 * @param charset The illegal charset to validate
		 */
		public IllegalCharsetTest(final Charset charset) {
			this.charset = charset;
		}

		/**
		 * Returns illegal charsets that should be tested.
		 *
		 * @return Each object array contains a single charset
		 */
		@Parameterized.Parameters(name = "{0}")
		public static Collection<Object[]> getSizes() {
			return CustomTestCharsetProvider.CHARSETS.stream()
				.map(charset -> new Object[] {charset})
				.collect(Collectors.toList());
		}

		@Test
		public void validateCharset() throws IOException {
			Map<String, String> properties = new HashMap<>();
			properties.put("file", FileSystem.createTemporaryFile());
			properties.put("charset", charset.name());

			assertThatCode(() -> new JsonWriter(properties))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(charset.name());
		}

	}

}
