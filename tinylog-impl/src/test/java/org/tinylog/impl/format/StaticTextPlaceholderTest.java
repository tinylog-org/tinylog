package org.tinylog.impl.format;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.test.LogEntryBuilder;
import org.tinylog.impl.test.PlaceholderRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class StaticTextPlaceholderTest {

	/**
	 * Verifies that the passed static text is output unchanged.
	 */
	@Test
	void render() {
		PlaceholderRenderer renderer = new PlaceholderRenderer(new StaticTextPlaceholder("Hello World!"));
		LogEntry logEntry = new LogEntryBuilder().create();
		assertThat(renderer.render(logEntry)).isEqualTo("Hello World!");
	}

}
