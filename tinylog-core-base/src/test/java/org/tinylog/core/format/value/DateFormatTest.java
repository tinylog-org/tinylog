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
	 * Verifies that a {@link Date} can be formatted.
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
