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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link PreciseTimestampFormatter}.
 */
public final class PreciseTimestampFormatterTest {

	/**
	 * Verifies that timestamps with nanosecond precision are supported.
	 */
	@Test
	public void supportPreciseTimestamps() {
		PreciseTimestampFormatter formatter = new PreciseTimestampFormatter("n", Locale.US);
		assertThat(formatter.isPrecise()).isTrue();
	}

	/**
	 * Verifies that a valid formatted timestamp will be accepted.
	 */
	@Test
	public void acceptValidFormattedTimestamp() {
		PreciseTimestampFormatter formatter = new PreciseTimestampFormatter("hh:mm:ss.SSSSSSSSS", Locale.US);
		assertThat(formatter.isValid("12:30:55.999999999")).isTrue();
	}

	/**
	 * Verifies that an invalid formatted timestamp will be refused.
	 */
	@Test
	public void refuseInvalidFormattedTimestamp() {
		PreciseTimestampFormatter formatter = new PreciseTimestampFormatter("hh:mm:ss.SSSSSSSSS", Locale.US);
		assertThat(formatter.isValid("1985-06-03")).isFalse();
	}

	/**
	 * Verifies that timestamps can be formatted minute precise.
	 */
	@Test
	public void minutePrecision() {
		PreciseTimestampFormatter formatter = new PreciseTimestampFormatter("yyyy-MM-dd hh:mm", Locale.US);

		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 55, 000_000_000), formatter)).isEqualTo("2016-02-01 12:30");
		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 55, 999_000_000), formatter)).isEqualTo("2016-02-01 12:30");
		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 56, 000_000_000), formatter)).isEqualTo("2016-02-01 12:30");
	}

	/**
	 * Verifies that timestamps can be formatted nanosecond precise.
	 */
	@Test
	public void nanosecondPrecision() {
		PreciseTimestampFormatter formatter = new PreciseTimestampFormatter("hh:mm:ss.SSSSSSSSS", Locale.US);

		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 55, 000_000_000), formatter)).isEqualTo("12:30:55.000000000");
		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 55, 999_999_999), formatter)).isEqualTo("12:30:55.999999999");
		assertThat(format(LocalDateTime.of(2016, 02, 01, 12, 30, 56, 000_000_000), formatter)).isEqualTo("12:30:56.000000000");
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
	private String format(final LocalDateTime date, final PreciseTimestampFormatter formatter) {
		Instant instant = date.atZone(ZoneId.systemDefault()).toInstant();

		Timestamp timestamp = mock(Timestamp.class);
		when(timestamp.toInstant()).thenReturn(instant);

		return formatter.format(timestamp);
	}

}
