package org.tinylog.impl.writers.file;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.placeholders.MessagePlaceholder;
import org.tinylog.impl.path.DynamicPath;
import org.tinylog.impl.policies.EndlessPolicy;
import org.tinylog.impl.policies.SizePolicy;
import org.tinylog.impl.policies.StartupPolicy;
import org.tinylog.impl.test.LogEntryBuilder;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@CaptureLogEntries
class FileWriterTest {

	@Inject
	private Framework framework;

	@TempDir
	private Path folder;

	/**
	 * Verifies that the file writer requires only log entry values from the passed placeholder.
	 */
	@Test
	void requiredLogEntryValues() throws Exception {
		Path file = folder.resolve("tinylog.log");
		DynamicPath path = new DynamicPath(framework, file.toString());

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), new EndlessPolicy(), path, UTF_8)) {
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
	void writeMultipleMessages(Charset charset) throws Exception {
		Path file = folder.resolve("tinylog.log");
		DynamicPath path = new DynamicPath(framework, file.toString());

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), new EndlessPolicy(), path, charset)) {
			LogEntry entry = new LogEntryBuilder().message("Hello World!").create();
			writer.log(entry);

			entry = new LogEntryBuilder().message("Goodbye.").create();
			writer.log(entry);
		}

		assertThat(file).usingCharset(charset).hasContent("Hello World!Goodbye.");
	}

	/**
	 * Verifies that an already existing file is continued can be continued.
	 *
	 * @param charset The charset to use for writing string
	 */
	@ParameterizedTest
	@ArgumentsSource(CharsetsProvider.class)
	void continueExistingFile(Charset charset) throws Exception {
		Path file = folder.resolve("tinylog.log");
		DynamicPath path = new DynamicPath(framework, file.toString());

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), new EndlessPolicy(), path, charset)) {
			LogEntry entry = new LogEntryBuilder().message("Hello World!").create();
			writer.log(entry);
		}

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), new EndlessPolicy(), path, charset)) {
			LogEntry entry = new LogEntryBuilder().message("Goodbye.").create();
			writer.log(entry);
		}

		assertThat(file).usingCharset(charset).hasContent("Hello World!Goodbye.");
	}

	/**
	 * Verifies that an already existing file can be overwritten.
	 *
	 * @param charset The charset to use for writing string
	 */
	@ParameterizedTest
	@ArgumentsSource(CharsetsProvider.class)
	void overwriteExistingFile(Charset charset) throws Exception {
		Path file = folder.resolve("tinylog.log");
		DynamicPath path = new DynamicPath(framework, file.toString());

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), new StartupPolicy(), path, charset)) {
			LogEntry entry = new LogEntryBuilder().message("Hello World!").create();
			writer.log(entry);
		}

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), new StartupPolicy(), path, charset)) {
			LogEntry entry = new LogEntryBuilder().message("Goodbye.").create();
			writer.log(entry);
		}

		assertThat(file).usingCharset(charset).hasContent("Goodbye.");
	}

	/**
	 * Verifies that a new log file can be started via policy.
	 *
	 * @param charset The charset to use for writing string
	 */
	@ParameterizedTest
	@ArgumentsSource(CharsetsProvider.class)
	void rollingExistingFile(Charset charset) throws Exception {
		int size = "ab".getBytes(charset).length;
		Path file = folder.resolve("tinylog.log");
		DynamicPath path = new DynamicPath(framework, file.toString());

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), new SizePolicy(size), path, charset)) {
			LogEntry entry = new LogEntryBuilder().message("a").create();
			writer.log(entry);
		}
		assertThat(file).usingCharset(charset).hasContent("a");

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), new SizePolicy(size), path, charset)) {
			LogEntry entry = new LogEntryBuilder().message("bc").create();
			writer.log(entry);
		}
		assertThat(file).usingCharset(charset).hasContent("bc");
	}

	/**
	 * Verifies that the content is written when flushing.
	 */
	@Test
	void flushing() throws Exception {
		Path file = folder.resolve("tinylog.log");
		DynamicPath path = new DynamicPath(framework, file.toString());

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), new EndlessPolicy(), path, UTF_8)) {
			writer.log(new LogEntryBuilder().message("1").create());
			writer.log(new LogEntryBuilder().message("2").create());
			writer.log(new LogEntryBuilder().message("3").create());

			assertThat(file).usingCharset(UTF_8).isEmptyFile();
			writer.flush();
			assertThat(file).usingCharset(UTF_8).hasContent("123");
		}
	}

	/**
	 * Verifies that a character that is not supported by the passed charset is replaced by a question mark ("?").
	 */
	@Test
	void writeUnsupportedCharacter() throws Exception {
		Path file = folder.resolve("tinylog.log");
		DynamicPath path = new DynamicPath(framework, file.toString());

		try (FileWriter writer = new FileWriter(new MessagePlaceholder(), new EndlessPolicy(), path, US_ASCII)) {
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
