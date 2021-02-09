package org.tinylog.impl.format.placeholder;

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

class ContextPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#CONTEXT} is defined as required by the context
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ContextPlaceholder placeholder = new ContextPlaceholder("foo", null, null);
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CONTEXT);
	}

	/**
	 * Verifies that a thread context value of a log entry will be output, if present.
	 */
	@Test
	void renderWithContextValue() {
		ContextPlaceholder placeholder = new ContextPlaceholder("foo", "-", null);
		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").create();
		assertThat(renderer.render(logEntry)).isEqualTo("bar");
	}

	/**
	 * Verifies that the default value will be output, if a thread context value is not present.
	 */
	@Test
	void renderWithoutContextValue() {
		ContextPlaceholder placeholder = new ContextPlaceholder("foo", "-", null);
		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("-");
	}

	/**
	 * Verifies that a thread context value of a log entry will be applied to a {@link PreparedStatement}, if present.
	 */
	@Test
	void applyWithContextValue() throws SQLException {
		ContextPlaceholder placeholder = new ContextPlaceholder("foo", null, "-");
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").create();
		placeholder.apply(statement, 42, logEntry);
		verify(statement).setString(42, "bar");
	}

	/**
	 * Verifies that the default value will be applied to a {@link PreparedStatement}, if a thread context value is not
	 * present.
	 */
	@Test
	void applyWithoutContextValue() throws SQLException {
		ContextPlaceholder placeholder = new ContextPlaceholder("foo", null, "-");
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		placeholder.apply(statement, 42, logEntry);
		verify(statement).setString(42, "-");
	}

}
