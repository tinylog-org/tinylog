package org.tinylog.impl.format.placeholder;

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

class ThreadIdPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#THREAD} is defined as required by the thread ID
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ThreadIdPlaceholder placeholder = new ThreadIdPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.THREAD);
	}

	/**
	 * Verifies that the source thread ID of a log entry will be output, if the thread object is present.
	 */
	@Test
	void renderWithSourceThread() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ThreadIdPlaceholder());
		Thread thread = new Thread(() -> { });
		LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
		assertThat(renderer.render(logEntry)).isEqualTo(Long.toString(thread.getId()));
	}

	/**
	 * Verifies that "?" will be output, if the thread object is not present.
	 */
	@Test
	void renderWithoutSourceThread() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ThreadIdPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("?");
	}

	/**
	 * Verifies that the source thread ID of a log entry will be applied to a {@link PreparedStatement}, if the thread
	 * object is present.
	 */
	@Test
	void applyWithSourceThread() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		Thread thread = new Thread(() -> { });
		LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
		new ThreadIdPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setLong(42, thread.getId());
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the thread object is not present.
	 */
	@Test
	void applyWithoutSourceThread() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new ThreadIdPlaceholder().apply(statement, 42, logEntry);
		verify(statement).setNull(42, Types.BIGINT);
	}

}
