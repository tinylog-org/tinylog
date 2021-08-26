package org.tinylog.impl.format.placeholders;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class MethodPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#METHOD} is defined as required by the method placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		MethodPlaceholder placeholder = new MethodPlaceholder();
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.METHOD);
	}

	/**
	 * Verifies that the source method name of a log entry will be output, if set.
	 */
	@Test
	void renderWithSourceMethodName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MethodPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().methodName("foo").create();
		assertThat(renderer.render(logEntry)).isEqualTo("foo");
	}

	/**
	 * Verifies that {@code <method unknown>} will be output, if the source method name is not set.
	 */
	@Test
	void renderWithoutSourceMethodName() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new MethodPlaceholder());
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("<method unknown>");
	}

	/**
	 * Verifies that the source method name of a log entry will be resolved, if set.
	 */
	@Test
	void resolveWithSourceMethodName() {
		LogEntry logEntry = new LogEntryBuilder().methodName("foo").create();
		MethodPlaceholder placeholder = new MethodPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "foo"));
	}

	/**
	 * Verifies that {@code null} will be resolved, if the source method name is not set.
	 */
	@Test
	void resolveWithoutSourceMethodName() {
		LogEntry logEntry = new LogEntryBuilder().create();
		MethodPlaceholder placeholder = new MethodPlaceholder();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, null));
	}

}
