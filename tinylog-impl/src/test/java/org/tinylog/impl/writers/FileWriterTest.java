package org.tinylog.impl.writers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.placeholders.MessagePlaceholder;
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
	 * Verifies that multiple log entries can be written to the same log file.
	 */
	@Test
	void writeMultipleMessages() throws IOException {
		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), logFile, StandardCharsets.UTF_8)) {
			LogEntry entry = new LogEntryBuilder().message("Hello World!").create();
			writer.log(entry);

			entry = new LogEntryBuilder().message("Goodbye.").create();
			writer.log(entry);
		}

		assertThat(logFile).hasContent("Hello World!Goodbye.");
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
