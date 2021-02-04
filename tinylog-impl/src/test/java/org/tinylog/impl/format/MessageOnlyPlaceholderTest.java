package org.tinylog.impl.format;

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

class MessageOnlyPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#MESSAGE} is defined as required by the message only
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		MessageOnlyPlaceholder placeholder = new MessageOnlyPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.MESSAGE);
	}

	/**
	 * Verifies that the log message will be output, if set.
	 */
	@Test
	void renderWithMessage() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MessageOnlyPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(renderer.render(logEntry)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that nothing will be output, if the log message is not set.
	 */
	@Test
	void renderWithoutMessage() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MessageOnlyPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("");
	}

	/**
	 * Verifies that the log message of a log entry will be applied to a{@link PreparedStatement}, if set.
	 */
	@Test
	void applyWithMessage() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		new MessageOnlyPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "Hello World!");
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the log message is not set.
	 */
	@Test
	void applyWithoutMessage() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new MessageOnlyPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, null);
	}

}
