package org.tinylog.impl.writers.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.json.NewlineDelimitedJson;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.writers.Writer;
import org.tinylog.impl.writers.WriterBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@CaptureLogEntries
class FileWriterBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

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
	 * Verifies that the default format pattern will be used, if no custom format pattern is set.
	 */
	@Test
	@CaptureLogEntries(configuration = {"locale=en_US", "zone=UTC"})
	void defaultPattern() throws Exception {
		Configuration configuration = new Configuration().set("file", file.toString());

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			LogEntry logEntry = new LogEntryBuilder()
				.timestamp(Instant.EPOCH)
				.thread(new Thread(() -> { }, "main"))
				.severityLevel(Level.INFO)
				.className("org.MyClass")
				.methodName("foo")
				.message("Hello World!")
				.create();

			writer.log(logEntry);
		}

		assertThat(file)
			.hasContent("1970-01-01 00:00:00 [main] INFO  org.MyClass.foo(): Hello World!" + System.lineSeparator());
	}

	/**
	 * Verifies that custom output formats like {@link NewlineDelimitedJson} are supported.
	 */
	@Test
	void customJsonFormat() throws Exception {
		Configuration configuration = new Configuration()
			.set("file", file.toString())
			.set("format", "ndjson")
			.set("fields.msg", "message");

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
			writer.log(logEntry);
		}

		assertThat(file).hasContent("{\"msg\": \"Hello World!\"}" + System.lineSeparator());
	}

	/**
	 * Verifies that illegal output formats are reported and the writer will use the default pattern output format
	 * instead.
	 */
	@Test
	@CaptureLogEntries(configuration = {"locale=en_US", "zone=UTC"})
	void illegalOutputFormat() throws Exception {
		Configuration configuration = new Configuration()
			.set("file", file.toString())
			.set("format", "foo");

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			assertThat(log.consume()).singleElement().satisfies(entry -> {
				assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
				assertThat(entry.getMessage()).contains("format", "foo");
			});

			LogEntry logEntry = new LogEntryBuilder()
				.timestamp(Instant.EPOCH)
				.thread(new Thread(() -> { }, "main"))
				.severityLevel(Level.INFO)
				.className("org.MyClass")
				.methodName("foo")
				.message("Hello World!")
				.create();

			writer.log(logEntry);
		}

		assertThat(file)
			.hasContent("1970-01-01 00:00:00 [main] INFO  org.MyClass.foo(): Hello World!" + System.lineSeparator());
	}

	/**
	 * Verifies that a new line will be appended to a custom format pattern automatically.
	 */
	@Test
	void appendNewLineToCustomPattern() throws Exception {
		Configuration configuration = new Configuration()
			.set("pattern", "{message}")
			.set("file", file.toString());

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().message("Hello World!").create());
		}

		assertThat(file).hasContent("Hello World!" + System.lineSeparator());
	}

	/**
	 * Verifies that an exception with a meaningful message will be thrown, if file name is undefined.
	 */
	@Test
	void missingFileName() {
		Configuration configuration = new Configuration();
		Throwable throwable = catchThrowable(() -> new FileWriterBuilder().create(framework, configuration).close());

		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
		assertThat(throwable.getMessage()).containsIgnoringCase("file");
	}

	/**
	 * Verifies that UTF-8 can be defined as custom charset, regardless of the spelling.
	 *
	 * @param charsetName The UTF-8 spelling to test
	 */
	@ParameterizedTest
	@ValueSource(strings = {"utf8", "utf-8", "UTF8", "UTF-8"})
	void utf8Charset(String charsetName) throws Exception {
		Configuration configuration = new Configuration()
			.set("pattern", "{message}")
			.set("file", file.toString())
			.set("charset", charsetName);

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().message("abc - äöüß - áéíóúüñ - 한글").create());
		}

		assertThat(file)
			.usingCharset(StandardCharsets.UTF_8)
			.hasContent("abc - äöüß - áéíóúüñ - 한글" + System.lineSeparator());
	}

	/**
	 * Verifies that ASCII can be defined as custom charset, regardless of the spelling.
	 *
	 * @param charsetName The ASCII spelling to test
	 */
	@ParameterizedTest
	@ValueSource(strings = {"ascii", "us-ascii", "ASCII", "US-ASCII"})
	void asciiCharset(String charsetName) throws Exception {
		Configuration configuration = new Configuration()
			.set("pattern", "{message}")
			.set("file", file.toString())
			.set("charset", charsetName);

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().message("abc - äöüß - áéíóúüñ - 한글").create());
		}

		assertThat(file)
			.usingCharset(StandardCharsets.US_ASCII)
			.hasContent("abc - ???? - ??????? - ??" + System.lineSeparator());
	}

	/**
	 * Verifies that an invalid charset name is reported, but does not prevent the file writer from outputting log
	 * entries.
	 */
	@Test
	void invalidCharset() throws Exception {
		Configuration configuration = new Configuration()
			.set("pattern", "{message}")
			.set("file", file.toString())
			.set("charset", "dummy");

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().message("Hello World!").create());
		}

		assertThat(file).hasContent("Hello World!" + System.lineSeparator());

		assertThat(log.consume()).singleElement().satisfies(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains("charset", "dummy");
		});
	}

	/**
	 * Verifies that a file writer can be created without defining a policy.
	 */
	@Test
	void noPolicy() throws Exception {
		Configuration configuration = new Configuration()
			.set("pattern", "{message}")
			.set("file", file.toString())
			.set("chatset", StandardCharsets.US_ASCII.name());

		Files.write(file, Collections.singleton("foo"), StandardCharsets.US_ASCII);

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().message("bar").create());
		}

		assertThat(file).hasContent("foo" + System.lineSeparator() + "bar" + System.lineSeparator());
	}

	/**
	 * Verifies that a file writer can be created with a single policy.
	 */
	@Test
	void singlePolicy() throws Exception {
		Configuration configuration = new Configuration()
			.set("pattern", "{message}")
			.set("file", file.toString())
			.set("chatset", StandardCharsets.US_ASCII.name())
			.set("policies", "startup");

		Files.write(file, Collections.singleton("foo"), StandardCharsets.US_ASCII);

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().message("bar").create());
		}

		assertThat(file).hasContent("bar" + System.lineSeparator());
	}

	/**
	 * Verifies that a file writer can be created with multiple policies.
	 */
	@Test
	void multiplePolicies() throws Exception {
		int size = (1 + System.lineSeparator().length()) * 2; // two lines (letter + line separator)

		Configuration configuration = new Configuration()
			.set("pattern", "{message}")
			.set("file", file.toString())
			.set("chatset", StandardCharsets.US_ASCII.name())
			.set("policies", "startup, size: " + size);

		Files.write(file, Collections.singleton("a"), StandardCharsets.US_ASCII);

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().message("b").create());
			writer.log(new LogEntryBuilder().message("c").create());
			writer.log(new LogEntryBuilder().message("d").create());
		}

		assertThat(file).usingCharset(StandardCharsets.US_ASCII).hasContent("d" + System.lineSeparator());
	}

	/**
	 * Verifies that unknown policies are reported, but other known policies work nevertheless.
	 */
	@Test
	void reportUnknownPolicy() throws Exception {
		Configuration configuration = new Configuration()
			.set("pattern", "{message}")
			.set("file", file.toString())
			.set("chatset", StandardCharsets.US_ASCII.name())
			.set("policies", "foo, startup");

		Files.write(file, Collections.singleton("foo"), StandardCharsets.US_ASCII);

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().message("bar").create());
		}

		assertThat(file).hasContent("bar" + System.lineSeparator());
		assertThat(log.consume()).singleElement().satisfies(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains("foo");
		});
	}

	/**
	 * Verifies that invalid configured policies are reported, but other valid configured policies work nevertheless.
	 */
	@Test
	void reportInvalidPolicy() throws Exception {
		Configuration configuration = new Configuration()
			.set("pattern", "{message}")
			.set("file", file.toString())
			.set("chatset", StandardCharsets.US_ASCII.name())
			.set("policies", "size: AB, startup");

		Files.write(file, Collections.singleton("foo"), StandardCharsets.US_ASCII);

		try (Writer writer = new FileWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().message("bar").create());
		}

		assertThat(file).hasContent("bar" + System.lineSeparator());
		assertThat(log.consume()).singleElement().satisfies(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains("size");
			assertThat(entry.getThrowable()).hasMessageContaining("AB");
		});
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(WriterBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(FileWriterBuilder.class);
			assertThat(builder.getName()).isEqualTo("file");
		});
	}

}
