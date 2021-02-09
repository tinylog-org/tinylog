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

class ClassNamePlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#CLASS} is defined as required by the class name
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ClassNamePlaceholder placeholder = new ClassNamePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CLASS);
	}

	/**
	 * Verifies that the simple source class name of a log entry will be output, if a simple class name is set.
	 */
	@Test
	void renderWithSimpleClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassNamePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().className("MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("MyClass");
	}

	/**
	 * Verifies that the simple source class name of a log entry will be output, if a fully-qualified class name is set.
	 */
	@Test
	void renderWithFullyQualifiedClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassNamePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("MyClass");
	}

	/**
	 * Verifies that {@code <class unknown>} will be output, if the class name is not set.
	 */
	@Test
	void renderWithoutClassName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ClassNamePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<class unknown>");
	}

	/**
	 * Verifies that the simple source class name of a log entry will be applied to a {@link PreparedStatement}, if a
	 * simple class name is set.
	 */
	@Test
	void applyWithSimpleClassName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().className("MyClass").create();
		new ClassNamePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "MyClass");
	}

	/**
	 * Verifies that the simple source class name of a log entry will be applied to a {@link PreparedStatement}, if a
	 * fully-qualified class name is set.
	 */
	@Test
	void applyWithFullyQualifiedClassName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
		new ClassNamePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "MyClass");
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the source class name is not set.
	 */
	@Test
	void applyWithoutClassName() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new ClassNamePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, null);
	}

}
