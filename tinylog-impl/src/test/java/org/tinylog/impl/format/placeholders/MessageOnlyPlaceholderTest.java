package org.tinylog.impl.format.placeholders;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class MessageOnlyPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#MESSAGE} is defined as required by the message only
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		MessageOnlyPlaceholder placeholder = new MessageOnlyPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.MESSAGE);
	}

	/**
	 * Verifies that the log message will be output, if set.
	 */
	@Test
	void renderWithMessage() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MessageOnlyPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(renderer.render(logEntry)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that nothing will be output, if the log message is not set.
	 */
	@Test
	void renderWithoutMessage() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MessageOnlyPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("");
	}

	/**
	 * Verifies that the log message of a log entry will be resolved, if set.
	 */
	@Test
	void resolveWithMessage() {
		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		MessageOnlyPlaceholder placeholder = new MessageOnlyPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.LONGVARCHAR, "Hello World!"));
	}

	/**
	 * Verifies that {@code null} will be resolved, if the log message is not set.
	 */
	@Test
	void resolveWithoutMessage() {
		LogEntry logEntry = new LogEntryBuilder().create();
		MessageOnlyPlaceholder placeholder = new MessageOnlyPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.LONGVARCHAR, null));
	}

}
