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

package org.tinylog.core.formats;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JodaTimeFormatTest {


	/**
	 * Verifies that {@link LocalTime LocalTimes} can be formatted.
	 */
	@Test
	void localTimeValue() {
		JodaTimeFormat format = new JodaTimeFormat(Locale.US, DateTimeZone.UTC);
		LocalTime time = new LocalTime(12, 30);
		assertThat(format.isSupported(time)).isTrue();
		assertThat(format.format("HH:mm", time)).isEqualTo("12:30");
	}

	/**
	 * Verifies that {@link LocalDate LocalDates} can be formatted.
	 */
	@Test
	void localDateValue() {
		JodaTimeFormat format = new JodaTimeFormat(Locale.GERMANY, DateTimeZone.UTC);
		LocalDate date = new LocalDate(2020, 12, 31);
		assertThat(format.isSupported(date)).isTrue();
		assertThat(format.format("dd.MM.yyyy", date)).isEqualTo("31.12.2020");
	}

	/**
	 * Verifies that {@link LocalDateTime LocalDateTimes} can be formatted.
	 */
	@Test
	void localDateTimeValue() {
		JodaTimeFormat format = new JodaTimeFormat(Locale.GERMANY, DateTimeZone.UTC);
		LocalDateTime dateTime = new LocalDateTime(2020, 12, 31, 12, 30);
		assertThat(format.isSupported(dateTime)).isTrue();
		assertThat(format.format("dd.MM.yyyy, HH:mm", dateTime)).isEqualTo("31.12.2020, 12:30");
	}

	/**
	 * Verifies that {@link DateTime DateTimes} can be formatted.
	 */
	@Test
	void dateTimeValue() {
		JodaTimeFormat format = new JodaTimeFormat(Locale.FRANCE, DateTimeZone.UTC);

		DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
		DateTime dateTime = new DateTime(2020, 12, 31, 12, 30, zone);

		assertThat(format.isSupported(dateTime)).isTrue();
		assertThat(format.format("yyyy-MM-dd HH:mm zzz", dateTime)).isEqualTo("2020-12-31 12:30 CET");
	}

	/**
	 * Verifies that {@link Instant Instants} can be formatted.
	 */
	@Test
	void instantValue() {
		JodaTimeFormat format = new JodaTimeFormat(Locale.US, DateTimeZone.UTC);
		Instant instant = Instant.ofEpochMilli(0);
		assertThat(format.isSupported(instant)).isTrue();
		assertThat(format.format("yyyy-MM-dd HH:mm", instant)).isEqualTo("1970-01-01 00:00");
	}

	/**
	 * Verifies that strings are not supported.
	 */
	@Test
	void stringValue() {
		assertThat(new JodaTimeFormat(Locale.US, DateTimeZone.UTC).isSupported("foo")).isFalse();
	}

}
