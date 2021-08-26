package org.tinylog.impl.format.placeholders;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.LogEntryValue;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class ContextPlaceholderTest {

	/**
	 * Verifies that the log entry value {@link LogEntryValue#CONTEXT} is defined as required by the context
	 * placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ContextPlaceholder placeholder = new ContextPlaceholder("foo", null, null);
		assertThat(placeholder.getRequiredLogEntryValues()).containsExactly(LogEntryValue.CONTEXT);
	}

	/**
	 * Verifies that a thread context value of a log entry will be output, if present.
	 */
	@Test
	void renderWithContextValue() {
		ContextPlaceholder placeholder = new ContextPlaceholder("foo", "-", null);
		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").create();
		assertThat(renderer.render(logEntry)).isEqualTo("bar");
	}

	/**
	 * Verifies that the default value will be output, if a thread context value is not present.
	 */
	@Test
	void renderWithoutContextValue() {
		ContextPlaceholder placeholder = new ContextPlaceholder("foo", "-", null);
		PlaceholderRenderer renderer = new PlaceholderRenderer(placeholder);
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("-");
	}

	/**
	 * Verifies that a thread context value of a log entry will be resolved, if present.
	 */
	@Test
	void resolveWithContextValue() {
		ContextPlaceholder placeholder = new ContextPlaceholder("foo", null, "-");
		LogEntry logEntry = new LogEntryBuilder().context("foo", "bar").create();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "bar"));
	}

	/**
	 * Verifies that the default value will be resolved, if a thread context value is not present.
	 */
	@Test
	void resolveWithoutContextValue() {
		ContextPlaceholder placeholder = new ContextPlaceholder("foo", null, "-");
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "-"));
	}

}
