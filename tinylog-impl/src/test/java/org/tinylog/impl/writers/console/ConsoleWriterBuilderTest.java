package org.tinylog.impl.writers.console;

import java.io.PrintStream;
import java.time.Instant;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.writers.Writer;
import org.tinylog.impl.writers.WriterBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@CaptureLogEntries
@ExtendWith(MockitoExtension.class)
class ConsoleWriterBuilderTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

	@Mock
	private PrintStream mockedOutputStream;

	@Mock
	private PrintStream mockedErrorStream;

	private PrintStream originalOutputStream;
	private PrintStream originalErrorStream;

	/**
	 * Mocks {@link System#out} and {@link System#err}.
	 */
	@BeforeEach
	void init() {
		originalOutputStream = System.out;
		originalErrorStream = System.err;

		System.setOut(mockedOutputStream);
		System.setErr(mockedErrorStream);
	}

	/**
	 * Restores the original streams for {@link System#out} and {@link System#err}.
	 */
	@AfterEach
	void reset() {
		System.setOut(originalOutputStream);
		System.setErr(originalErrorStream);
	}

	/**
	 * Verifies that the default format pattern will be used, if no custom format pattern is set.
	 */
	@Test
	@CaptureLogEntries(configuration = {"locale=en_US", "zone=UTC"})
	void defaultPattern() throws Exception {
		Configuration configuration = new Configuration().set("threshold", "off");
		try (Writer writer = new ConsoleWriterBuilder().create(framework, configuration)) {
			LogEntry logEntry = new LogEntryBuilder()
				.timestamp(Instant.EPOCH)
				.thread(new Thread(() -> { }, "main"))
				.severityLevel(Level.INFO)
				.className("org.MyClass")
				.methodName("foo")
				.message("Hello World!")
				.create();

			writer.log(logEntry);

			verify(mockedOutputStream)
				.print("1970-01-01 00:00:00 [main] INFO  org.MyClass.foo(): Hello World!" + System.lineSeparator());
		}
	}

	/**
	 * Verifies that a new line will be appended to a custom format pattern automatically.
	 */
	@Test
	void appendNewLineToCustomPattern() throws Exception {
		Configuration configuration = new Configuration().set("pattern", "{message}").set("threshold", "off");
		try (Writer writer = new ConsoleWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().severityLevel(Level.INFO).message("Hello World!").create());
			verify(mockedOutputStream).print("Hello World!" + System.lineSeparator());
		}
	}

	/**
	 * Verifies that {@link Level#WARN} will be used as default severity level threshold, if no custom threshold is set.
	 */
	@Test
	void defaultSeverityLevelThreshold() throws Exception {
		Configuration configuration = new Configuration().set("pattern", "{message}");
		try (Writer writer = new ConsoleWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().severityLevel(Level.INFO).message("Hello system out!").create());
			verify(mockedOutputStream).print("Hello system out!" + System.lineSeparator());

			writer.log(new LogEntryBuilder().severityLevel(Level.WARN).message("Hello system err!").create());
			verify(mockedErrorStream).print("Hello system err!" + System.lineSeparator());
		}
	}

	/**
	 * Verifies that a custom severity level threshold can be set.
	 */
	@Test
	void customSeverityLevelThreshold() throws Exception {
		Configuration configuration = new Configuration().set("pattern", "{message}").set("threshold", "error");
		try (Writer writer = new ConsoleWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().severityLevel(Level.WARN).message("Hello system out!").create());
			verify(mockedOutputStream).print("Hello system out!" + System.lineSeparator());

			writer.log(new LogEntryBuilder().severityLevel(Level.ERROR).message("Hello system err!").create());
			verify(mockedErrorStream).print("Hello system err!" + System.lineSeparator());
		}
	}

	/**
	 * Verifies that an illegal severity level as threshold is logged and the writer uses the default severity level
	 * threshold {@link Level#WARN} instead.
	 */
	@Test
	void illegalSeverityLevelThreshold() throws Exception {
		Configuration configuration = new Configuration().set("pattern", "{message}").set("threshold", "foo");
		try (Writer writer = new ConsoleWriterBuilder().create(framework, configuration)) {
			writer.log(new LogEntryBuilder().severityLevel(Level.INFO).message("Hello system out!").create());
			verify(mockedOutputStream).print("Hello system out!" + System.lineSeparator());

			writer.log(new LogEntryBuilder().severityLevel(Level.WARN).message("Hello system err!").create());
			verify(mockedErrorStream).print("Hello system err!" + System.lineSeparator());
		}

		assertThat(log.consume()).anySatisfy(entry -> {
			assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
			assertThat(entry.getMessage()).contains("foo");
		});
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		Assertions.assertThat(ServiceLoader.load(WriterBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(ConsoleWriterBuilder.class);
			assertThat(builder.getName()).isEqualTo("console");
		});
	}

}
