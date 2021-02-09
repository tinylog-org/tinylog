package org.tinylog.impl.format.placeholder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MessagePlaceholderTest {

	/**
	 * Verifies that the log entry values {@link LogEntryValue#MESSAGE} and {@link LogEntryValue#EXCEPTION} are defined
	 * are required by the message placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		MessagePlaceholder placeholder = new MessagePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues())
			.containsExactly(LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
	}

	/**
	 * Verifies that nothing will be rendered, if neither a log message nor an exception are set.
	 */
	@Test
	void renderWithoutMessageOrException() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MessagePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("");
	}

	/**
	 * Verifies that the log message will be rendered correctly, if the log message is set but not an exception.
	 */
	@Test
	void renderWithMessageOnly() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MessagePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(renderer.render(logEntry)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that the exception will be rendered correctly, if the exception is set but not a log message.
	 */
	@Test
	void renderWithExceptionOnly() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MessagePlaceholder());
		Exception exception = new RuntimeException();
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();
		assertThat(renderer.render(logEntry)).isEqualTo(print(exception));
	}

	/**
	 * Verifies that the log message and the exception are rendered correctly, if both are set.
	 */
	@Test
	void renderWithMessageAndException() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MessagePlaceholder());
		Exception exception = new RuntimeException();
		LogEntry logEntry = new LogEntryBuilder().message("Oops").exception(exception).create();
		assertThat(renderer.render(logEntry)).isEqualTo("Oops: " + print(exception));
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if neither a log message nor an
	 * exception are set.
	 */
	@Test
	void applyWithoutMessageOrException() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new MessagePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, null);
	}

	/**
	 * Verifies that the log message will be correctly applied to a {@link PreparedStatement}, if the log message is set
	 * but not an exception.
	 */
	@Test
	void applyWithMessageOnly() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		new MessagePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "Hello World!");
	}

	/**
	 * Verifies that the exception will be correctly applied to a {@link PreparedStatement}, if the exception is set but
	 * not a log message.
	 */
	@Test
	void applyWithExceptionOnly() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		Exception exception = new RuntimeException();
		LogEntry logEntry = new LogEntryBuilder().exception(exception).create();
		new MessagePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, print(exception));
	}

	/**
	 * Verifies that the log message and the exception will be correctly applied to a {@link PreparedStatement}, if both
	 * are set.
	 */
	@Test
	void applyWithMessageAndException() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		Exception exception = new RuntimeException();
		LogEntry logEntry = new LogEntryBuilder().message("Oops").exception(exception).create();
		new MessagePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "Oops: " + print(exception));
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
