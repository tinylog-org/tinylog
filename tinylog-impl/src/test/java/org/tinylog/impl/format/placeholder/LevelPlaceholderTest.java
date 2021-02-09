package org.tinylog.impl.format.placeholder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LevelPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#LEVEL} is defined as required by the level placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		LevelPlaceholder placeholder = new LevelPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that the severity level of a log entry will be output, if set.
	 */
	@Test
	void renderWithSeverityLevel() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new LevelPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
		assertThat(renderer.render(logEntry)).isEqualTo("INFO");
	}

	/**
	 * Verifies that {@code <level unknown>} will be output, if the severity level is not set.
	 */
	@Test
	void renderWithoutSeverityLevel() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new LevelPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<level unknown>");
	}

	/**
	 * Verifies that the severity level of a log entry will be applied to a {@link PreparedStatement}, if set.
	 */
	@Test
	void applyWithSeverityLevel() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
		new LevelPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "INFO");
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the severity level is not set.
	 */
	@Test
	void applyWithoutSeverityLevel() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new LevelPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, null);
	}

}
