package org.tinylog.impl.format.pattern.placeholders;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.SqlRecord;
import org.tinylog.impl.format.pattern.SqlType;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

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
		FormatOutputRenderer renderer = new FormatOutputRenderer(new StaticTextPlaceholder("Hello World!"));
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
			.isEqualTo(new SqlRecord<>(SqlType.STRING, "Hello World!"));
	}

}
