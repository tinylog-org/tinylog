package org.tinylog.impl.segments;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeSegmentTest {

	/**
	 * Verifies that the date-time segment appends the passed date-time to the passed string builder by using the
	 * provided date-time formatter.
	 */
	@Test
	void resolve() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm", Locale.ENGLISH);
		ZonedDateTime date = ZonedDateTime.ofInstant(Instant.parse("2000-01-01T12:00:00Z"), ZoneOffset.UTC);

		StringBuilder builder = new StringBuilder("bar/");
		new DateTimeSegment(formatter).resolve(builder, date);
		assertThat(builder).asString().isEqualTo("bar/2000-01-01_12-00");
	}

}
