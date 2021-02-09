package org.tinylog.impl.format.placeholder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SeverityCodePlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#LEVEL} is defined as required by the severity code
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		SeverityCodePlaceholder placeholder = new SeverityCodePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that the numeric severity level code of a log entry will be output, if the severity level set.
	 */
	@Test
	void renderWithSeverityLevel() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new SeverityCodePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
		assertThat(renderer.render(logEntry)).isEqualTo("3");
	}

	/**
	 * Verifies that "?" will be output, if the severity level is not set.
	 */
	@Test
	void renderWithoutSeverityLevel() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new SeverityCodePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("?");
	}

	/**
	 * Verifies that the numeric severity level code of a log entry will be applied to a {@link PreparedStatement},
	 * if the severity level is set.
	 */
	@Test
	void applyWithSeverityLevel() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
		new SeverityCodePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setInt(42, 3);
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the severity level is not set.
	 */
	@Test
	void applyWithoutSeverityLevel() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new SeverityCodePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setNull(42, Types.INTEGER);
	}

}
