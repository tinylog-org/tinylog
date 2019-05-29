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
	 * Verifies that milliseconds pattern will be rendered correctly for a {@link StringBuilder}.
	 */
	@Test
	public void renderMillisecondsTimestampPattern() {
		TimestampToken token = new TimestampToken("milliseconds");

		assertThat(render(token, LocalDateTime.of(2016, 01, 01, 00, 00))).isEqualTo("1451606400000");
		assertThat(render(token, LocalDateTime.of(2016, 01, 01, 12, 00))).isEqualTo("1451649600000");
		assertThat(render(token, LocalDateTime.of(2016, 01, 02, 00, 00))).isEqualTo("1451692800000");
	}

	/**
	 * Verifies that the rendered default pattern equals timestamp in seconds.
	 */
	@Test
	public void renderDefaultPattern() {
		TimestampToken token = new TimestampToken();

		assertThat(render(token, LocalDateTime.of(2016, 06, 30, 12, 00))).isEqualTo("1467288000");
		assertThat(render(token, LocalDateTime.of(2016, 06, 30, 12, 15))).isEqualTo("1467288900");
	}

	/**
	 * Verifies that the current time will be added as a {@link Timestamp} to a {@link PreparedStatement}.
	 *
	 * @throws SQLException
	 *             Failed to add value to prepared SQL statement
	 */
	@Test
	public void applyTimestamp() throws SQLException {
		TimestampToken token = new TimestampToken();
		LocalDateTime now = LocalDateTime.now();

		PreparedStatement statement = mock(PreparedStatement.class);
		token.apply(createLogEntry(now), statement, 1);
		verify(statement).setTimestamp(1, Timestamp.valueOf(now));
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
