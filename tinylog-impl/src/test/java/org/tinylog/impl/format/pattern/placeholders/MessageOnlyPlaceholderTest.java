package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

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
	 * Verifies that the log message of a log entry will be resolved, if set.
	 */
	@Test
	void resolveWithMessage() {
		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		MessageOnlyPlaceholder placeholder = new MessageOnlyPlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that {@code null} will be resolved, if the log message is not set.
	 */
	@Test
	void resolveWithoutMessage() {
		LogEntry logEntry = new LogEntryBuilder().create();
		MessageOnlyPlaceholder placeholder = new MessageOnlyPlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isNull();
	}

	/**
	 * Verifies that the log message will be output, if set.
	 */
	@Test
	void renderWithMessage() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new MessageOnlyPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().message("Hello World!").create();
		assertThat(renderer.render(logEntry)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that nothing will be output, if the log message is not set.
	 */
	@Test
	void renderWithoutMessage() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new MessageOnlyPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("");
	}

}
