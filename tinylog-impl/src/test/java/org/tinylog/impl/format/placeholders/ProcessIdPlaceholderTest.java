package org.tinylog.impl.format.placeholders;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

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
		PlaceholderRenderer renderer = new PlaceholderRenderer(new ProcessIdPlaceholder(1000));
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
			.isEqualTo(new SqlRecord<>(Types.BIGINT, 1000L));
	}

}
