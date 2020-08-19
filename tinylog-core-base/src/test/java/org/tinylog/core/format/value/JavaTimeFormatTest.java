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

package org.tinylog.core.format.value;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JavaTimeFormatTest {

	/**
	 * Verifies that {@link LocalTime LocalTimes} can be formatted.
	 */
	@Test
	void localTimeValue() {
		JavaTimeFormat format = new JavaTimeFormat(Locale.US, ZoneOffset.UTC);
		LocalTime time = LocalTime.of(12, 30);
		assertThat(format.isSupported(time)).isTrue();
		assertThat(format.format("HH:mm", time)).isEqualTo("12:30");
	}

	/**
	 * Verifies that {@link LocalDate LocalDates} can be formatted.
	 */
	@Test
	void localDateValue() {
		JavaTimeFormat format = new JavaTimeFormat(Locale.GERMANY, ZoneOffset.UTC);
		LocalDate date = LocalDate.of(2020, 12, 31);
		assertThat(format.isSupported(date)).isTrue();
		assertThat(format.format("dd.MM.yyyy", date)).isEqualTo("31.12.2020");
	}

	/**
	 * Verifies that {@link LocalDateTime LocalDateTimes} can be formatted.
	 */
	@Test
	void localDateTimeValue() {
		JavaTimeFormat format = new JavaTimeFormat(Locale.GERMANY, ZoneOffset.UTC);
		LocalDateTime dateTime = LocalDateTime.of(2020, 12, 31, 12, 30);
		assertThat(format.isSupported(dateTime)).isTrue();
		assertThat(format.format("dd.MM.yyyy, HH:mm", dateTime)).isEqualTo("31.12.2020, 12:30");
	}

	/**
	 * Verifies that {@link ZonedDateTime ZonedDateTimes} can be formatted.
	 */
	@Test
	void zonedDateTimeValue() {
		JavaTimeFormat format = new JavaTimeFormat(Locale.FRANCE, ZoneOffset.UTC);

		LocalDate date = LocalDate.of(2020, 12, 31);
		LocalTime time = LocalTime.of(12, 30);
		ZonedDateTime dateTime = ZonedDateTime.of(date, time, ZoneId.of("Europe/Paris"));

		assertThat(format.isSupported(dateTime)).isTrue();
		assertThat(format.format("yyyy-MM-dd HH:mm zzz", dateTime)).isEqualTo("2020-12-31 12:30 CET");
	}

	/**
	 * Verifies that {@link Instant Instants} can be formatted.
	 */
	@Test
	void instantValue() {
		JavaTimeFormat format = new JavaTimeFormat(Locale.US, ZoneOffset.UTC);
		Instant instant = Instant.ofEpochMilli(0);
		assertThat(format.isSupported(instant)).isTrue();
		assertThat(format.format("yyyy-MM-dd HH:mm", instant)).isEqualTo("1970-01-01 00:00");
	}

	/**
	 * Verifies that strings are not supported.
	 */
	@Test
	void stringValue() {
		assertThat(new JavaTimeFormat(Locale.US, ZoneOffset.UTC).isSupported("foo")).isFalse();
	}

}
