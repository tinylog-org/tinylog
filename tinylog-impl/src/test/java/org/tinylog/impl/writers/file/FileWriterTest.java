package org.tinylog.impl.writers.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.placeholders.MessagePlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class FileWriterTest {

	private Path file;

	/**
	 * Creates a temporary log file.
	 *
	 * @throws IOException Failed to create a temporary log file
	 */
	@BeforeEach
	void init() throws IOException {
		file = Files.createTempFile("tinylog", ".log");
		file.toFile().deleteOnExit();
	}

	/**
	 * Deletes the created temporary log file.
	 *
	 * @throws IOException Failed to delete the temporary log file
	 */
	@AfterEach
	void release() throws IOException {
		Files.deleteIfExists(file);
	}

	/**
	 * Verifies that the file writer requires only log entry values from the passed placeholder.
	 */
	@Test
	void requiredLogEntryValues() throws IOException {
		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), file, UTF_8)) {
			assertThat(writer.getRequiredLogEntryValues())
				.containsExactlyInAnyOrder(LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
		}
	}

	/**
	 * Verifies that multiple log entries can be written to the same log file.
	 *
	 * @param charset The charset to use for writing string
	 */
	@ParameterizedTest
	@ArgumentsSource(CharsetsProvider.class)
	void writeMultipleMessages(Charset charset) throws IOException {
		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), file, charset)) {
			LogEntry entry = new LogEntryBuilder().message("Hello World!").create();
			writer.log(entry);

			entry = new LogEntryBuilder().message("Goodbye.").create();
			writer.log(entry);
		}

		assertThat(file).usingCharset(charset).hasContent("Hello World!Goodbye.");
	}

	/**
	 * Verifies that an already existing file is continued and not overwritten.
	 *
	 * @param charset The charset to use for writing string
	 */
	@ParameterizedTest
	@ArgumentsSource(CharsetsProvider.class)
	void appendExistingFile(Charset charset) throws IOException {
		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), file, charset)) {
			LogEntry entry = new LogEntryBuilder().message("Hello World!").create();
			writer.log(entry);
		}

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), file, charset)) {
			LogEntry entry = new LogEntryBuilder().message("Goodbye.").create();
			writer.log(entry);
		}

		assertThat(file).usingCharset(charset).hasContent("Hello World!Goodbye.");
	}


	/**
	 * Verifies that a character that is not supported by the passed charset is replaced by a question mark ("?").
	 */
	@Test
	void writeUnsupportedCharacter() throws IOException {
		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), file, US_ASCII)) {
			LogEntry entry = new LogEntryBuilder().message("<ä>").create();
			writer.log(entry);
		}

		assertThat(file).usingCharset(US_ASCII).hasContent("<?>");
	}

	/**
	 * Provider for all charsets to test.
	 */
	private static final class CharsetsProvider implements ArgumentsProvider {

		/** */
		private CharsetsProvider() {
		}

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
				Arguments.of(US_ASCII),
				Arguments.of(StandardCharsets.ISO_8859_1),
				Arguments.of(UTF_8),
				Arguments.of(StandardCharsets.UTF_16)
			);
		}

	}

}
