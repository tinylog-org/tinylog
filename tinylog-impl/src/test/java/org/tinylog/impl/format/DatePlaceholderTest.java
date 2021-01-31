package org.tinylog.impl.format;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class DatePlaceholderTest {

	private final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneOffset.UTC);

	/**
	 * Verifies that the formatted date and time of a log entry will be output, if the timestamp is set.
	 */
	@Test
	void renderWithTimestamp() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new DatePlaceholder(formatter));
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.EPOCH).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1970-01-01T00:00:00Z");
	}

	/**
	 * Verifies that {@code <unknown>} will be output, if the timestamp is not set.
	 */
	@Test
	void renderWithoutTimestamp() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new DatePlaceholder(formatter));
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<unknown>");
	}

}
