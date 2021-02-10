package org.tinylog.impl.format.placeholder;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class LinePlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#LINE} is defined as required by the line placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		LinePlaceholder placeholder = new LinePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.LINE);
	}

	/**
	 * Verifies that the line number of a log entry in the source file will be output, if set.
	 */
	@Test
	void renderWithSourceLineName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new LinePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().lineNumber(100).create();
		assertThat(renderer.render(logEntry)).isEqualTo("100");
	}

	/**
	 * Verifies that "?" will be output, if the line number in the source file is not set.
	 */
	@Test
	void renderWithoutSourceLineName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new LinePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("?");
	}

	/**
	 * Verifies that the line number of a log entry in the source file will be resolved, if set.
	 */
	@Test
	void resolveWithSourceLineName() {
		LogEntry logEntry = new LogEntryBuilder().lineNumber(100).create();
		LinePlaceholder placeholder = new LinePlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.INTEGER, 100));
	}

	/**
	 * Verifies that {@code null} will be resolved, if the line number in the source file is not set.
	 */
	@Test
	void resolveWithoutSourceLineName() {
		LogEntry logEntry = new LogEntryBuilder().create();
		LinePlaceholder placeholder = new LinePlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.INTEGER, null));
	}

}
