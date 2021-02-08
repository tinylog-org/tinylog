package org.tinylog.impl.writer;

import java.io.PrintStream;
import java.util.Map;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.impl.test.LogEntryBuilder;

import com.google.common.collect.ImmutableMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@CaptureLogEntries
@ExtendWith(MockitoExtension.class)
class ConsoleWriterBuilderTest {

	@Inject
	private Framework framework;

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
	 * Verifies that a new line will be appended to the format pattern automatically.
	 */
	@Test
	void appendNewLineToPattern() throws Exception {
		Map<String, String> configuration = ImmutableMap.of("pattern", "{message}", "threshold", "off");
		Writer writer = new ConsoleWriterBuilder().create(framework, configuration);
		try {
			writer.log(new LogEntryBuilder().severityLevel(Level.INFO).message("Hello World!").create());
			verify(mockedOutputStream).print("Hello World!" + System.lineSeparator());
		} finally {
			writer.close();
		}
	}

	/**
	 * Verifies that {@link Level#WARN} will be used as default severity level threshold, if no custom threshold is set.
	 */
	@Test
	void defaultSeverityLevelThreshold() throws Exception {
		Map<String, String> configuration = ImmutableMap.of("pattern", "{message}");
		Writer writer = new ConsoleWriterBuilder().create(framework, configuration);
		try {
			writer.log(new LogEntryBuilder().severityLevel(Level.INFO).message("Hello system out!").create());
			verify(mockedOutputStream).print("Hello system out!" + System.lineSeparator());

			writer.log(new LogEntryBuilder().severityLevel(Level.WARN).message("Hello system err!").create());
			verify(mockedErrorStream).print("Hello system err!" + System.lineSeparator());
		} finally {
			writer.close();
		}
	}

	/**
	 * Verifies that a custom severity level threshold can be set.
	 */
	@Test
	void customSeverityLevelThreshold() throws Exception {
		Map<String, String> configuration = ImmutableMap.of("pattern", "{message}", "threshold", "error");
		Writer writer = new ConsoleWriterBuilder().create(framework, configuration);
		try {
			writer.log(new LogEntryBuilder().severityLevel(Level.WARN).message("Hello system out!").create());
			verify(mockedOutputStream).print("Hello system out!" + System.lineSeparator());

			writer.log(new LogEntryBuilder().severityLevel(Level.ERROR).message("Hello system err!").create());
			verify(mockedErrorStream).print("Hello system err!" + System.lineSeparator());
		} finally {
			writer.close();
		}
	}

	/**
	 * Verifies that the builder is registered as service.
	 */
	@Test
	void service() {
		assertThat(ServiceLoader.load(WriterBuilder.class)).anySatisfy(builder -> {
			assertThat(builder).isInstanceOf(ConsoleWriterBuilder.class);
			assertThat(builder.getName()).isEqualTo("console");
		});
	}

}
