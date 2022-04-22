package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.ValueType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#THREAD} is defined as required by the thread placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ThreadPlaceholder placeholder = new ThreadPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.THREAD);
	}

	/**
	 * Verifies that the source thread name of a log entry will be resolved, if the thread object is present.
	 */
	@Test
	void resolveWithSourceThread() {
		Thread thread = new Thread(() -> { }, "foo");
		LogEntry logEntry = new LogEntryBuilder().thread(thread).create();

		ThreadPlaceholder placeholder = new ThreadPlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isEqualTo("foo");
	}

	/**
	 * Verifies that {@code null} will be resolved, if the thread object is not present.
	 */
	@Test
	void resolveWithoutSourceThread() {
		LogEntry logEntry = new LogEntryBuilder().create();

		ThreadPlaceholder placeholder = new ThreadPlaceholder();
		assertThat(placeholder.getType()).isEqualTo(ValueType.STRING);
		assertThat(placeholder.getValue(logEntry)).isNull();
	}

	/**
	 * Verifies that the source thread name of a log entry will be output, if the thread object is present.
	 */
	@Test
	void renderWithSourceThread() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new ThreadPlaceholder());
		Thread thread = new Thread(() -> { }, "foo");
		LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo");
	}

	/**
	 * Verifies that {@code <thread unknown>} will be output, if the thread object is not present.
	 */
	@Test
	void renderWithoutSourceThread() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new ThreadPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<thread unknown>");
	}

}
