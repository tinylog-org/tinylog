package org.tinylog.impl.format.placeholder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DatePlaceholderTest {

	private final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneOffset.UTC);

	/**
	 * Verifies that the log entry value {@link LogEntryValue#TIMESTAMP} is defined as required by the date placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		DatePlaceholder placeholder = new DatePlaceholder(DateTimeFormatter.ISO_INSTANT, false);
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.TIMESTAMP);
	}

	/**
	 * Verifies that the formatted date and time of a log entry will be output, if the timestamp is set.
	 */
	@Test
	void renderWithTimestamp() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new DatePlaceholder(formatter, false));
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1970-01-01T00:00:00Z");
	}

	/**
	 * Verifies that {@code <timestamp unknown>} will be output, if the timestamp is not set.
	 */
	@Test
	void renderWithoutTimestamp() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new DatePlaceholder(formatter, false));
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<timestamp unknown>");
	}

	/**
	 * Verifies that the date and time of a log entry will be applied as a {@link Timestamp} to a
	 * {@link PreparedStatement}, if the date and time of issue is set.
	 */
	@Test
	void applyUnformattedWithTimestamp() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		new DatePlaceholder(formatter, false).apply(statement, 42, logEntry);
		verify(statement).setTimestamp(42, new Timestamp(0));
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the date and time of issue is not
	 * set.
	 */
	@Test
	void applyUnformattedWithoutTimestamp() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new DatePlaceholder(formatter, false).apply(statement, 42, logEntry);
		verify(statement).setTimestamp(42, null);
	}

	/**
	 * Verifies that the date and time of a log entry will be applied as formatted string to a
	 * {@link PreparedStatement}, if the date and time of issue is set.
	 */
	@Test
	void applyFormattedWithTimestamp() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		new DatePlaceholder(formatter, true).apply(statement, 42, logEntry);
		verify(statement).setString(42, "1970-01-01T00:00:00Z");
	}

	/**
	 * Verifies that {@code null} will be applied to a {@link PreparedStatement}, if the date and time of issue is not
	 * set.
	 */
	@Test
	void applyFormattedWithoutTimestamp() throws SQLException {
		PreparedStatement statement = mock(PreparedStatement.class);
		LogEntry logEntry = new LogEntryBuilder().create();
		new DatePlaceholder(formatter, true).apply(statement, 42, logEntry);
		verify(statement).setString(42, null);
	}

}
