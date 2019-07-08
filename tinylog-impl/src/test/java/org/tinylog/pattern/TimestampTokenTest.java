/*
 * Copyright 2019 Martin Winandy
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link TimestampToken}.
 */
public class TimestampTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#DATE} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		TimestampToken token = new TimestampToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.DATE);
	}

	/**
	 * Verifies that seconds pattern will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderSecondsPattern() {
		TimestampToken token = new TimestampToken("seconds");

		assertThat(render(token, LocalDateTime.of(2016, 6, 30, 12, 0, 0, 0))).isEqualTo("1467288000");
		assertThat(render(token, LocalDateTime.of(2016, 6, 30, 12, 15, 1, 987654321))).isEqualTo("1467288901");
	}

	/**
	 * Verifies that milliseconds pattern will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderMillisecondsPattern() {
		TimestampToken token = new TimestampToken("milliseconds");

		assertThat(render(token, LocalDateTime.of(2016, 6, 30, 12, 0, 0, 0))).isEqualTo("1467288000000");
		assertThat(render(token, LocalDateTime.of(2016, 6, 30, 12, 15, 1, 987654321))).isEqualTo("1467288901987");
	}

	/**
	 * Verifies that the rendered default pattern equals timestamp in seconds.
	 */
	@Test
	public void renderDefaultPattern() {
		TimestampToken token = new TimestampToken();

		assertThat(render(token, LocalDateTime.of(2016, 6, 30, 12, 0, 0, 0))).isEqualTo("1467288000");
		assertThat(render(token, LocalDateTime.of(2016, 6, 30, 12, 15, 1, 987654321))).isEqualTo("1467288901");
	}

	/**
	 * Verifies that seconds pattern will be added correctly to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applySecondsTimestamp() throws SQLException {
		TimestampToken token = new TimestampToken("seconds");
		LocalDateTime now = LocalDateTime.now();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(now), statement, 1);
		verify(statement).setLong(1, now.atZone(ZoneOffset.UTC).toEpochSecond());
	}

	/**
	 * Verifies that milliseconds pattern will be added correctly to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyMillisecondsTimestamp() throws SQLException {
		TimestampToken token = new TimestampToken("milliseconds");
		LocalDateTime now = LocalDateTime.now();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(now), statement, 1);
		verify(statement).setLong(1, Date.from(now.atZone(ZoneOffset.UTC).toInstant()).getTime());
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
		return LogEntryBuilder.empty().date(date.atZone(ZoneOffset.UTC)).create();
	}

}
