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

class ClassPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#CLASS} is defined as required by the class placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ClassPlaceholder placeholder = new ClassPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CLASS);
	}

	/**
	 * Verifies that the source class name of a log entry will be output, if set.
	 */
	@Test
	void renderWithClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo.MyClass");
	}

	/**
	 * Verifies that {@code <class unknown>} will be output, if the class name is not set.
	 */
	@Test
	void renderWithoutClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<class unknown>");
	}

	/**
	 * Verifies that the source class name of a log entry will be applied to a {@link PreparedStatement}, if set.
	 */
	@Test
	void applyWithClassName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
		new ClassPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "foo.MyClass");
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the source class name is not set.
	 */
	@Test
	void applyWithoutClassName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new ClassPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, null);
	}

}
