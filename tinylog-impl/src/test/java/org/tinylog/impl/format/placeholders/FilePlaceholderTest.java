package org.tinylog.impl.format.placeholders;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class FilePlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#FILE} is defined as required by the file placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		FilePlaceholder placeholder = new FilePlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.FILE);
	}

	/**
	 * Verifies that the source file name of a log entry will be output, if set.
	 */
	@Test
	void renderWithSourceFileName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new FilePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().fileName("foo.java").create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo.java");
	}

	/**
	 * Verifies that {@code <file unknown>} will be output, if the source file name is not set.
	 */
	@Test
	void renderWithoutSourceFileName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new FilePlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<file unknown>");
	}

	/**
	 * Verifies that the source file name of a log entry will be resolved, if set.
	 */
	@Test
	void resolveWithSourceFileName() {
		LogEntry logEntry = new LogEntryBuilder().fileName("foo.java").create();
		FilePlaceholder placeholder = new FilePlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "foo.java"));
	}

	/**
	 * Verifies that {@code null} will be resolved, if the source file name is not set.
	 */
	@Test
	void resolveWithoutSourceFileName() {
		LogEntry logEntry = new LogEntryBuilder().create();
		FilePlaceholder placeholder = new FilePlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, null));
	}

}
