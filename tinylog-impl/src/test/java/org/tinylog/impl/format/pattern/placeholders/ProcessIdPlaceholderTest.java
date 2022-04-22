package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.SqlType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessIdPlaceholderTest {

	/**
	 * Verifies that no log entry values are defined as required by the process ID placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		ProcessIdPlaceholder placeholder = new ProcessIdPlaceholder(42);
		assertThat(placeholder.getRequiredLogEntryValues()).isEmpty();
	}

	/**
	 * Verifies that the passed process ID is output.
	 */
	@Test
	void render() {
		FormatOutputRenderer renderer = new FormatOutputRenderer(new ProcessIdPlaceholder(1000));
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("1000");
	}

	/**
	 * Verifies that the passed process ID is resolved.
	 */
	@Test
	void resolve() {
		LogEntry logEntry = new LogEntryBuilder().create();
		ProcessIdPlaceholder placeholder = new ProcessIdPlaceholder(1000);
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(SqlType.LONG, 1000L));
	}

}
