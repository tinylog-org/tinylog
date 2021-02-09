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

class ThreadPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#THREAD} is defined as required by the thread placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ThreadPlaceholder placeholder = new ThreadPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.THREAD);
	}

	/**
	 * Verifies that the source thread name of a log entry will be output, if the thread object is present.
	 */
	@Test
	void renderWithSourceThread() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ThreadPlaceholder());
		Thread thread = new Thread(() -> { }, "foo");
		LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo");
	}

	/**
	 * Verifies that {@code <thread unknown>} will be output, if the thread object is not present.
	 */
	@Test
	void renderWithoutSourceThread() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ThreadPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<thread unknown>");
	}

	/**
	 * Verifies that the source thread name of a log entry will be applied to a {@link PreparedStatement}, if the thread
	 * object is present.
	 */
	@Test
	void applyWithSourceThread() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		Thread thread = new Thread(() -> { }, "foo");
		LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
		new ThreadPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, "foo");
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the thread object is not present.
	 */
	@Test
	void applyWithoutSourceThread() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new ThreadPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setString(42, null);
	}

}
