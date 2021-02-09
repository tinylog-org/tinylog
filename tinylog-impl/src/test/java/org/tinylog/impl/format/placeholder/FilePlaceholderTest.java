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

class FilePlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#FILE} is defined as required by the file placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		FilePlaceholder placeholder = new FilePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.FILE);
	}

	/**
	 * Verifies that the source file name of a log entry will be output, if set.
	 */
	@Test
	void renderWithSourceFileName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new FilePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().fileName("foo.java").create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo.java");
	}

	/**
	 * Verifies that {@code <file unknown>} will be output, if the source file name is not set.
	 */
	@Test
	void renderWithoutSourceFileName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new FilePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<file unknown>");
	}

	/**
	 * Verifies that the source file name of a log entry will be applied to a {@link PreparedStatement}, if set.
	 */
	@Test
	void applyWithSourceFileName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().fileName("foo.java").create();
		new FilePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "foo.java");
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the source file name is not set.
	 */
	@Test
	void applyWithoutSourceFileName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new FilePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, null);
	}

}
