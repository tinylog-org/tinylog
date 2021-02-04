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

class MethodPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#METHOD} is defined as required by the method placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		MethodPlaceholder placeholder = new MethodPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.METHOD);
	}

	/**
	 * Verifies that the source method name of a log entry will be output, if set.
	 */
	@Test
	void renderWithSourceMethodName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MethodPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().methodName("foo").create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo");
	}

	/**
	 * Verifies that {@code <method unknown>} will be output, if the source method name is not set.
	 */
	@Test
	void renderWithoutSourceMethodName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MethodPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<method unknown>");
	}

	/**
	 * Verifies that the source method name of a log entry will be applied to a {@link PreparedStatement}, if set.
	 */
	@Test
	void applyWithSourceMethodName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().methodName("foo").create();
		new MethodPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "foo");
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the source method name is not set.
	 */
	@Test
	void applyWithoutSourceMethodName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new MethodPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, null);
	}

}
