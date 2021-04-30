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

package org.tinylog.writers;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;
import org.tinylog.util.JvmProcessBuilder;
import org.tinylog.util.LogEntryBuilder;
import org.tinylog.writers.raw.ByteArrayWriter;
import org.tinylog.writers.raw.SynchronizedWriterDecorator;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.tinylog.util.Maps.doubletonMap;
import static org.tinylog.util.Maps.tripletonMap;

/**
 * Tests for {@link SharedFileWriter}.
 */
@RunWith(PowerMockRunner.class)
public final class SharedFileWriterTest {

	private static final int NUMBER_OF_PROCESSES = 5;
	private static final int NUMBER_OF_LINES = 10_000;

	private static final String LOG_ENTRY_MESSAGE = "LOG ENTRY";
	private static final String NEW_LINE = System.lineSeparator();

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
		SharedFileWriter writer = new SharedFileWriter(tripletonMap("file", file, "format", "{message}", "buffered", "false"));

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
		SharedFileWriter writer = new SharedFileWriter(tripletonMap("file", file, "format", "{message}", "buffered", "true"));

		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		assertThat(FileSystem.readFile(file)).isEmpty();

		writer.flush();
		assertThat(FileSystem.readFile(file)).isEqualTo("Hello World!" + NEW_LINE);

