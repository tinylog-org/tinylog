package org.tinylog.impl.writer;

import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.core.Level;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.MessageOnlyPlaceholder;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsoleWriterTest {

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
	 * Verifies that the console writer requires all log entry values from the passed placeholder plus
	 * {@link LogEntryValue#LEVEL}.
	 */
	@Test
	void requiredLogEntryValues() {
		ConsoleWriter writer = new ConsoleWriter(new MessageOnlyPlaceholder(), Level.WARN);
		try {
			assertThat(writer.getRequiredLogEntryValues()).containsExactly(LogEntryValue.LEVEL, LogEntryValue.MESSAGE);
		} finally {
			writer.close();
		}
	}

	/**
	 * Verifies that all log entries are output to the correct stream according to the defined severity level threshold.
	 */
	@Test
	void logging() {
		ConsoleWriter writer = new ConsoleWriter(new MessageOnlyPlaceholder(), Level.WARN);
		try {
			writer.log(new LogEntryBuilder().severityLevel(Level.TRACE).message("Hello Trace!").create());
			verify(mockedOutputStream).print("Hello Trace!");

			writer.log(new LogEntryBuilder().severityLevel(Level.DEBUG).message("Hello Debug!").create());
			verify(mockedOutputStream).print("Hello Debug!");

			writer.log(new LogEntryBuilder().severityLevel(Level.INFO).message("Hello Info!").create());
			verify(mockedOutputStream).print("Hello Info!");

			writer.log(new LogEntryBuilder().severityLevel(Level.WARN).message("Hello Warn!").create());
			verify(mockedErrorStream).print("Hello Warn!");

			writer.log(new LogEntryBuilder().severityLevel(Level.ERROR).message("Hello Error!").create());
			verify(mockedErrorStream).print("Hello Error!");
		} finally {
			writer.close();
		}
	}

}
