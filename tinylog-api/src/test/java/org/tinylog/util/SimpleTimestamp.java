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

package org.tinylog.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.Date;

import org.tinylog.runtime.Timestamp;

/**
 * Simple timestamp for a defined day and time at the system default time zone.
 */
public final class SimpleTimestamp implements Timestamp {

	private final Instant instant;

	/**
	 * @param year
	 *            Year from {@link Year#MIN_VALUE} to {@link Year#MAX_VALUE}
	 * @param month
	 *            Month of the year from 1 (January) to 12 (December)
	 * @param day
	 *            Day of the month from 1 to 31
	 */
	public SimpleTimestamp(final int year, final int month, final int day) {
		this(year, month, day, 0, 0, 0, 0);
	}

	/**
	 * @param year
	 *            Year from {@link Year#MIN_VALUE} to {@link Year#MAX_VALUE}
	 * @param month
	 *            Month of the year from 1 (January) to 12 (December)
	 * @param day
	 *            Day of the month from 1 to 31
	 * @param hour
	 *            Hour of the day from 0 to 23
	 * @param minute
	 *            Minute of the hour from 0 to 59
	 */
	public SimpleTimestamp(final int year, final int month, final int day, final int hour, final int minute) {
		this(year, month, day, hour, minute, 0, 0);
	}

	/**
	 * @param year
	 *            Year from {@link Year#MIN_VALUE} to {@link Year#MAX_VALUE}
	 * @param month
	 *            Month of the year from 1 (January) to 12 (December)
	 * @param day
	 *            Day of the month from 1 to 31
	 * @param hour
	 *            Hour of the day from 0 to 23
	 * @param minute
	 *            Minute of the hour from 0 to 59
	 * @param second
	 *            Second of the minute from 0 to 59
	 */
	public SimpleTimestamp(final int year, final int month, final int day, final int hour, final int minute, final int second) {
		this(year, month, day, hour, minute, second, 0);
	}

	/**
	 * @param year
	 *            Year from {@link Year#MIN_VALUE} to {@link Year#MAX_VALUE}
	 * @param month
	 *            Month of the year from 1 (January) to 12 (December)
	 * @param day
	 *            Day of the month from 1 to 31
	 * @param hour
	 *            Hour of the day from 0 to 23
	 * @param minute
	 *            Minute of the hour from 0 to 59
	 * @param second
	 *            Second of the minute from 0 to 59
	 * @param nanosecond
	 *            Nanosecond of the second from 0 to 999,999,999
	 */
	public SimpleTimestamp(final int year, final int month, final int day, final int hour, final int minute, final int second,
		final int nanosecond) {
		instant = LocalDateTime.of(year, month, day, hour, minute, second, nanosecond).atZone(ZoneId.systemDefault()).toInstant();
	}

	@Override
	public Date toDate() {
		return Date.from(instant);
	}

	@Override
	public Instant toInstant() {
		return instant;
	}

	@Override
	public java.sql.Timestamp toSqlTimestamp() {
		return java.sql.Timestamp.from(instant);
	}

}
