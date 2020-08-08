/*
 * Copyright 2020 Martin Winandy
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

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.LogEntryValue;
import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link UptimeToken}.
 */
public class UptimeTokenTest {

	/**
	 * Verifies that {@link LogEntryValue#DATE} is the only required log entry value.
	 */
	@Test
	public void requiredLogEntryValues() {
		UptimeToken token = new UptimeToken();
		assertThat(token.getRequiredLogEntryValues()).containsOnly(LogEntryValue.DATE);
	}

	/**
	 * Verifies that the days placeholder can be resolved.
	 */
	@Test
	public void renderDays() {
		UptimeToken token = new UptimeToken("d");

		assertThat(render(token, Duration.ofDays(0))).isEqualTo("0");
		assertThat(render(token, Duration.ofDays(1))).isEqualTo("1");
		assertThat(render(token, Duration.ofDays(10))).isEqualTo("10");
		assertThat(render(token, Duration.ofDays(100))).isEqualTo("100");
	}

	/**
	 * Verifies that the hours placeholder can be resolved.
	 */
	@Test
	public void renderHours() {
		UptimeToken token = new UptimeToken("H");

		assertThat(render(token, Duration.ofHours(0))).isEqualTo("0");
		assertThat(render(token, Duration.ofHours(1))).isEqualTo("1");
		assertThat(render(token, Duration.ofHours(10))).isEqualTo("10");
		assertThat(render(token, Duration.ofHours(100))).isEqualTo("100");
		assertThat(render(token, Duration.ofHours(1000))).isEqualTo("1000");
	}

	/**
	 * Verifies that the minutes placeholder can be resolved.
	 */
	@Test
	public void renderMinutes() {
		UptimeToken token = new UptimeToken("m");

		assertThat(render(token, Duration.ofMinutes(0))).isEqualTo("0");
		assertThat(render(token, Duration.ofMinutes(1))).isEqualTo("1");
		assertThat(render(token, Duration.ofMinutes(10))).isEqualTo("10");
		assertThat(render(token, Duration.ofMinutes(100))).isEqualTo("100");
		assertThat(render(token, Duration.ofMinutes(1000))).isEqualTo("1000");
	}

	/**
	 * Verifies that the seconds placeholder can be resolved.
	 */
	@Test
	public void renderSeconds() {
		UptimeToken token = new UptimeToken("s");

		assertThat(render(token, Duration.ofSeconds(0))).isEqualTo("0");
		assertThat(render(token, Duration.ofSeconds(1))).isEqualTo("1");
		assertThat(render(token, Duration.ofSeconds(10))).isEqualTo("10");
		assertThat(render(token, Duration.ofSeconds(100))).isEqualTo("100");
		assertThat(render(token, Duration.ofSeconds(1000))).isEqualTo("1000");
	}

	/**
	 * Verifies that the fraction of second placeholder can be resolved.
	 */
	@Test
	public void renderFractionOfSecond() {
		UptimeToken token = new UptimeToken("S");

		assertThat(render(token, Duration.ofMillis(0))).isEqualTo("0");
		assertThat(render(token, Duration.ofMillis(1))).isEqualTo("0");
		assertThat(render(token, Duration.ofMillis(10))).isEqualTo("0");
		assertThat(render(token, Duration.ofMillis(100))).isEqualTo("1");
		assertThat(render(token, Duration.ofMillis(1000))).isEqualTo("10");
	}

	/**
	 * Verifies that the all supported placeholder can be resolved combined.
	 */
	@Test
	public void renderAllPlaceholdersCombined() {
		UptimeToken token = new UptimeToken("d:HH:mm:ss.SSS");

		Duration duration = Duration.ZERO;
		assertThat(render(token, duration)).isEqualTo("0:00:00:00.000");

		duration = Duration.ofDays(1).plusHours(12).plusMinutes(30).plusSeconds(55);
		assertThat(render(token, duration)).isEqualTo("1:12:30:55.000");

		duration = Duration.ofDays(90).plusHours(23).plusMinutes(59).plusSeconds(59).plusNanos(999_999_999);
		assertThat(render(token, duration)).isEqualTo("90:23:59:59.999");
	}

	/**
	 * Verifies that an entire phrase can be escaped by singe quotes.
	 */
	@Test
	public void renderEscapedPhrase() {
		UptimeToken token = new UptimeToken("d 'days'");
		Duration duration = Duration.ofDays(42);
		assertThat(render(token, duration)).isEqualTo("42 days");
	}

	/**
	 * Verifies that two consecutive single quotes are interpreted as one single quote.
	 */
	@Test
	public void renderEscapedSingleQuote() {
		UptimeToken token = new UptimeToken("HH''mm");
		Duration duration = Duration.ofHours(12);
		assertThat(render(token, duration)).isEqualTo("12'00");
	}

	/**
	 * Verifies that one single quote is output unchanged.
	 */
	@Test
	public void renderUnescapedSingleQuote() {
		UptimeToken token = new UptimeToken("HH'mm");
		Duration duration = Duration.ofHours(12);
		assertThat(render(token, duration)).isEqualTo("12'00");
	}

	private static String render(final Token token, final Duration duration) {
		StringBuilder builder = new StringBuilder();
		token.render(createLogEntry(duration), builder);
		return builder.toString();
	}

	private static LogEntry createLogEntry(final Duration duration) {
		Instant instant = RuntimeProvider.getStartTime().toInstant().plus(duration);
		ZonedDateTime date = instant.atZone(ZoneId.systemDefault());
		return LogEntryBuilder.empty().date(date).create();
	}

}
