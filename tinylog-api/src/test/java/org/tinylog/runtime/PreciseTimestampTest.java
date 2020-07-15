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
 * Tests for {@link PreciseTimestamp}.
 */
public final class PreciseTimestampTest {

	/**
	 * Verifies that a correct {@link Date} will be returned.
	 */
	@Test
	public void convertingToDate() {
		PreciseTimestamp timestamp = create(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789));
		assertThat(timestamp.toDate()).isEqualTo(asDate(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_000_000)));
	}

	/**
	 * Verifies that a correct {@link Instant} will be returned.
	 */
	@Test
	public void convertingToInstant() {
		PreciseTimestamp timestamp = create(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789));
		assertThat(timestamp.toInstant()).isEqualTo(asInstant(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789)));
	}

	/**
	 * Verifies that a correct {@link java.sql.Timestamp SQL Timestamp} will be returned.
	 */
	@Test
	public void convertingToSqlTimestamp() {
		PreciseTimestamp timestamp = create(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789));
		assertThat(timestamp.toSqlTimestamp()).isEqualTo(asSqlTimestamp(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789)));
	}

	/**
	 * Creates a new precise timestamp.
	 *
	 * @param date
	 *            Current date
	 * @param time
	 *            Current time
	 * @return Created precise timestamp
	 */
	private static PreciseTimestamp create(final LocalDate date, final LocalTime time) {
		Instant instant = asInstant(date, time);
		return new PreciseTimestamp(instant.toEpochMilli(), instant.getNano() % 1_000_000);
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
