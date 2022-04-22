package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.SqlType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadIdPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#THREAD} is defined as required by the thread ID
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ThreadIdPlaceholder placeholder = new ThreadIdPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.THREAD);
	}

	/**
	 * Verifies that the source thread ID of a log entry will be output, if the thread object is present.
	 */
	@Test
	void renderWithSourceThread() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new ThreadIdPlaceholder());
		Thread thread = new Thread(() -> { });
		LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
		assertThat(renderer.render(logEntry)).isEqualTo(Long.toString(thread.getId()));
	}

	/**
	 * Verifies that "?" will be output, if the thread object is not present.
	 */
	@Test
	void renderWithoutSourceThread() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new ThreadIdPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("?");
	}

	/**
	 * Verifies that the source thread ID of a log entry will be resolved, if the thread object is present.
	 */
	@Test
	void resolveWithSourceThread() {
		Thread thread = new Thread(() -> { });
		LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
		ThreadIdPlaceholder placeholder = new ThreadIdPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(SqlType.LONG, thread.getId()));
	}

	/**
	 * Verifies that {@code null} will be resolved, if the thread object is not present.
	 */
	@Test
	void resolveWithoutSourceThread() {
		LogEntry logEntry = new LogEntryBuilder().create();
		ThreadIdPlaceholder placeholder = new ThreadIdPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(SqlType.LONG, null));
	}

}
