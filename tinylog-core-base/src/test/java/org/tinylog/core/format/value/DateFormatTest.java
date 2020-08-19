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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DateFormatTest {

	/**
	 * Verifies that {@link Date Dates} can be formatted.
	 */
	@Test
	void dateValue() {
		DateFormat format = new DateFormat(Locale.US);

		ZonedDateTime zonedDateTime = LocalDateTime.parse("2020-12-31T11:55").atZone(ZoneId.systemDefault());
		Date date = Date.from(zonedDateTime.toInstant());

		assertThat(format.isSupported(date)).isTrue();
		assertThat(format.format("MM/dd/yyyy, HH:mm", date)).isEqualTo("12/31/2020, 11:55");
	}

	/**
	 * Verifies that strings are not supported.
	 */
	@Test
	void stringValue() {
		assertThat(new DateFormat(Locale.US).isSupported("foo")).isFalse();
	}

}
