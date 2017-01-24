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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.core.LogEntryValue;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;
import org.tinylog.util.LogEntryBuilder;
import org.tinylog.writers.raw.ByteArrayWriter;
import org.tinylog.writers.raw.SynchronizedWriterDecorator;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.tinylog.util.Maps.doubletonMap;
import static org.tinylog.util.Maps.tripletonMap;

/**
 * Tests for {@link FileWriter}.
 */
public final class FileWriterTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Verifies that log entries will be immediately output, if buffer is disabled.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void unbufferedWriting() throws IOException {
		String file = FileSystem.createTemporaryFile();
		FileWriter writer = new FileWriter(tripletonMap("file", file, "format", "{message}", "buffered", "false"));

		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		assertThat(FileSystem.readFile(file)).isEqualTo("Hello World!" + NEW_LINE);

		writer.close();
		assertThat(FileSystem.readFile(file)).isEqualTo("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that log entries will be output after flushing, if buffer is enabled.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void bufferedWriting() throws IOException {
		String file = FileSystem.createTemporaryFile();
		FileWriter writer = new FileWriter(tripletonMap("file", file, "format", "{message}", "buffered", "true"));

		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		assertThat(FileSystem.readFile(file)).isEmpty();

		writer.flush();
		assertThat(FileSystem.readFile(file)).isEqualTo("Hello World!" + NEW_LINE);

		writer.close();
		assertThat(FileSystem.readFile(file)).isEqualTo("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that an already existing file will be overridden, if append mode is disabled.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void appendingDisabled() throws IOException {
		String file = FileSystem.createTemporaryFile("Test");

		FileWriter writer = new FileWriter(tripletonMap("file", file, "format", "{message}", "append", "false"));
		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		writer.close();

		assertThat(FileSystem.readFile(file)).isEqualTo("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that an already existing file will be continued, if append mode is enabled.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void appendingEnabled() throws IOException {
		String file = FileSystem.createTemporaryFile("Test");

		FileWriter writer = new FileWriter(tripletonMap("file", file, "format", "{message}", "append", "true"));
		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		writer.close();

		assertThat(FileSystem.readFile(file)).isEqualTo("Test" + NEW_LINE + "Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that writing works and underlying byte array writer is thread-safe, if writing thread is disable.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void writingThreadDisabled() throws IOException {
		String file = FileSystem.createTemporaryFile();
		FileWriter writer = new FileWriter(tripletonMap("file", file, "format", "{message}", "writingthread", "false"));

		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		assertThat(FileSystem.readFile(file)).isEqualTo("Hello World!" + NEW_LINE);
		writer.close();

		assertThat(Whitebox.getInternalState(writer, ByteArrayWriter.class)).isInstanceOf(SynchronizedWriterDecorator.class);
	}

	/**
	 * Verifies that writing works and underlying byte array writer is not thread-safe, if writing thread is enabled.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void writingThreadEnabled() throws IOException {
		String file = FileSystem.createTemporaryFile();
		FileWriter writer = new FileWriter(tripletonMap("file", file, "format", "{message}", "writingthread", "true"));

		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		assertThat(FileSystem.readFile(file)).isEqualTo("Hello World!" + NEW_LINE);
		writer.close();

		assertThat(Whitebox.getInternalState(writer, ByteArrayWriter.class)).isNotInstanceOf(SynchronizedWriterDecorator.class);
	}

	/**
	 * Verifies that a configured charset will be used for encoding text.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void definedCharset() throws IOException {
		String file = FileSystem.createTemporaryFile();

		FileWriter writer = new FileWriter(tripletonMap("file", file, "format", "{message}", "charset", "UTF-16"));
		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		writer.close();

		assertThat(FileSystem.readFile(file, StandardCharsets.UTF_16)).isEqualTo("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that the default pattern contains a minimum set of informations.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void defaultFormatPattern() throws IOException {
		String file = FileSystem.createTemporaryFile();
		FileWriter writer = new FileWriter(singletonMap("file", file));

		assertThat(writer.getRequiredLogEntryValues())
			.contains(LogEntryValue.DATE, LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);

		writer.write(LogEntryBuilder.prefilled(FileWriterTest.class).create());
		writer.close();

		assertThat(FileSystem.readFile(file))
			.contains("1985").contains("03")
			.contains("TRACE")
			.contains("Hello World!")
			.endsWith(NEW_LINE);
	}

	/**
	 * Verifies that an exception will be thrown, if no file name is defined. The message of the thrown exception should
	 * contain "file name" or "filename".
	 */
	@Test
	public void missingFileName() {
		assertThatThrownBy(() -> new FileWriter(emptyMap())).hasMessageMatching("(?i).*file ?name.*");
	}

	/**
	 * Verifies that an invalid charset will be reported as error.
	 *
	 * @throws IOException
	 *             Failed opening file
	 */
	@Test
	public void invalidCharset() throws IOException {
		String file = FileSystem.createTemporaryFile();
		new FileWriter(doubletonMap("file", file, "charset", "UTF-42")).close();

		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("charset").containsOnlyOnce("UTF-42");
	}

}