		writer.close();
		assertThat(FileSystem.readFile(file)).isEqualTo("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that an already existing file will be overridden by a single process, if append mode is disabled.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void appendingDisabledForSingleProcess() throws IOException {
		String file = FileSystem.createTemporaryFile("Test");

		SharedFileWriter writer = new SharedFileWriter(tripletonMap("file", file, "format", "{message}", "append", "false"));
		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		writer.close();

		assertThat(FileSystem.readFile(file)).isEqualTo("Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that multiple processes override an already existing file but don't override each other, if append mode
	 * is disabled.
	 *
	 * @throws IOException
	 *             Failed writing to file or creating process
	 * @throws InterruptedException
	 *             Interrupted while waiting for process
	 */
	@Test
	public void appendingDisabledForMultipleProcesses() throws IOException, InterruptedException {
		File file = new File(FileSystem.createTemporaryFile());
		String path = file.getAbsolutePath();

		if (!file.delete()) {
			throw new IOException("Failed to delete temporary file: " + path);
		}

		List<Process> processes = new JvmProcessBuilder(SharedFileWriterTest.class, path, "false").start(NUMBER_OF_PROCESSES);

		Files.write(file.toPath(), Arrays.asList("PREAMBLE"));

		for (Process process : processes) {
			process.waitFor();
		}

		assertThat(FileSystem.readFile(path))
			.hasLineCount(NUMBER_OF_PROCESSES * NUMBER_OF_LINES)
			.matches("(" + Pattern.quote(LOG_ENTRY_MESSAGE + NEW_LINE) + "){" + (NUMBER_OF_PROCESSES * NUMBER_OF_LINES) + "}");
	}

	/**
	 * Verifies that an already existing file will be continued by a single process, if append mode is enabled.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void appendingEnabledForSingleProcess() throws IOException {
		String file = FileSystem.createTemporaryFile("Test");

		SharedFileWriter writer = new SharedFileWriter(tripletonMap("file", file, "format", "{message}", "append", "true"));
		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		writer.close();

		assertThat(FileSystem.readFile(file)).isEqualTo("Test" + NEW_LINE + "Hello World!" + NEW_LINE);
	}

	/**
	 * Verifies that multiple processes neither override an already existing file nor each other, if append mode is
	 * enabled.
	 *
	 * @throws IOException
	 *             Failed writing to file or creating process
	 * @throws InterruptedException
	 *             Interrupted while waiting for process
	 */
	@Test
	public void appendingEnabledForMultipleProcesses() throws IOException, InterruptedException {
		File file = new File(FileSystem.createTemporaryFile());
		String path = file.getAbsolutePath();

		if (!file.delete()) {
			throw new IOException("Failed to delete temporary file: " + path);
		}

		List<Process> processes = new JvmProcessBuilder(SharedFileWriterTest.class, path, "true").start(NUMBER_OF_PROCESSES);

		Files.write(file.toPath(), Arrays.asList("PREAMBLE"));

		for (Process process : processes) {
			process.waitFor();
		}

		int entries = NUMBER_OF_PROCESSES * NUMBER_OF_LINES;
		assertThat(FileSystem.readFile(path))
			.hasLineCount(entries + 1)
			.matches(Pattern.quote("PREAMBLE" + NEW_LINE) + "(" + Pattern.quote(LOG_ENTRY_MESSAGE + NEW_LINE) + "){" + entries + "}");
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
		SharedFileWriter writer = new SharedFileWriter(tripletonMap("file", file, "format", "{message}", "writingthread", "false"));

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
		SharedFileWriter writer = new SharedFileWriter(tripletonMap("file", file, "format", "{message}", "writingthread", "true"));

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

		SharedFileWriter writer = new SharedFileWriter(tripletonMap("file", file, "format", "{message}", "charset", "UTF-16"));
		writer.write(LogEntryBuilder.empty().message("Hello World!").create());
		writer.write(LogEntryBuilder.empty().message("Goodbye!").create());
		writer.close();

		assertThat(FileSystem.readFile(file, StandardCharsets.UTF_16))
			.isEqualTo("Hello World!" + NEW_LINE + "Goodbye!" + NEW_LINE);
	}

	/**
	 * Verifies that the default pattern contains a minimum set of information.
	 *
	 * @throws IOException
	 *             Failed writing to file
	 */
	@Test
	public void defaultFormatPattern() throws IOException {
		String file = FileSystem.createTemporaryFile();
		SharedFileWriter writer = new SharedFileWriter(singletonMap("file", file));

		assertThat(writer.getRequiredLogEntryValues())
			.contains(LogEntryValue.DATE, LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);

		writer.write(LogEntryBuilder.prefilled(SharedFileWriterTest.class).create());
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
		assertThatThrownBy(() -> new SharedFileWriter(emptyMap())).hasMessageMatching("(?i).*file ?name.*");
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
		new SharedFileWriter(doubletonMap("file", file, "charset", "UTF-42")).close();

		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("charset").containsOnlyOnce("UTF-42");
	}

	/**
	 * Verifies that a warning will be output, if the operating system doesn't support shared locks.
	 *
	 * @throws IOException
	 *             Failed accessing target or lock file
	 * @throws Exception
	 *             Required for {@link PowerMockito#whenNew(Class)}
	 */
	@Test
	@PrepareForTest(SharedFileWriter.class)
	public void unsupportedSharedLocks() throws IOException, Exception {
		FileLock mockedLock = mock(FileLock.class);
		when(mockedLock.isShared()).thenReturn(false);

		FileChannel mockedChannel = mock(FileChannel.class);
		when(mockedChannel.lock(anyLong(), anyLong(), eq(true))).thenReturn(mockedLock);

		RandomAccessFile mockedFile = mock(RandomAccessFile.class);
		when(mockedFile.getChannel()).thenReturn(mockedChannel);
		whenNew(RandomAccessFile.class).withAnyArguments().thenReturn(mockedFile);

		String file = FileSystem.createTemporaryFile();
		new SharedFileWriter(doubletonMap("file", file, "append", "false")).close();

		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARN").contains("shared");
	}

	/**
	 * Verifies that writer is registered as service under the name "file".
	 *
	 * @throws IOException
	 *             Failed creating temporary file
	 */
	@Test
	public void isRegistered() throws IOException {
		String file = FileSystem.createTemporaryFile();
		Writer writer = new ServiceLoader<>(Writer.class, Map.class).create("shared file", singletonMap("file", file));
		assertThat(writer).isInstanceOf(SharedFileWriter.class);
	}

	/**
	 * Writes a defined number of lines to a given target file. This main method is used to test writing simultaneously
	 * to the same file by multiple processes.
	 *
	 * @param arguments
	 *            First element will be used as file name for target file and second element for defining append mode
	 * @throws IOException
	 *             Failed writing to target file
	 */
	public static void main(final String[] arguments) throws IOException {
		File file = new File(arguments[0]);
		while (!file.exists()) {
			Thread.yield();
		}

		Map<String, String> properties = tripletonMap("file", arguments[0], "append", arguments[1], "format", "{message}");
		SharedFileWriter writer = new SharedFileWriter(properties);
		LogEntry logEntry = LogEntryBuilder.prefilled(SharedFileWriterTest.class).message(LOG_ENTRY_MESSAGE).create();

		for (int i = 0; i < NUMBER_OF_LINES; ++i) {
			writer.write(logEntry);
		}

		writer.close();
	}

}
