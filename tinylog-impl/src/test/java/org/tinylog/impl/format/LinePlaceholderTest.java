package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LinePlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#LINE} is defined as required by the line placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		LinePlaceholder placeholder = new LinePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.LINE);
	}

	/**
	 * Verifies that the line number of a log entry in the source file will be output, if set.
	 */
	@Test
	void renderWithSourceLineName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new LinePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().lineNumber(100).create();
		assertThat(renderer.render(logEntry)).isEqualTo("100");
	}

	/**
	 * Verifies that "?" will be output, if the line number in the source file is not set.
	 */
	@Test
	void renderWithoutSourceLineName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new LinePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("?");
	}

	/**
	 * Verifies that the line number of a log entry in the source file will be applied to a{@link PreparedStatement},
	 * if set.
	 */
	@Test
	void applyWithSourceLineName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().lineNumber(100).create();
		new LinePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setInt(42, 100);
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the line number in the source file
	 * is not set.
	 */
	@Test
	void applyWithoutSourceLineName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new LinePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setNull(42, Types.INTEGER);
	}

}
