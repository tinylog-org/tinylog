/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.runtime;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link FastTimestampFormatter}.
 */
public final class FastTimestampFormatterTest {

	/**
	 * Verifies that timestamps with nanosecond precision are not supported.
	 */
	@Test
	public void notSupportPreciseTimestamps() {
		FastTimestampFormatter formatter = new FastTimestampFormatter("hh:mm:ss", Locale.US);
		assertThat(formatter.isPrecise()).isFalse();
	}

	/**
	 * Verifies that a valid formatted timestamp will be accepted.
	 */
	@Test
	public void acceptValidFormattedTimestamp() {
		FastTimestampFormatter formatter = new FastTimestampFormatter("hh:mm:ss.SSS", Locale.US);
		assertThat(formatter.isValid("12:30:55.999")).isTrue();
	}

	/**
	 * Verifies that an invalid formatted timestamp will be refused.
	 */
	@Test
	public void refuseInvalidFormattedTimestamp() {
		FastTimestampFormatter formatter = new FastTimestampFormatter("hh:mm:ss.SSS", Locale.US);
		assertThat(formatter.isValid("1985-06-03")).isFalse();
	}

	/**
	 * Verifies that timestamps can be formatted minute precise.
	 */
	@Test
	public void minutePrecision() {
		FastTimestampFormatter formatter = new FastTimestampFormatter("yyyy-MM-dd hh:mm", Locale.US);

		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 55, 000_000_000), formatter)).isEqualTo("2016-02-01 12:30");
		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 55, 999_000_000), formatter)).isEqualTo("2016-02-01 12:30");
		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 56, 000_000_000), formatter)).isEqualTo("2016-02-01 12:30");
	}

	/**
	 * Verifies that timestamps can be formatted second precise.
	 */
	@Test
	public void secondPrecision() {
		FastTimestampFormatter formatter = new FastTimestampFormatter("yyyy-MM-dd hh:mm:ss", Locale.US);

		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 55, 000_000_000), formatter)).isEqualTo("2016-02-01 12:30:55");
		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 55, 999_000_000), formatter)).isEqualTo("2016-02-01 12:30:55");
		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 56, 000_000_000), formatter)).isEqualTo("2016-02-01 12:30:56");
	}

	/**
	 * Verifies that timestamps can be formatted millisecond precise.
	 */
	@Test
	public void millisecondPrecision() {
		FastTimestampFormatter formatter = new FastTimestampFormatter("hh:mm:ss.SSS", Locale.US);

		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 55, 000_000_000), formatter)).isEqualTo("12:30:55.000");
		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 55, 999_000_000), formatter)).isEqualTo("12:30:55.999");
		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 56, 000_000_000), formatter)).isEqualTo("12:30:56.000");
	}

	/**
	 * Formats a {@link LocalDateTime} by using a given formatter.
	 *
	 * @param date
	 *            Date to format
	 * @param formatter
	 *            Formatter for formatting the given date
	 * @return Formatted date
	 */
	private String format(final LocalDateTime date, final FastTimestampFormatter formatter) {
		Instant instant = date.atZone(ZoneId.systemDefault()).toInstant();

		Timestamp timestamp = mock(Timestamp.class);
		when(timestamp.toDate()).thenReturn(Date.from(instant));

		return formatter.format(timestamp);
	}

}
