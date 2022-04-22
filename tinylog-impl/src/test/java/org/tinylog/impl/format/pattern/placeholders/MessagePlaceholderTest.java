package org.tinylog.impl.format.pattern.placeholders;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class MessagePlaceholderTest {

	/**
	 * Verifies that the log entry values {@link LogEntryValue#MESSAGE} and {@link LogEntryValue#EXCEPTION} are defined
	 * are required by the message placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		MessagePlaceholder placeholder = new MessagePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues())
			.containsExactlyInAnyOrder(LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
	}

	/**
	 * Verifies that {@code null} will be resolved, if neither a log message nor an exception is set.
	 */
	@Test
	void resolveWithoutMessageOrException() {
		LogEntry logEntry = new LogEntryBuilder().create();

		MessagePlaceholder placeholder = new MessagePlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isNull();
	}

	/**
	 * Verifies that the log message will be correctly resolved, if the log message is set but not an exception.
	 */
	@Test
	void resolveWithMessageOnly() {
		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();

		MessagePlaceholder placeholder = new MessagePlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that the exception will be correctly resolved, if the exception is set but not a log message.
	 */
	@Test
	void resolveWithExceptionOnly() {
		Exception exception = new RuntimeException();
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();

		MessagePlaceholder placeholder = new MessagePlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Verifies that the log message and the exception will be correctly resolved, if both are set.
	 */
	@Test
	void resolveWithMessageAndException() {
		Exception exception = new RuntimeException();
		LogEntry logEntry = new LogEntryBuilder().message("Oops").exception(exception).create();

		MessagePlaceholder placeholder = new MessagePlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isEqualTo("Oops: " + print(exception));
	}

	/**
	 * Verifies that nothing will be rendered, if neither a log message nor an exception are set.
	 */
	@Test
	void renderWithoutMessageOrException() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new MessagePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("");
	}

	/**
	 * Verifies that the log message will be rendered correctly, if the log message is set but not an exception.
	 */
	@Test
	void renderWithMessageOnly() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new MessagePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(renderer.render(logEntry)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that the exception will be rendered correctly, if the exception is set but not a log message.
	 */
	@Test
	void renderWithExceptionOnly() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new MessagePlaceholder());
		Exception exception = new RuntimeException();
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();
		assertThat(renderer.render(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Verifies that the log message and the exception are rendered correctly, if both are set.
	 */
	@Test
	void renderWithMessageAndException() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new MessagePlaceholder());
		Exception exception = new RuntimeException();
		LogEntry logEntry = new LogEntryBuilder().message("Oops").exception(exception).create();
		assertThat(renderer.render(logEntry)).isEqualTo("Oops: " + print(exception));
	}

	/**
	 * Prints a throwable including its stack trace as string.
	 *
	 * @param throwable The throwable to print
	 * @return The completely rendered throwable including stack trace
	 */
	private String print(Throwable throwable) {
		StringWriter writer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));
		return writer.toString().trim();
	}

}
