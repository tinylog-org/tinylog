/*
 * Copyright 2016 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.pattern;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link DateToken}.
 */
public final class DateTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#DATE_WITH_MILLISECOND_PRECISION} is the only required log entry value by
	 * default.
	 */
	@Test
	public void defaultRequiredLogEntryValues() {
		DateToken token = new DateToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.DATE_WITH_MILLISECOND_PRECISION);
	}

	/**
	 * Verifies that {@link LogEntryValue#DATE_WITH_MILLISECOND_PRECISION} is the only required log entry value for a
	 * date pattern that requires only millisecond precision.
	 */
	@Test
	public void requiredLogEntryValuesForMillisecondPrecision() {
		DateToken token = new DateToken("yyyy-MM-dd HH:mm:ss.SSS");
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.DATE_WITH_MILLISECOND_PRECISION);
	}

	/**
	 * Verifies that {@link LogEntryValue#DATE_WITH_NANOSECOND_PRECISION} is the only required log entry value for a
	 * date pattern that requires nanosecond precision.
	 */
	@Test
	public void requiredLogEntryValuesForNanosecondPrecision() {
		DateToken token = new DateToken("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.DATE_WITH_NANOSECOND_PRECISION);
	}

	/**
	 * Verifies that a date pattern will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderDatePattern() {
		DateToken token = new DateToken("yyyy-MM-dd");

		assertThat(render(token, LocalDateTime.of(2016, 01, 01, 00, 00))).isEqualTo("2016-01-01");
		assertThat(render(token, LocalDateTime.of(2016, 01, 01, 12, 00))).isEqualTo("2016-01-01");
		assertThat(render(token, LocalDateTime.of(2016, 01, 02, 00, 00))).isEqualTo("2016-01-02");
	}

	/**
	 * Verifies that a time pattern will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderTimePattern() {
		DateToken token = new DateToken("HH:mm:ss.SSS");

		assertThat(render(token, LocalDateTime.of(2016, 01, 01, 00, 00, 00))).isEqualTo("00:00:00.000");
		assertThat(render(token, LocalDateTime.of(2016, 01, 01, 02, 03, 04))).isEqualTo("02:03:04.000");
		assertThat(render(token, LocalDateTime.of(2016, 01, 02, 00, 00, 00))).isEqualTo("00:00:00.000");
	}

	/**
	 * Verifies that the rendered default pattern contains all common date and time values.
	 */
	@Test
	public void renderDefaultPattern() {
		DateToken token = new DateToken();

		assertThat(render(token, LocalDateTime.of(2016, 06, 30, 12, 00))).containsSubsequence("2016", "06", "30", "12", "00");
		assertThat(render(token, LocalDateTime.of(2016, 06, 30, 12, 15))).containsSubsequence("2016", "06", "30", "12", "15");
	}

	/**
	 * Verifies that the current date and time will be added as a {@link Timestamp} to a {@link PreparedStatement}, if
	 * no format pattern has been explicitly defined.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyTimestamp() throws SQLException {
		DateToken token = new DateToken();
		LocalDateTime now = LocalDateTime.now();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(now), statement, 1);
		verify(statement).setTimestamp(1, Timestamp.valueOf(now));
	}

	/**
	 * Verifies that the current date and time be added as a formatted {@link String} to a {@link PreparedStatement}, if
	 * a format pattern has been defined.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyString() throws SQLException {
		DateToken token = new DateToken("yyyy-MM-dd HH:mm");

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(LocalDateTime.of(2016, 06, 30, 12, 15)), statement, 1);
		verify(statement).setString(1, "2016-06-30 12:15");
	}

	/**
	 * Renders a token.
	 *
	 * @param token
	 *            Token to render
	 * @param timestamp
	 *            Date and time of issue for log entry
	 * @return Result text
	 */
	private static String render(final Token token, final LocalDateTime timestamp) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(timestamp), builder);
		return builder.toString();
	}

	/**
	 * Creates a log entry that contains a date.
	 *
	 * @param date
	 *            Date for log entry
	 * @return Filled log entry
	 */
	private static LogEntry createLogEntry(final LocalDateTime date) {
		return LogEntryBuilder.empty().date(date).create();
	}

}
