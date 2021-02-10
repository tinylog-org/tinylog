package org.tinylog.impl.format.placeholder;

import java.sql.Types;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.SqlRecord;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class StaticTextPlaceholderTest {

	/**
	 * Verifies that none log entry values are defined as required by the static text placeholder.
	 */
	@Test
	void requiredLogEntryValues() {
		StaticTextPlaceholder placeholder = new StaticTextPlaceholder("Hello World!");
		assertThat(placeholder.getRequiredLogEntryValues()).isEmpty();
	}

	/**
	 * Verifies that the passed static text is output unchanged.
	 */
	@Test
	void render() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new StaticTextPlaceholder("Hello World!"));
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("Hello World!");
	}

	/**
	 * Verifies that the passed static text is resolved correctly.
	 */
	@Test
	void resolve() {
		LogEntry logEntry = new LogEntryBuilder().create();
		StaticTextPlaceholder placeholder = new StaticTextPlaceholder("Hello World!");
		assertThat(placeholder.resolve(logEntry))
			.usingRecursiveComparison()
			.isEqualTo(new SqlRecord<>(Types.VARCHAR, "Hello World!"));
	}

}
