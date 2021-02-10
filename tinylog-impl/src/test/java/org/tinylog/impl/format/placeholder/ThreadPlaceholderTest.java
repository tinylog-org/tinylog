package org.tinylog.impl.format.placeholder;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

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
	 * Verifies that the source thread name of a log entry will be output, if the thread object is present.
	 */
	@Test
	void renderWithSourceThread() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ThreadPlaceholder());
		Thread thread = new Thread(() -> { }, "foo");
		LogEntry logEntry = new LogEntryBuilder().thread(thread).create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo");
	}

	/**
	 * Verifies that {@code <thread unknown>} will be output, if the thread object is not present.
	 */
	@Test
	void renderWithoutSourceThread() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ThreadPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<thread unknown>");
	}

	/**
	 * Verifies that the source thread name of a log entry will be resolved, if the thread object is present.
	 */
	@Test
	void resolveWithSourceThread() {
		Thread thread = new Thread(() -> { }, "foo");
		LogEntry logEntry = new LogEntryBuilder().thread(thread).create();

		ThreadPlaceholder placeholder = new ThreadPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "foo"));
	}

	/**
	 * Verifies that {@code null} will be resolved, if the thread object is not present.
	 */
	@Test
	void resolveWithoutSourceThread() {
		LogEntry logEntry = new LogEntryBuilder().create();

		ThreadPlaceholder placeholder = new ThreadPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, null));
	}

}
