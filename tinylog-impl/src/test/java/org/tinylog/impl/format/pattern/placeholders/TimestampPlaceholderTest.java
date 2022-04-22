package org.tinylog.impl.format.pattern.placeholders;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class TimestampPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#TIMESTAMP} is defined as required by the timestamp
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		TimestampPlaceholder placeholder = new TimestampPlaceholder(Instant::toEpochMilli);
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.TIMESTAMP);
	}

	/**
	 * Verifies that the timestamp of issue of a log entry will be resolved, if present.
	 */
	@Test
	void resolveWithTimestamp() {
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1000)).create();
		TimestampPlaceholder placeholder = new TimestampPlaceholder(Instant::toEpochMilli);
		assertThat(placeholder.getType()).isEqualTo(ValueType.LONG);
		assertThat(placeholder.getValue(logEntry)).isEqualTo(1000L);
	}

	/**
	 * Verifies that {@code null} will be resolved, if the timestamp is not present.
	 */
	@Test
	void resolveWithoutTimestamp() {
		LogEntry logEntry = new LogEntryBuilder().create();
		TimestampPlaceholder placeholder = new TimestampPlaceholder(Instant::toEpochMilli);
		assertThat(placeholder.getType()).isEqualTo(ValueType.LONG);
		assertThat(placeholder.getValue(logEntry)).isEqualTo(null);
	}

	/**
	 * Verifies that the timestamp of issue of a log entry will be output, if present.
	 */
	@Test
	void renderWithTimestamp() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new TimestampPlaceholder(Instant::toEpochMilli));
		LogEntry logEntry = new LogEntryBuilder().timestamp(Instant.ofEpochMilli(1000)).create();
		assertThat(renderer.render(logEntry)).isEqualTo("1000");
	}

	/**
	 * Verifies that {@code <timestamp unknown>} will be output, if the timestamp is not present.
	 */
	@Test
	void renderWithoutTimestamp() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new TimestampPlaceholder(Instant::toEpochMilli));
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<timestamp unknown>");
	}

}
