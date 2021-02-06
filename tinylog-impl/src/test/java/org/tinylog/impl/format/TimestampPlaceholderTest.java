package org.tinylog.impl.format;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TimestampPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#TIMESTAMP} is defined as required by the timestamp
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		TimestampPlaceholder placeholder = new TimestampPlaceholder(Instant::toEpochMilli);
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.TIMESTAMP);
	}

	/**
	 * Verifies that the timestamp of issue of a log entry will be output, if present.
	 */
	@Test
	void renderWithTimestamp() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new TimestampPlaceholder(Instant::toEpochMilli));
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1000)).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1000");
	}

	/**
	 * Verifies that {@code <timestamp unknown>} will be output, if the timestamp is not present.
	 */
	@Test
	void renderWithoutTimestamp() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new TimestampPlaceholder(Instant::toEpochMilli));
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<timestamp unknown>");
	}

	/**
	 * Verifies that the timestamp of issue of a log entry will be applied to a {@link PreparedStatement}, if present.
	 */
	@Test
	void applyWithTimestamp() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1000)).create();
		new TimestampPlaceholder(Instant::toEpochMilli).apply(statement, 42, logEntry);
		verify(statement).setLong(42, 1000);
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the timestamp is not present.
	 */
	@Test
	void applyWithoutTimestamp() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new TimestampPlaceholder(Instant::toEpochMilli).apply(statement, 42, logEntry);
		verify(statement).setNull(42, Types.BIGINT);
	}

}
