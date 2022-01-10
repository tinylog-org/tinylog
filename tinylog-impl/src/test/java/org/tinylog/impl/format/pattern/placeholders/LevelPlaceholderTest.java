package org.tinylog.impl.format.pattern.placeholders;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class LevelPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#LEVEL} is defined as required by the level placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		LevelPlaceholder placeholder = new LevelPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that the severity level of a log entry will be output, if set.
	 */
	@Test
	void renderWithSeverityLevel() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new LevelPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
		assertThat(renderer.render(logEntry)).isEqualTo("INFO");
	}

	/**
	 * Verifies that {@code <level unknown>} will be output, if the severity level is not set.
	 */
	@Test
	void renderWithoutSeverityLevel() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new LevelPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<level unknown>");
	}

	/**
	 * Verifies that the severity level of a log entry will be resolved, if set.
	 */
	@Test
	void resolveWithSeverityLevel() {
		LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
		LevelPlaceholder placeholder = new LevelPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "INFO"));
	}

	/**
	 * Verifies that {@code null} will be resolved, if the severity level is not set.
	 */
	@Test
	void resolveWithoutSeverityLevel() {
		LogEntry logEntry = new LogEntryBuilder().create();
		LevelPlaceholder placeholder = new LevelPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, null));
	}

}
