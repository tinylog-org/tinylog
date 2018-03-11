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
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Tests for {@link FastTimestamp}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FastTimestamp.class)
public final class FastTimestampTest {

	/**
	 * Verifies that a correct {@link Date} will be returned.
	 *
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public void convertingToDate() throws Exception {
		setTime(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789));
		FastTimestamp fastTimestamp = new FastTimestamp();

		setTime(LocalDate.now(), LocalTime.now());
		assertThat(fastTimestamp.toDate()).isEqualTo(asDate(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_000_000)));
	}

	/**
	 * Verifies that a correct {@link Instant} will be returned.
	 *
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public void convertingToInstant() throws Exception {
		setTime(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789));
		FastTimestamp fastTimestamp = new FastTimestamp();

		setTime(LocalDate.now(), LocalTime.now());
		assertThat(fastTimestamp.toInstant()).isEqualTo(asInstant(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_000_000)));
	}

	/**
	 * Verifies that a correct {@link java.sql.Timestamp SQL Timestamp} will be returned.
	 *
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public void convertingToSqlTimestamp() throws Exception {
		setTime(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_456_789));
		FastTimestamp fastTimestamp = new FastTimestamp();

		setTime(LocalDate.now(), LocalTime.now());
		assertThat(fastTimestamp.toSqlTimestamp())
			.isEqualTo(asSqlTimestamp(LocalDate.of(1985, 6, 3), LocalTime.of(12, 30, 50, 123_000_000)));
	}

	/**
	 * Sets the current date and time.
	 *
	 * @param date
	 *            New current date
	 * @param time
	 *            New current time
	 * @throws Exception
	 *             Failed mocking {@link Date}
	 */
	private static void setTime(final LocalDate date, final LocalTime time) throws Exception {
		long milliseconds = asInstant(date, time).toEpochMilli();
		Date newDate = new Date(milliseconds);
		whenNew(Date.class).withNoArguments().thenReturn(newDate);
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
