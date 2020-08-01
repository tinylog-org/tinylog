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

package org.tinylog.core.runtime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModernTimestampFormatterTest {

	/**
	 * Verifies that timestamps can be formatted and cached as nanosecond of the second.
	 */
	@Test
	public void nanosecondOfSecond() {
		ModernTimestampFormatter formatter = new ModernTimestampFormatter("n", Locale.US);

		String cached = formatter.format(parseTimestamp("2020-01-01T12:00:00.000000000"));
		assertThat(cached).isEqualTo("0");

		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.000000000"))).isSameAs(cached);
		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.000000001"))).isEqualTo("1");
	}

	/**
	 * Verifies that timestamps can be formatted and cached as nanosecond of the day.
	 */
	@Test
	public void nanosecondOfDay() {
		ModernTimestampFormatter formatter = new ModernTimestampFormatter("N", Locale.US);

		String cached = formatter.format(parseTimestamp("2020-01-01T12:00:00.000000000"));
		assertThat(cached).isEqualTo("43200000000000");

		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.000000000"))).isSameAs(cached);
		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.000000001"))).isEqualTo("43200000000001");
	}

	/**
	 * Verifies that timestamps can be formatted and cached with nanosecond precision.
	 */
	@Test
	public void nanosecondPrecision() {
		ModernTimestampFormatter formatter = new ModernTimestampFormatter("HH:mm:ss.SSSSSSSSS", Locale.US);

		String cached = formatter.format(parseTimestamp("2020-01-01T12:00:00.000000000"));
		assertThat(cached).isEqualTo("12:00:00.000000000");

		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.000000000"))).isSameAs(cached);
		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.000000001"))).isEqualTo("12:00:00.000000001");
	}

	/**
	 * Verifies that timestamps can be formatted and cached with microsecond precision.
	 */
	@Test
	public void microsecondPrecision() {
		ModernTimestampFormatter formatter = new ModernTimestampFormatter("HH:mm:ss.SSSSSS", Locale.US);

		String cached = formatter.format(parseTimestamp("2020-01-01T12:00:00.000000000"));
		assertThat(cached).isEqualTo("12:00:00.000000");

		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.000000999"))).isSameAs(cached);
		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.000001000"))).isEqualTo("12:00:00.000001");
	}

	/**
	 * Verifies that timestamps can be formatted and cached with millisecond precision.
	 */
	@Test
	public void millisecondPrecision() {
		ModernTimestampFormatter formatter = new ModernTimestampFormatter("HH:mm:ss.SSS", Locale.US);

		String cached = formatter.format(parseTimestamp("2020-01-01T12:00:00.000000"));
		assertThat(cached).isEqualTo("12:00:00.000");

		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.000999"))).isSameAs(cached);
		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.001000"))).isEqualTo("12:00:00.001");
	}

	/**
	 * Verifies that timestamps can be formatted and cached with second precision.
	 */
	@Test
	public void secondPrecision() {
		ModernTimestampFormatter formatter = new ModernTimestampFormatter("HH:mm:ss", Locale.US);

		String cached = formatter.format(parseTimestamp("2020-01-01T12:00:00.000"));
		assertThat(cached).isEqualTo("12:00:00");

		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:00.000"))).isSameAs(cached);
		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:01.000"))).isEqualTo("12:00:01");
	}

	/**
	 * Verifies that timestamps can be formatted and cached with minute precision.
	 */
	@Test
	public void minutePrecision() {
		ModernTimestampFormatter formatter = new ModernTimestampFormatter("HH:mm", Locale.US);

		String cached = formatter.format(parseTimestamp("2020-01-01T12:00:00"));
		assertThat(cached).isEqualTo("12:00");

		assertThat(formatter.format(parseTimestamp("2020-01-01T12:00:59"))).isSameAs(cached);
		assertThat(formatter.format(parseTimestamp("2020-01-01T12:01:00"))).isEqualTo("12:01");
	}

	/**
	 * Parses an ISO local date time as timestamp.
	 *
	 * @param formattedDate Formatted ISO local date time
	 * @return Parsed timestamp
	 */
	private ModernTimestamp parseTimestamp(String formattedDate) {
		ZonedDateTime zonedDateTime = LocalDateTime.parse(formattedDate).atZone(ZoneId.systemDefault());
		return new ModernTimestamp(zonedDateTime.toInstant());
	}

}
