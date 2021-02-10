package org.tinylog.impl.format.placeholder;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class SeverityCodePlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#LEVEL} is defined as required by the severity code
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		SeverityCodePlaceholder placeholder = new SeverityCodePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.LEVEL);
	}

	/**
	 * Verifies that the numeric severity level code of a log entry will be output, if the severity level set.
	 */
	@Test
	void renderWithSeverityLevel() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new SeverityCodePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
		assertThat(renderer.render(logEntry)).isEqualTo("3");
	}

	/**
	 * Verifies that "?" will be output, if the severity level is not set.
	 */
	@Test
	void renderWithoutSeverityLevel() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new SeverityCodePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("?");
	}

	/**
	 * Verifies that the numeric severity level code of a log entry will be resolved,  if the severity level is set.
	 */
	@Test
	void resolveWithSeverityLevel() {
		LogEntry logEntry = new LogEntryBuilder().severityLevel(Level.INFO).create();
		SeverityCodePlaceholder placeholder = new SeverityCodePlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.INTEGER, 3));
	}

	/**
	 * Verifies that {@code null} will be resolved, if the severity level is not set.
	 */
	@Test
	void resolveWithoutSeverityLevel() {
		LogEntry logEntry = new LogEntryBuilder().create();
		SeverityCodePlaceholder placeholder = new SeverityCodePlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.INTEGER, null));
	}

}
