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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LegacyTimestamp}.
 */
public final class LegacyTimestampTest {

	/**
	 * Verifies that a correct {@link Date} will be returned.
	 */
	@Test
	public void convertingToDate() {
		LegacyTimestamp timestamp = create(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789));
		assertThat(timestamp.toDate()).isEqualTo(asDate(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_000_000)));
	}

	/**
	 * Verifies that a correct {@link Instant} will be returned.
	 */
	@Test
	public void convertingToInstant() {
		LegacyTimestamp timestamp = create(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789));
		assertThat(timestamp.toInstant()).isEqualTo(asInstant(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_000_000)));
	}

	/**
	 * Verifies that a correct {@link java.sql.Timestamp SQL Timestamp} will be returned.
	 */
	@Test
	public void convertingToSqlTimestamp() {
		LegacyTimestamp timestamp = create(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789));
		assertThat(timestamp.toSqlTimestamp()).isEqualTo(asSqlTimestamp(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_000_000)));
	}

	/**
	 * Verifies that the difference between two legacy timestamps can be correctly calculated in nanoseconds.
	 */
	@Test
	public void calcDifferenceInNanoseconds() {
		LegacyTimestamp first = create(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 500_000_000));
		LegacyTimestamp second = create(LocalDate.of(1985, 6, 3), LocalTime.of(14, 33, 54, 505_000_000));

		long nanoseconds = second.calcDifferenceInNanoseconds(first);
		assertThat(nanoseconds).isEqualTo((((2L * 60L + 3) * 60L + 4L) * 1000L + 5L) * 1_000_000L);
	}

	/**
	 * Creates a new legacy timestamp.
	 *
	 * @param date
	 *            Current date
	 * @param time
	 *            Current time
	 * @return Created legacy timestamp
	 */
	private static LegacyTimestamp create(final LocalDate date, final LocalTime time) {
		Instant instant = asInstant(date, time);
		return new LegacyTimestamp(instant.toEpochMilli());
	}

	/**
	 * Converts a local date and time to an {@link Instant}.
	 *
	 * @param date
	 *            Local date
	 * @param time
	 *            Local time
	 * @return Local data and time as {@link Instant}
	 */
	private static Instant asInstant(final LocalDate date, final LocalTime time) {
		return ZonedDateTime.of(date, time, ZoneId.systemDefault()).toInstant();
	}

	/**
	 * Converts a local date and time to a legacy {@link Date}.
	 *
	 * @param date
	 *            Local date
	 * @param time
	 *            Local time
	 * @return Local data and time as legacy {@link Date}
	 */
	private static Date asDate(final LocalDate date, final LocalTime time) {
		return Date.from(asInstant(date, time));
	}

	/**
	 * Converts a local date and time to an {@link java.sql.Timestamp SQL Timestamp}.
	 *
	 * @param date
	 *            Local date
	 * @param time
	 *            Local time
	 * @return Local data and time as {@link java.sql.Timestamp SQL Timestamp}
	 */
	private static java.sql.Timestamp asSqlTimestamp(final LocalDate date, final LocalTime time) {
		return java.sql.Timestamp.from(asInstant(date, time));
	}

}
