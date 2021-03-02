package org.tinylog.impl.writer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.placeholder.MessagePlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class FileWriterTest {

	private Path logFile;

	/**
	 * Creates a temporary log file.
	 *
	 * @throws IOException Failed to create a temporary log file
	 */
	@BeforeEach
	void init() throws IOException {
		logFile = Files.createTempFile("tinylog", ".log");
		logFile.toFile().deleteOnExit();
	}

	/**
	 * Deletes the created temporary log file.
	 *
	 * @throws IOException Failed to delete the temporary log file
	 */
	@AfterEach
	void release() throws IOException {
		Files.deleteIfExists(logFile);
	}

	/**
	 * Verifies that the file writer requires only log entry values from the passed placeholder.
	 */
	@Test
	void requiredLogEntryValues() throws IOException {
		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), logFile, StandardCharsets.UTF_8)) {
			assertThat(writer.getRequiredLogEntryValues())
				.containsExactlyInAnyOrder(LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
		}
	}

	/**
	 * Verifies that an already existing file is continued and not overwritten.
	 */
	@Test
	void appendExistingFile() throws IOException {
		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), logFile, StandardCharsets.UTF_8)) {
			LogEntry entry = new LogEntryBuilder().message("Hello World!").create();
			writer.log(entry);
		}

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), logFile, StandardCharsets.UTF_8)) {
			LogEntry entry = new LogEntryBuilder().message("Goodbye.").create();
			writer.log(entry);
		}

		assertThat(logFile).hasContent("Hello World!Goodbye.");
	}

	/**
	 * Verifies that a single short string (smaller than the used buffer) is written to the log file.
	 */
	@Test
	void writeSingleShortMessage() throws IOException {
		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), logFile, StandardCharsets.UTF_8)) {
			LogEntry entry = new LogEntryBuilder().message("Hello World!").create();
			writer.log(entry);
		}

		assertThat(logFile).hasContent("Hello World!");
	}

	/**
	 * Verifies that multiple short strings (smaller than the used buffer) are written to the log file.
	 */
	@Test
	void writeMultipleShortMessage() throws IOException {
		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), logFile, StandardCharsets.UTF_8)) {
			LogEntry entry = new LogEntryBuilder().message("Hello World!").create();
			writer.log(entry);

			entry = new LogEntryBuilder().message("Goodbye.").create();
			writer.log(entry);
		}

		assertThat(logFile).hasContent("Hello World!Goodbye.");
	}

	/**
	 * Verifies that a long string (larger than the used buffer) is written to the log file.
	 */
	@Test
	void writeSingleLongMessage() throws IOException {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 256 * 1024; ++i) {
			builder.append(i % 10);
		}

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), logFile, StandardCharsets.UTF_8)) {
			LogEntry entry = new LogEntryBuilder().message(builder.toString()).create();
			writer.log(entry);
		}

		assertThat(logFile).hasContent(builder.toString());
	}

	/**
	 * Verifies that multiple long string (larger than the used buffer) are written to the log file.
	 */
	@Test
	void writeMultipleLongMessage() throws IOException {
		String first = "<";

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 128 * 1024; ++i) {
			builder.append(i % 10);
		}
		String second = builder.toString();

		builder = new StringBuilder();
		for (int i = 0; i < 128 * 1024; ++i) {
			builder.append(('a' + i) % 26);
		}
		String third = builder.toString();

		String fourth = ">";

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), logFile, StandardCharsets.UTF_8)) {
			LogEntry entry = new LogEntryBuilder().message(first).create();
			writer.log(entry);

			entry = new LogEntryBuilder().message(second).create();
			writer.log(entry);

			entry = new LogEntryBuilder().message(third).create();
			writer.log(entry);

			entry = new LogEntryBuilder().message(fourth).create();
			writer.log(entry);
		}

		assertThat(logFile).hasContent(first + second + third + fourth);
	}

	/**
	 * Verifies that a character that is not supported by the passed charset is replaced by a question mark ("?").
	 */
	@Test
	void writeUnsupportedCharacter() throws IOException {
		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), logFile, StandardCharsets.US_ASCII)) {
			LogEntry entry = new LogEntryBuilder().message("<ä>").create();
			writer.log(entry);
		}

		assertThat(logFile).hasContent("<?>");
	}

}
