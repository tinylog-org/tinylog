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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public final class JsonWriterTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

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
		properties.put("buffered", "false");
		properties.put("append", "false");

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

		LogEntry expectedLogEntry = givenLogEntry;
		String expectedMessage = String.format("\"message\": \"%s\"", expectedLogEntry.getMessage());
		String expectedLevel = String.format("\"level\": \"%s\"", expectedLogEntry.getLevel());
		String resultingEntry = FileSystem.readFile(file);

		int resultingMessagCount = resultingEntry.split(Pattern.quote(expectedMessage)).length - 1;
		int resultingLevelCount = resultingEntry.split(Pattern.quote(expectedLevel)).length - 1;
		assertThat(resultingMessagCount).isEqualTo(2);
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
		properties.put("buffered", "false");
		properties.put("append", "true");

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

		LogEntry expectedLogEntry = givenLogEntry;
		String expectedMessage = String.format("\"message\": \"%s\"", expectedLogEntry.getMessage());
		String expectedLevel = String.format("\"level\": \"%s\"", expectedLogEntry.getLevel());
		String resultingEntry = FileSystem.readFile(file);

		int resultingMessagCount = resultingEntry.split(Pattern.quote(expectedMessage)).length - 1;
		int resultingLevelCount = resultingEntry.split(Pattern.quote(expectedLevel)).length - 1;
		assertThat(resultingMessagCount).isEqualTo(4);
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
		properties.put("buffered", "false");
		properties.put("append", "false");

		JsonWriter writer;
		writer = new JsonWriter(properties);
		LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
		writer.write(givenLogEntry);
		writer.close();

		String resultingEntry = FileSystem.readFile(file);

		int indexOfOpeningArray = resultingEntry.indexOf("[");
		int indexOfOpeningJsonObject = resultingEntry.indexOf("{");
		int indexOfClosingJsonObject = resultingEntry.indexOf("}");
		int indexOfClosingArray = resultingEntry.indexOf("]");

		assertThat(indexOfOpeningJsonObject).isGreaterThan(indexOfOpeningArray).isLessThan(indexOfClosingJsonObject);
		assertThat(indexOfClosingJsonObject).isLessThan(indexOfClosingArray);
	}

	/**
	 * Verifies that JSON entries are correctly added and are seperated by commas.
	 * 
	 * @throws IOException Failed writing to file
	 */
	@Test
	public void addsJsonObjectCorrectly() throws IOException {
		String file = FileSystem.createTemporaryFile();

		Map<String, String> properties = new HashMap<>();
		properties.put("file", file);
		properties.put("buffered", "false");
		properties.put("append", "true");

		JsonWriter writer;
		writer = new JsonWriter(properties);
		LogEntry givenLogEntry = LogEntryBuilder.prefilled(JsonWriterTest.class).create();
		writer.write(givenLogEntry);
		writer.write(givenLogEntry);
		writer.close();

		String resultingEntry = FileSystem.readFile(file);

		int indexOfClosingFirstJsonObject = resultingEntry.indexOf("}");
		int indexOfOpeningSecondJsonObject = resultingEntry.indexOf("{", indexOfClosingFirstJsonObject);
		int indexOfComma = resultingEntry.indexOf(",", indexOfClosingFirstJsonObject);
		assertThat(indexOfComma).isLessThan(indexOfOpeningSecondJsonObject)
				.isGreaterThan(indexOfClosingFirstJsonObject);
	}
}
