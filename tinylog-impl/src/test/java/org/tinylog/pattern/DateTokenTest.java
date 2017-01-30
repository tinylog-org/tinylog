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

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Test;
import org.tinylog.core.LogEntryValue;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DateToken}.
 */
public final class DateTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#DATE} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		DateToken token = new DateToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.DATE);
	}

	/**
	 * Verifies that a date pattern will be output correctly.
	 */
	@Test
	public void datePattern() {
		DateToken token = new DateToken("yyyy-MM-dd");

		assertThat(render(token, LocalDateTime.of(2016, 01, 01, 00, 00))).isEqualTo("2016-01-01");
		assertThat(render(token, LocalDateTime.of(2016, 01, 01, 12, 00))).isEqualTo("2016-01-01");
		assertThat(render(token, LocalDateTime.of(2016, 01, 02, 00, 00))).isEqualTo("2016-01-02");
	}

	/**
	 * Verifies that a time pattern will be output correctly.
	 */
	@Test
	public void timePattern() {
		DateToken token = new DateToken("HH:mm:ss.SSS");

		assertThat(render(token, LocalDateTime.of(2016, 01, 01, 00, 00, 00))).isEqualTo("00:00:00.000");
		assertThat(render(token, LocalDateTime.of(2016, 01, 01, 02, 03, 04))).isEqualTo("02:03:04.000");
		assertThat(render(token, LocalDateTime.of(2016, 01, 02, 00, 00, 00))).isEqualTo("00:00:00.000");
	}

	/**
	 * Verifies that the default pattern contains all common date and time values.
	 */
	@Test
	public void defaultPattern() {
		DateToken token = new DateToken();

		assertThat(render(token, LocalDateTime.of(2016, 06, 30, 12, 00))).containsSequence("2016", "06", "30", "12", "00");
		assertThat(render(token, LocalDateTime.of(2016, 06, 30, 12, 15))).containsSequence("2016", "06", "30", "12", "15");
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
	private String render(final Token token, final LocalDateTime timestamp) {
		ZonedDateTime zonedDateTime = ZonedDateTime.of(timestamp, ZoneOffset.systemDefault());
		StringBuilder builder = new StringBuilder();
		token.render(LogEntryBuilder.empty().date(Date.from(zonedDateTime.toInstant())).create(), builder);
		return builder.toString();
	}

}
