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

class PackagePlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#CLASS} is defined as required by the package placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		PackagePlaceholder placeholder = new PackagePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CLASS);
	}

	/**
	 * Verifies that an empty string will be output, if a class name with default package is set.
	 */
	@Test
	void renderWithDefaultPackage() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new PackagePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().className("MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("MyClass");
	}

	/**
	 * Verifies that the package name of a log entry will be output, if a fully-qualified class name with package
	 * information is set.
	 */
	@Test
	void renderWithFullyQualifiedPackage() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new PackagePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
		assertThat(renderer.render(logEntry)).isEqualTo("org.foo");
	}

	/**
	 * Verifies that {@code <package unknown>} will be output, if the class name is not set.
	 */
	@Test
	void renderWithoutPackage() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new PackagePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<package unknown>");
	}

	/**
	 * Verifies that an string will be applied to a {@link PreparedStatement}, if a fully-qualified class name with
	 * package information is set.
	 */
	@Test
	void applyWithDefaultPackage() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().className("MyClass").create();
		new PackagePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "MyClass");
	}

	/**
	 * Verifies that the package name of a log entry will be applied to a {@link PreparedStatement}, if a
	 * fully-qualified class name with package information is set.
	 */
	@Test
	void applyWithFullyQualifiedPackage() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().className("org.foo.MyClass").create();
		new PackagePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "org.foo");
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the source class name is not set.
	 */
	@Test
	void applyWithoutPackage() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new PackagePlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, null);
	}

}
